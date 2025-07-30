package wtf.n1zamu.database.player;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

import org.bukkit.Bukkit;

import wtf.n1zamu.database.DataBase;
import wtf.n1zamu.database.player.type.RewardType;
import wtf.n1zamu.util.LevelFormatUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class PlayerDataBase implements DataBase {
    public int getExp(String name) {
        int exp = -1;
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("SELECT * FROM players WHERE player = ?");
            pStatement.setString(1, name);
            ResultSet rs = pStatement.executeQuery();
            while (rs.next()) {
                exp = rs.getInt("currentExp");
            }
            pStatement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass() + ":" + e.getMessage());
        }
        return exp;
    }

    public void setExp(String name, int exp) {
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("UPDATE players SET currentExp = " + exp + " WHERE player = ?");
            pStatement.setString(1, name);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass() + ":" + e.getMessage());
        }
    }

    public void upLevel(String name, int newLevel) {
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("UPDATE players SET currentLvl = " + newLevel + ", taken = 0 " + " WHERE player = ?");
            pStatement.setString(1, name);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass() + ":" + e.getMessage());
        }
    }

    public void addNewPlayer(String name) {
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("INSERT INTO players(player, rewardType, currentExp, currentLvl, taken) VALUES(?, ?, ?, ?, ?)");
            pStatement.setString(1, name);
            pStatement.setString(2, RewardType.DEFAULT.name());
            pStatement.setInt(3, 0);
            pStatement.setInt(4, 0);
            pStatement.setInt(5, 0);
            pStatement.executeUpdate();
            pStatement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass() + ":" + e.getMessage());
        }
    }

    public void giveSubscribe(String name) {
        User user = LuckPermsProvider.get().getUserManager().getUser(name);
        if (user != null) {
            user.data().add(Node.builder("nBattlePass.subscribe").build());
        }
        try {
            int playerExp = getExp(name);
            int level = LevelFormatUtil.getLevel(playerExp).getNumber() - 1;
            PreparedStatement pStatement = getConnection().prepareStatement("INSERT INTO players(player, rewardType, currentExp, currentLvl, taken) VALUES(?, ?, ?, ?, ?)");
            pStatement.setString(1, name);
            pStatement.setString(2, RewardType.SUBSCRIBE.name());
            pStatement.setInt(3, playerExp);
            pStatement.setInt(4, level);
            pStatement.setInt(5, 0);
            pStatement.executeUpdate();
            pStatement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass() + ":" + e.getMessage());
        }
    }

    public Map<String, Integer> getPlayerExp() {
        Map<String, Integer> playerExp = new HashMap<>();
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("SELECT DISTINCT player, currentExp FROM players");
            ResultSet rs = pStatement.executeQuery();
            while (rs.next()) {
                playerExp.put(rs.getString("player"), rs.getInt("currentExp"));
            }
            pStatement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass() + ":" + e.getMessage());
        }
        return playerExp;
    }

    public void clear() {
        try {
            getConnection().createStatement().executeUpdate("DROP TABLE IF EXISTS players;");
            String sql = "CREATE TABLE IF NOT EXISTS players " +
                    "(player TEXT NOT NULL, " +
                    "rewardType TEXT, " +
                    "currentExp INTEGER, " +
                    "currentLvl INTEGER, " +
                    "taken INTEGER)";

            getConnection().createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
