package wtf.n1zamu.quest.impl;

import lombok.val;
import org.bukkit.event.block.BlockBreakEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.quest.Quest;
import wtf.n1zamu.quest.QuestProgressHandler;
import wtf.n1zamu.quest.enums.QuestType;

public class BlockBreakQuest implements Quest<BlockBreakEvent> {
    @Override
    public void handle(@NonNull BlockBreakEvent event, QuestProgressHandler handler) {
        val player = event.getPlayer();
        val block = event.getBlock();
        val quests = getQuests(player, QuestType.BREAK_BLOCKS);
        quests.forEach(questData -> {
            if (questData.getMaterial() != block.getType()) {
                return;
            }
        handler.addProgress(player, questData, 1);
        });
    }
}
