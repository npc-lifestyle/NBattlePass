package wtf.n1zamu.quest.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
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

public class EatQuest implements Quest<PlayerItemConsumeEvent> {
    @Override
    public Runnable getExecutionTask(@NonNull PlayerItemConsumeEvent event) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                ItemStack itemStack = event.getItem();
                if (!NBattlePass.getInstance().getQuestDataBase().hasQuest(player.getName(), QuestType.EAT)) {
                    return;
                }
                Map<Material, QuestObject> materialQuestMap = QuestUtil.getMaterialsByQuests(player, QuestType.EAT);
                if (!materialQuestMap.containsKey(itemStack.getType())) {
                    return;
                }
                if (materialQuestMap.get(itemStack.getType()).getStatus() == QuestStatus.PASSED) {
                    return;
                }
                update(materialQuestMap.get(itemStack.getType()), player, 1);
            }
        };
    }
}
