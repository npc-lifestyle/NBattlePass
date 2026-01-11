package wtf.n1zamu.database.impl.quest;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.database.Database;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestTime;
import wtf.n1zamu.quest.enums.QuestType;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class QuestDatabase extends Database {

    private static final ExecutorService DB_EXECUTOR =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final Cache<String, Map<QuestKey, QuestObject>> cache =
            CacheBuilder.newBuilder()
                    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                    .initialCapacity(10_000)
                    .maximumSize(100_000)
                    .expireAfterAccess(30, TimeUnit.MINUTES)
                    .build();

    private Connection connection;
    private final File dataFile;

    public QuestDatabase() {
        this.dataFile = new File(NBattlePass.getInstance().getDataFolder(), "quests.db");
    }

    public void connect() {
        try {
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile.getAbsolutePath());
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS quests (
                          player TEXT NOT NULL,
                          currentExp INTEGER NOT NULL,
                          needExp INTEGER NOT NULL,
                          state TEXT NOT NULL,
                          time TEXT NOT NULL,
                          type TEXT NOT NULL,
                          object TEXT NOT NULL,
                          PRIMARY KEY (player, time, type, object)
                        );
                        """);
                st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_player ON quests(player);");
            }
            preloadCache();
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    @Override
    protected void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    @Override
    protected Connection getConnection() {
        return connection;
    }



    public List<QuestObject> getQuests(String player, QuestTime time) {
        Map<QuestKey, QuestObject> map = cache.getIfPresent(player);
        if (map != null) {
            return map.values().stream()
                    .filter(q -> q.getTime() == time)
                    .toList();
        }

        Map<QuestKey, QuestObject> loaded = new ConcurrentHashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM quests WHERE player = ? AND time = ?")) {
            ps.setString(1, player);
            ps.setString(2, time.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuestObject q = QuestMapper.from(rs);
                    loaded.put(QuestKey.from(q), q);
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }

        cache.put(player, loaded);
        return new ArrayList<>(loaded.values());
    }

    public void updateProgress(String player, QuestObject quest, int progress) {
        quest.setCurrentExp(progress);
        cache.getIfPresent(player).put(QuestKey.from(quest), quest);

        DB_EXECUTOR.execute(() -> {
            try (PreparedStatement ps = connection.prepareStatement("""
                    UPDATE quests SET currentExp = ?
                    WHERE player = ? AND time = ? AND type = ? AND object = ?
                    """)) {
                ps.setInt(1, progress);
                ps.setString(2, player);
                ps.setString(3, quest.getTime().name());
                ps.setString(4, quest.getType().name());
                ps.setString(5, QuestMapper.objectName(quest));
                ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.getMessage());
            }
        });
    }

    public void completeQuest(String player, QuestObject quest) {
        quest.setStatus(QuestStatus.PASSED);
        cache.getIfPresent(player).put(QuestKey.from(quest), quest);

        DB_EXECUTOR.execute(() -> {
            try (PreparedStatement ps = connection.prepareStatement("""
                    UPDATE quests SET state = 'PASSED'
                    WHERE player = ? AND time = ? AND type = ? AND object = ?
                    """)) {
                ps.setString(1, player);
                ps.setString(2, quest.getTime().name());
                ps.setString(3, quest.getType().name());
                ps.setString(4, QuestMapper.objectName(quest));
                ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.getMessage());
            }
        });
    }

    public void giveQuest(String player, QuestObject quest) {
        cache.asMap()
                .computeIfAbsent(player, k -> new ConcurrentHashMap<>())
                .put(QuestKey.from(quest), quest);

        DB_EXECUTOR.execute(() -> {
            try (PreparedStatement ps = connection.prepareStatement("""
                    INSERT OR IGNORE INTO quests
                    (player, currentExp, needExp, state, time, type, object)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """)) {
                ps.setString(1, player);
                ps.setInt(2, quest.getCurrentExp());
                ps.setInt(3, quest.getNeedExp());
                ps.setString(4, quest.getStatus().name());
                ps.setString(5, quest.getTime().name());
                ps.setString(6, quest.getType().name());
                ps.setString(7, QuestMapper.objectName(quest));
                ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.getMessage());
            }
        });
    }

    public boolean hasQuest(String player, QuestType type) {
        Map<QuestKey, QuestObject> map = cache.getIfPresent(player);
        if (map != null) {
            return map.values().stream().anyMatch(q -> q.getType() == type);
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT 1 FROM quests WHERE player = ? AND type = ? LIMIT 1")) {
            ps.setString(1, player);
            ps.setString(2, type.name());
            return ps.executeQuery().next();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
            return false;
        }
    }

    public void clear(QuestTime time) {
        cache.invalidateAll();
        DB_EXECUTOR.execute(() -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM quests WHERE time = ?")) {
                ps.setString(1, time.name());
                ps.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getLogger().severe(e.getMessage());
            }
        });
    }

    private void preloadCache() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM quests")) {
            while (rs.next()) {
                String player = rs.getString("player");
                QuestObject quest = QuestMapper.from(rs);
                cache.asMap()
                        .computeIfAbsent(player, k -> new ConcurrentHashMap<>())
                        .put(QuestKey.from(quest), quest);
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }
}