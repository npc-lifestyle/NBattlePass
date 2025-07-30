package wtf.n1zamu.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.quest.type.QuestType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuestUtil {
    public static List<QuestObject> getQuestsByType(Player player, QuestType questType) {
        return Stream.of(
                        NBattlePass.getInstance().getQuestDataBase().getQuests(player.getName(), QuestTime.DAY),
                        NBattlePass.getInstance().getQuestDataBase().getQuests(player.getName(), QuestTime.WEEK)
                )
                .flatMap(List::stream)
                .filter(quest -> quest.getType() == questType)
                .collect(Collectors.toList());
    }

    public static Map<EntityType, QuestObject> getEntitiesByQuests(Player player, QuestType questType) {
        Map<EntityType, QuestObject> entityTypes = new HashMap<>();
        getQuestsByType(player, questType).forEach(quest -> {
            if (quest.getEntityType() != null) {
                entityTypes.put(quest.getEntityType(), quest);
            }
        });
        return entityTypes;
    }

    public static Map<Material, QuestObject> getMaterialsByQuests(Player player, QuestType questType) {
        Map<Material, QuestObject> materials = new HashMap<>();
        getQuestsByType(player, questType).forEach(quest -> {
            if (quest.getMaterial() != null) {
                materials.put(quest.getMaterial(), quest);
            }
        });

        return materials;
    }
}
