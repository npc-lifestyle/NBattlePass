package wtf.n1zamu.quest.impl;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.Quest;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.status.QuestStatus;
import wtf.n1zamu.quest.type.QuestType;
import wtf.n1zamu.util.QuestUtil;

import java.util.Map;

public class KillQuest implements Quest<EntityDeathEvent> {
    @Override
    public Runnable getExecutionTask(@NonNull EntityDeathEvent event) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getEntity().getKiller();
                if (player == null) {
                    return;
                }
                if (!NBattlePass.getInstance().getQuestDataBase().hasQuest(player.getName(), QuestType.KILL_MOBS)) {
                    return;
                }
                Map<EntityType, QuestObject> entityTypeQuestMap = QuestUtil.getEntitiesByQuests(player, QuestType.KILL_MOBS);
                if (!entityTypeQuestMap.containsKey(event.getEntityType())) {
                    return;
                }
                if (entityTypeQuestMap.get(event.getEntityType()).getStatus() == QuestStatus.PASSED) {
                    return;
                }
                int current = NBattlePass.getInstance().getQuestDataBase().getCurrentProgress(player.getName(), entityTypeQuestMap.get(event.getEntityType()));
                if (current >= entityTypeQuestMap.get(event.getEntityType()).getNeedExp()) {
                    return;
                }
                update(entityTypeQuestMap.get(event.getEntityType()), player, 1);
            }
        };
    }
}
