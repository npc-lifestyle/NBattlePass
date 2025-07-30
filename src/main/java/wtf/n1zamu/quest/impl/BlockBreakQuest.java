package wtf.n1zamu.quest.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.Quest;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.status.QuestStatus;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.quest.type.QuestType;
import wtf.n1zamu.util.ConfigUtil;
import wtf.n1zamu.util.LevelFormatUtil;
import wtf.n1zamu.util.QuestUtil;

import java.util.Map;

public class BlockBreakQuest implements Quest<BlockBreakEvent> {
    @Override
    public Runnable getExecutionTask(@NonNull BlockBreakEvent event) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                Block block = event.getBlock();
                if (!NBattlePass.getInstance().getQuestDataBase().hasQuest(player.getName(), QuestType.BREAK_BLOCKS)) {
                    return;
                }
                Map<Material, QuestObject> materialQuestMap = QuestUtil.getMaterialsByQuests(player, QuestType.BREAK_BLOCKS);
                if (!materialQuestMap.containsKey(block.getType())) {
                    return;
                }
                if (materialQuestMap.get(block.getType()).getStatus() == QuestStatus.PASSED) {
                    return;
                }
                int current = NBattlePass.getInstance().getQuestDataBase().getCurrentProgress(player.getName(), materialQuestMap.get(block.getType()));
                if (current >= materialQuestMap.get(block.getType()).getNeedExp()) {
                    return;
                }
                update(materialQuestMap.get(block.getType()), player, 1);
            }
        };
    }
}
