package wtf.n1zamu.database.quest.impl;

import org.bukkit.Bukkit;
import wtf.n1zamu.database.quest.QuestDataBase;
import wtf.n1zamu.util.ConfigUtil;

import java.sql.*;

public class MySQLDataBase extends QuestDataBase {
    private Connection connection;

    public MySQLDataBase() {
        String url = "jdbc:mysql://"
                + ConfigUtil.getRawString("host") + ":"
                + ConfigUtil.getInt("port") + "/"
                + ConfigUtil.getRawString("questDatabase") + "?useSSL=false&serverTimezone=UTC";

        String user = ConfigUtil.getRawString("username");
        String password = ConfigUtil.getRawString("password");
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            fillCache();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String sql = "CREATE TABLE IF NOT EXISTS quests " +
                    "(player TEXT NOT NULL, " +
                    "currentExp INTEGER, " +
                    "needExp INTEGER, " +
                    "state TEXT," +
                    "time TEXT, " +
                    "type TEXT, " +
                    "object TEXT)";

            this.connection.createStatement().executeUpdate(sql);
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            this.connection.createStatement().getConnection().close();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
