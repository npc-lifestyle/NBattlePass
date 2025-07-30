package wtf.n1zamu.quest.impl;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.Quest;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.status.QuestStatus;
import wtf.n1zamu.quest.type.QuestType;
import wtf.n1zamu.util.QuestUtil;

import java.util.List;

public class ExperienceQuest implements Quest<PlayerPickupExperienceEvent> {
    @Override
    public Runnable getExecutionTask(@NonNull PlayerPickupExperienceEvent event) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (!NBattlePass.getInstance().getQuestDataBase().hasQuest(player.getName(), QuestType.PICK_EXP)) {
                    return;
                }
                ExperienceOrb orb = event.getExperienceOrb();
                if (orb.getExperience() == 0) {
                    return;
                }
                List<QuestObject> expQuestObjects = QuestUtil.getQuestsByType(player, QuestType.PICK_EXP);
                expQuestObjects.forEach(quest -> {
                    if (quest.getStatus() == QuestStatus.PASSED) {
                        return;
                    }
                    update(quest, player, orb.getExperience());
                });
            }
        };
    }
}
