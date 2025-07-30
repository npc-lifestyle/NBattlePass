package wtf.n1zamu.database.quest;

import lombok.SneakyThrows;
import me.realized.tokenmanager.TokenManagerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.database.DataBase;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.status.QuestStatus;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.quest.type.QuestType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class QuestDataBase implements DataBase {
    public List<QuestObject> getQuests(String name, QuestTime time) {
        List<QuestObject> questObjects = new ArrayList<>();
        if (getQuestsCache().asMap().containsKey(name)) {
            for (QuestObject questObject : getQuestsCache().asMap().get(name)) {
                if (questObject.getTime() != time) {
                    continue;
                }
                questObjects.add(questObject);
            }
            return questObjects;
        }
        try {
            Statement stmnt = getConnection().createStatement();
            PreparedStatement pStatement = stmnt.getConnection().prepareStatement("SELECT * FROM quests WHERE player = ? AND time = ?");
            pStatement.setString(1, name);
            pStatement.setString(2, time.name());
            ResultSet rs = pStatement.executeQuery();
            while (rs.next()) {
                int currentExp = rs.getInt("currentExp");
                int needExp = rs.getInt("needExp");
                QuestStatus state = QuestStatus.valueOf(rs.getString("state"));
                QuestType type = QuestType.valueOf(rs.getString("type"));

                QuestObject questObject;
                if (type.isEntity()) {
                    questObject = new QuestObject(currentExp, needExp, type, time, state, EntityType.valueOf(rs.getString("object")));
                } else {
                    questObject = new QuestObject(currentExp, needExp, type, time, state, Material.getMaterial(rs.getString("object")));
                }
                questObjects.add(questObject);
            }
            pStatement.close();
            stmnt.close();
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass() + "(getQuests)" + ":" + e.getMessage());
        }
        getQuestsCache().put(name, questObjects);
        return questObjects;
    }

    public void updateProgress(String name, QuestObject questObject, int progress) {
        Bukkit.getScheduler().runTaskAsynchronously(NBattlePass.getInstance(), () -> {
            try {
                Statement stmnt = getConnection().createStatement();
                PreparedStatement pStatement = stmnt.getConnection().prepareStatement("UPDATE quests SET currentExp = " + progress + " WHERE player = ? AND time = ? AND state = ? AND object = ? AND type = ?");
                pStatement.setString(1, name);
                pStatement.setString(2, questObject.getTime().name());
                pStatement.setString(3, questObject.getStatus().name());
                if (questObject.getEntityType() != null) {
                    pStatement.setString(4, questObject.getEntityType().name());
                } else {
                    pStatement.setString(4, questObject.getMaterial().name());
                }
                pStatement.setString(5, questObject.getType().name());
                pStatement.executeUpdate();
                pStatement.close();
                stmnt.close();
            } catch (Exception e) {
                Bukkit.getLogger().info(e.getClass() + "(updateProgress)" + ":" + e.getMessage());
            }
        });
        if (getQuestsCache().asMap().containsKey(name)) {
            if (getQuestsCache().asMap().get(name) == null) {
                return;
            }
            for (QuestObject q : getQuestsCache().asMap().get(name)) {
                if (q != questObject) {
                    continue;
                }
                q.setCurrentExp(progress);
            }
        }
    }

    public int getCurrentProgress(String name, QuestObject questObject) {
        if (getQuestsCache().asMap().containsKey(name)) {
            if (getQuestsCache().getIfPresent(name) != null) {
                List<QuestObject> questObjectList = getQuestsCache().getIfPresent(name);
                if (questObjectList != null) {
                    for (QuestObject q : questObjectList) {
                        if (q != questObject) {
                            continue;
                        }
                        return q.getCurrentExp();
                    }
                }
            }
        }
        int progress = 0;
        try {
            Statement stmnt = getConnection().createStatement();
            PreparedStatement pStatement = stmnt.getConnection().prepareStatement("SELECT * FROM quests WHERE player = ? AND time = ? AND state = ? AND object = ? AND type = ?");
            pStatement.setString(1, name);
            pStatement.setString(2, questObject.getTime().name());
            pStatement.setString(3, questObject.getStatus().name());
            if (questObject.getType().isEntity()) {
                pStatement.setString(4, questObject.getEntityType().name());
            } else {
                pStatement.setString(4, questObject.getMaterial().name());
            }
            pStatement.setString(5, questObject.getType().name());
            ResultSet rs = pStatement.executeQuery();
            while (rs.next()) {
                progress = rs.getInt("currentExp");
            }
            pStatement.close();
            stmnt.close();
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass() + "(getCurrentProgress)" + ":" + e.getMessage());
        }
        return progress;
    }

    public void completeQuest(String name, QuestObject questObject) {
        Bukkit.getScheduler().runTaskAsynchronously(NBattlePass.getInstance(), () -> {
            try {
                Statement stmnt = getConnection().createStatement();
                PreparedStatement pStatement = stmnt.getConnection().prepareStatement("UPDATE quests SET state = 'PASSED' WHERE player = ? AND state = ? AND time = ? AND object = ?");
                pStatement.setString(1, name);
                pStatement.setString(2, questObject.getStatus().name());
                pStatement.setString(3, questObject.getTime().name());
                if (questObject.getType().isEntity()) {
                    pStatement.setString(4, questObject.getEntityType().name());
                } else {
                    pStatement.setString(4, questObject.getMaterial().name());
                }
                pStatement.executeUpdate();
                pStatement.close();
                stmnt.close();
            } catch (Exception e) {
                Bukkit.getLogger().info(e.getClass() + "(giveQuest)" + ":" + e.getMessage());
            }
        });
        if (getQuestsCache().asMap().containsKey(name)) {
            if (getQuestsCache().getIfPresent(name) == null) {
                return;
            }
            for (QuestObject q : getQuestsCache().asMap().get(name)) {
                if (q != questObject) {
                    continue;
                }
                q.setStatus(QuestStatus.PASSED);
            }
        }
    }

    @SneakyThrows
    public void giveQuest(String name, QuestObject questObject) {
        if (NBattlePass.getInstance().getPlayerDataBase().getExp(name) == -1) {
            NBattlePass.getInstance().getPlayerDataBase().addNewPlayer(name);
        }
        Bukkit.getScheduler().runTaskAsynchronously(NBattlePass.getInstance(), () -> {
            try {
                Statement stmnt = getConnection().createStatement();
                PreparedStatement pStatement = stmnt.getConnection().prepareStatement("INSERT INTO quests (player, currentExp, needExp, state, time, type, object) VALUES (?, ?, ?, ?, ?, ?, ?)");
                pStatement.setString(1, name);
                pStatement.setInt(2, questObject.getCurrentExp());
                pStatement.setInt(3, questObject.getNeedExp());
                pStatement.setString(4, questObject.getStatus().name());
                pStatement.setString(5, questObject.getTime().name());
                pStatement.setString(6, questObject.getType().name());
                if (questObject.getEntityType() != null) {
                    pStatement.setString(7, questObject.getEntityType().name());
                } else {
                    pStatement.setString(7, questObject.getMaterial().name());
                }
                pStatement.executeUpdate();
                pStatement.close();
                stmnt.close();
            } catch (Exception e) {
                Bukkit.getLogger().info(e.getClass() + "(giveQuest)" + ":" + e.getMessage());
            }
        });
        if (getQuestsCache().asMap().get(name) == null) {
            return;
        }
        getQuestsCache().asMap().get(name).add(questObject);
    }

    public boolean hasQuest(String name, QuestType type) {
        if (getQuestsCache().asMap().containsKey(name)) {
            if (getQuestsCache().getIfPresent(name) != null) {
                List<QuestObject> questObjectList = getQuestsCache().asMap().get(name);
                if (questObjectList != null) {
                    for (QuestObject q : questObjectList) {
                        if (q.getType() != type) {
                            continue;
                        }
                        return true;
                    }
                }
            }
            return false;
        }
        try {
            Statement stmnt = getConnection().createStatement();
            PreparedStatement pStatement = stmnt.getConnection().prepareStatement("SELECT * FROM quests WHERE player = ?");
            pStatement.setString(1, name);
            ResultSet rs = pStatement.executeQuery();
            while (rs.next()) {
                QuestType questType = QuestType.valueOf(rs.getString("type"));
                if (questType == type) {
                    return true;
                }
            }
            pStatement.close();
            stmnt.close();
        } catch (Exception e) {
            Bukkit.getLogger().info(e.getClass() + "(hasQuest)" + ":" + e.getMessage());
        }
        return false;
    }

    public void clear(QuestTime time) {
        try {
            Statement stmnt = getConnection().createStatement();
            stmnt.getConnection().prepareStatement("DELETE FROM quests WHERE time = '" + time.name() + "';").executeUpdate();
            stmnt.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info(e.getClass() + "(clear - quests)" + ":" + e.getMessage());
        }
    }

    public void fillCache() {
        try {
            ResultSet resultSet = getConnection().createStatement().executeQuery("SELECT * FROM quests");
            int cachedObjects = 0;
            while (resultSet.next()) {
                QuestObject questObject;
                String name = resultSet.getString("player");
                int currentExp = resultSet.getInt("currentExp");
                int needExp = resultSet.getInt("needExp");
                QuestStatus status = QuestStatus.valueOf(resultSet.getString("state").toUpperCase());
                QuestTime time = QuestTime.valueOf(resultSet.getString("time").toUpperCase());
                QuestType type = QuestType.valueOf(resultSet.getString("type").toUpperCase());
                if (type.isEntity()) {
                    questObject = new QuestObject(currentExp, needExp, type, time, status, EntityType.valueOf(resultSet.getString("object").toUpperCase()));
                } else {
                    questObject = new QuestObject(currentExp, needExp, type, time, status, Material.valueOf(resultSet.getString("object").toUpperCase()));
                }

                if (!getQuestsCache().asMap().containsKey(name)) {
                    List<QuestObject> questObjectList = new ArrayList<>();
                    questObjectList.add(questObject);
                    getQuestsCache().asMap().put(name, questObjectList);
                } else {
                    getQuestsCache().asMap().get(name).add(questObject);
                }
                cachedObjects++;
            }
            Bukkit.getLogger().info("[NBattlePass] В Кэш занесено " + cachedObjects + " объектов!");
        } catch (SQLException ignored) {
        }
    }
}
