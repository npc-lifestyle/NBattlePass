package wtf.n1zamu.quest.impl;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import lombok.val;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.Quest;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestType;

import java.util.List;

public class ExperienceQuest implements Quest<PlayerPickupExperienceEvent> {
    @Override
    public void handle(@NonNull PlayerPickupExperienceEvent event, wtf.n1zamu.quest.QuestProgressHandler handler) {
        val player = event.getPlayer();
        val expOrb = event.getExperienceOrb();
        if (expOrb.getExperience() <= 0) return;
        val quests = getQuests(player, QuestType.PICK_EXP);
        quests.forEach(questData -> handler.addProgress(player, questData, expOrb.getExperience()));
    }
}
