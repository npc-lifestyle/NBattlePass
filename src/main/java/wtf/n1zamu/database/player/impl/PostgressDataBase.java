package wtf.n1zamu.database.player.impl;

import org.bukkit.Bukkit;
import wtf.n1zamu.database.player.PlayerDataBase;
import wtf.n1zamu.util.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgressDataBase extends PlayerDataBase {
    private Connection connection;

    public PostgressDataBase() {
        String url = "jdbc:postgresql://"
                + ConfigUtil.getRawString("host") + ":"
                + ConfigUtil.getInt("port") + "/"
                + ConfigUtil.getRawString("playerDatabase") + "?useSSL=false&serverTimezone=UTC";

        String user = ConfigUtil.getRawString("username");
        String password = ConfigUtil.getRawString("password");
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");

            String sql = "CREATE TABLE IF NOT EXISTS players " +
                    "(player TEXT NOT NULL, " +
                    "rewardType TEXT, " +
                    "currentExp INTEGER, " +
                    "currentLvl INTEGER, " +
                    "taken INTEGER)";

            this.connection.createStatement().executeUpdate(sql);
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
