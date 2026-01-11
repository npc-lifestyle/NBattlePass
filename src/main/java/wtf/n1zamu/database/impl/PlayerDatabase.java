package wtf.n1zamu.database.impl;

import lombok.Getter;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

import org.bukkit.Bukkit;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.database.Database;

import wtf.n1zamu.quest.enums.RewardType;

import java.io.File;
import java.sql.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class PlayerDatabase extends Database {
    @Getter
    private Connection connection;
    private final Map<String, PlayerStats> cache = new ConcurrentHashMap<>();
    private final ExecutorService asyncExecutor;
    private final File dataFile;

    public PlayerDatabase() {
        this.dataFile = new File(NBattlePass.getInstance().getDataFolder(), "players.db");
        this.asyncExecutor = Executors.newSingleThreadExecutor();
        connect();
        loadCache();
    }

    public void connect() {
        try {
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile.getAbsolutePath());

            try (Statement smt = connection.createStatement()) {
                smt.execute("PRAGMA journal_mode=WAL;");
                smt.execute("PRAGMA synchronous=NORMAL;");

                String sql = "CREATE TABLE IF NOT EXISTS players (" +
                        "player TEXT PRIMARY KEY, " +
                        "rewardType TEXT, " +
                        "currentExp INTEGER, " +
                        "currentLvl INTEGER, " +
                        "taken INTEGER)";
                smt.executeUpdate(sql);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Database connection error", e);
        }
    }

    private void loadCache() {
        String query = "SELECT * FROM players";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("player");
                PlayerStats stats = new PlayerStats(
                        rs.getString("rewardType"),
                        rs.getInt("currentExp"),
                        rs.getInt("currentLvl"),
                        rs.getInt("taken")
                );
                cache.put(name, stats);
            }
            Bukkit.getLogger().info("[NBattlePass] Cache loaded: " + cache.size() + " players.");
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to load cache", e);
        }
    }

    public int getExp(String name) {
        PlayerStats stats = cache.get(name);
        return stats != null ? stats.currentExp : 0;
    }

    public void setExp(String name, int exp) {
        cache.compute(name, (k, v) -> {
            if (v == null) return new PlayerStats(RewardType.DEFAULT.name(), exp, 0, 0);
            v.currentExp = exp;
            return v;
        });
        saveAsync(name);
    }

    public void upLevel(String name, int newLevel) {
        cache.compute(name, (k, v) -> {
            if (v == null) return new PlayerStats(RewardType.DEFAULT.name(), 0, newLevel, 0);
            v.currentLvl = newLevel;
            v.taken = 0;
            return v;
        });
        saveAsync(name);
    }

    public void addNewPlayer(String name) {
        cache.putIfAbsent(name, new PlayerStats(RewardType.DEFAULT.name(), 0, 0, 0));
        saveAsync(name);
    }

    public void giveSubscribe(String name) {
        User user = LuckPermsProvider.get().getUserManager().getUser(name);
        if (user != null) {
            user.data().add(Node.builder("nbattlepass.subscribe").build());
            LuckPermsProvider.get().getUserManager().saveUser(user);
        }

        cache.compute(name, (k, v) -> {
            if (v == null) {
                return new PlayerStats(RewardType.SUBSCRIBE.name(), 0, 0, 0);
            }
            v.rewardType = RewardType.SUBSCRIBE.name();
            return v;
        });
        saveAsync(name);
    }

    public Map<String, Integer> getPlayerExp() {
        Map<String, Integer> map = new ConcurrentHashMap<>();
        cache.forEach((name, stats) -> map.put(name, stats.currentExp));
        return map;
    }

    public void clear() {
        cache.clear();
        asyncExecutor.execute(() -> {
            String drop = "DROP TABLE IF EXISTS players";
            String create = "CREATE TABLE IF NOT EXISTS players (" +
                    "player TEXT PRIMARY KEY, " +
                    "rewardType TEXT, " +
                    "currentExp INTEGER, " +
                    "currentLvl INTEGER, " +
                    "taken INTEGER)";
            try (Statement smt = connection.createStatement()) {
                smt.executeUpdate(drop);
                smt.executeUpdate(create);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void saveAsync(String name) {
        PlayerStats stats = cache.get(name);
        if (stats == null) return;

        asyncExecutor.execute(() -> {
            String sql = "INSERT OR REPLACE INTO players(player, rewardType, currentExp, currentLvl, taken) VALUES(?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, stats.rewardType);
                ps.setInt(3, stats.currentExp);
                ps.setInt(4, stats.currentLvl);
                ps.setInt(5, stats.taken);
                ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Async save failed for " + name + ": " + e.getMessage());
            }
        });
    }

    public void disconnect() {
        asyncExecutor.shutdown();
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class PlayerStats {
        volatile String rewardType;
        volatile int currentExp;
        volatile int currentLvl;
        volatile int taken;

        public PlayerStats(String rewardType, int currentExp, int currentLvl, int taken) {
            this.rewardType = rewardType;
            this.currentExp = currentExp;
            this.currentLvl = currentLvl;
            this.taken = taken;
        }
    }
}