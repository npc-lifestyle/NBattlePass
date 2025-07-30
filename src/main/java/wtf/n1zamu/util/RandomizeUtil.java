package wtf.n1zamu.util;


import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.status.QuestStatus;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.quest.type.QuestType;

import java.util.*;

public class RandomizeUtil {
    public static QuestObject getRandomQuest(QuestTime time) {
        QuestObject questObject;
        QuestType type = getRandomQuestType();
        if (type.getEntityTypes() != null) {
            questObject = new QuestObject(0, getRandomCount(time), type, time, QuestStatus.NOT_PASSED, getRandomEntity(type));
        } else {
            questObject = new QuestObject(0, getRandomCount(time), type, time, QuestStatus.NOT_PASSED, getMaterial(type));
        }
        return questObject;
    }

    public static String getNameByQuest(QuestObject questObject) {
        StringBuilder finalName = new StringBuilder();
        Keyed object = questObject.getEntityType() != null ? questObject.getEntityType() : questObject.getMaterial();
        String start = questObject.getType().getName();
        String objectName;
        if (questObject.getType() == QuestType.PICK_EXP) {
            return finalName.append(start).append(" ").append(questObject.getNeedExp()).append(" ").append("опыта").toString();
        }
        if (object instanceof Material) {
            objectName = questObject.getType().getMaterials().entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getKey(), object))
                    .map(Map.Entry::getValue)
                    .findAny().get();
            return finalName.append(start).append(" ").append(questObject.getNeedExp()).append(" ").append(objectName).toString();
        }
        objectName = questObject.getType().getEntityTypes().entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getKey(), object))
                .map(Map.Entry::getValue)
                .findAny().get();
        return finalName.append(start).append(" ").append(questObject.getNeedExp()).append(" ").append(objectName).toString();
    }

    private static int getRandomCount(QuestTime questTime) {
        List<Integer> dayCount = NBattlePass.getInstance().getConfig().getIntegerList("dayQuestValues");
        List<Integer> weekCount = NBattlePass.getInstance().getConfig().getIntegerList("weekQuestValues");
        if (questTime == QuestTime.DAY) {
            return dayCount.get(new Random().nextInt(dayCount.size()));
        } else {
            return weekCount.get(new Random().nextInt(weekCount.size()));
        }
    }

    private static QuestType getRandomQuestType() {
        return QuestType.values()[new Random().nextInt(QuestType.values().length)];
    }

    private static EntityType getRandomEntity(QuestType questType) {
        Bukkit.getLogger().info(questType.getEntityTypes().toString());
        return (EntityType) questType.getEntityTypes().keySet().toArray()[new Random().nextInt(questType.getEntityTypes().keySet().toArray().length)];
    }

    private static Material getMaterial(QuestType questType) {
        Bukkit.getLogger().info(questType.getMaterials().toString());
        return (Material) questType.getMaterials().keySet().toArray()[new Random().nextInt(questType.getMaterials().size())];
    }
}
