package wtf.n1zamu.quest.impl;

import lombok.val;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.Quest;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.QuestProgressHandler;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestType;

import java.util.Map;

public class KillQuest implements Quest<EntityDeathEvent> {
    @Override
    public void handle(@NonNull EntityDeathEvent event, QuestProgressHandler handler) {
        val player = event.getEntity().getKiller();
        if (player == null) {
            return;
        }
        val entityType = event.getEntityType();
        val quests = getQuests(player, QuestType.KILL_MOBS);
        quests.forEach(questData -> {
            if (questData.getEntityType() != entityType) {
                return;
            }
            handler.addProgress(player, questData, 1);
        });
    }
}
