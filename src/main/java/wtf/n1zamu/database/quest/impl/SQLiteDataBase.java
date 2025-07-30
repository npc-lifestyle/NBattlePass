package wtf.n1zamu.database.quest.impl;

import org.bukkit.Bukkit;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.database.quest.QuestDataBase;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteDataBase extends QuestDataBase {

    private Connection connection;

    public SQLiteDataBase() {
        File dataFile = new File(NBattlePass.getInstance().getDataFolder(), "quests.db");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/NBattlepass/quests.db");
            String sql = "CREATE TABLE IF NOT EXISTS quests " +
                    "(player TEXT NOT NULL, " +
                    "currentExp INTEGER, " +
                    "needExp INTEGER, " +
                    "state TEXT," +
                    "time TEXT, " +
                    "type TEXT, " +
                    "object TEXT)";
            connection.createStatement().executeUpdate(sql);
            fillCache();
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            Bukkit.getLogger().info("[NBattlePass] В Кэше на момент выключения " + this.getQuestsCache().asMap().size() + "объектов!");
            connection.close();
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass().getName() + ":" + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

}
