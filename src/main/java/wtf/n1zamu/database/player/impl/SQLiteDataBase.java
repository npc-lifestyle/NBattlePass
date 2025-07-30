package wtf.n1zamu.database.player.impl;

import org.bukkit.Bukkit;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.database.player.PlayerDataBase;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteDataBase extends PlayerDataBase {
    private Connection connection;

    public SQLiteDataBase() {
        File dataFile = new File(NBattlePass.getInstance().getDataFolder(), "players.db");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                boolean isCreated = dataFile.createNewFile();
                if (isCreated) {
                    Bukkit.getLogger().info("[NBattlePass] SQLite База Данных успешно создана!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/NBattlepass/players.db");
            String sql = "CREATE TABLE IF NOT EXISTS players " +
                    "(player TEXT NOT NULL, " +
                    "rewardType TEXT, " +
                    "currentExp INTEGER, " +
                    "currentLvl INTEGER, " +
                    "taken INTEGER)";
            connection.createStatement().executeUpdate(sql);
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            connection.close();
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass().getName() + ":" + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
