package wtf.n1zamu.util;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestTime;
import wtf.n1zamu.quest.enums.QuestType;

import java.util.*;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RandomizeUtility {
    List<Integer> dayCount, weekCount;

    static {
        dayCount = NBattlePass.getInstance().getConfig().getIntegerList("dayQuestValues");
        weekCount = NBattlePass.getInstance().getConfig().getIntegerList("weekQuestValues");
    }

    public QuestObject getRandomQuest(QuestTime time) {
        QuestObject questObject;
        QuestType type = getRandomQuestType();
        if (type.getEntityTypes() != null) {
            questObject = new QuestObject(0, getRandomCount(time), type, time, QuestStatus.NOT_PASSED, getRandomEntity(type));
        } else {
            questObject = new QuestObject(0, getRandomCount(time), type, time, QuestStatus.NOT_PASSED, getMaterial(type));
        }
        return questObject;
    }

    private int getRandomCount(QuestTime questTime) {
        if (questTime == QuestTime.DAY) {
            return dayCount.get(new Random().nextInt(dayCount.size()));
        } else {
            return weekCount.get(new Random().nextInt(weekCount.size()));
        }
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

    private QuestType getRandomQuestType() {
        return QuestType.values()[new Random().nextInt(QuestType.values().length)];
    }

    private EntityType getRandomEntity(QuestType questType) {
        return (EntityType) questType.getEntityTypes().keySet().toArray()[new Random().nextInt(questType.getEntityTypes().keySet().toArray().length)];
    }

    private Material getMaterial(QuestType questType) {
        return (Material) questType.getMaterials().keySet().toArray()[new Random().nextInt(questType.getMaterials().size())];
    }
}
