package wtf.n1zamu.quest.impl;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.Quest;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.QuestProgressHandler;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestType;

import java.util.Map;

public class BlockPlaceQuest implements Quest<BlockPlaceEvent> {

    @Override
    public void handle(@NonNull BlockPlaceEvent event, QuestProgressHandler handler) {
        val player = event.getPlayer();
        val block = event.getBlockPlaced();
        val quests = getQuests(player, QuestType.PLACE_BLOCKS);
        quests.forEach(questData -> {
            if (block.getType() != questData.getMaterial()) {
                return;
            }
            handler.addProgress(player, questData, 1);
        });
    }
}
