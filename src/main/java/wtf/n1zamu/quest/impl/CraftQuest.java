package wtf.n1zamu.quest.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
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
import wtf.n1zamu.util.PlayerUtil;
import wtf.n1zamu.util.QuestUtil;

import java.util.Map;

public class CraftQuest implements Quest<CraftItemEvent> {
    @Override
    public Runnable getExecutionTask(@NonNull CraftItemEvent event) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (!(event.getWhoClicked() instanceof Player)) {
                    return;
                }
                Player player = (Player) event.getWhoClicked();
                if (player.getInventory().firstEmpty() == -1) {
                    return;
                }
                if (!NBattlePass.getInstance().getQuestDataBase().hasQuest(player.getName(), QuestType.CRAFT_ITEMS)) {
                    return;
                }
                ItemStack test = event.getRecipe().getResult().clone();
                ClickType click = event.getClick();
                int recipeAmount = test.getAmount();
                switch (click) {
                    case NUMBER_KEY: {
                        if (event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) == null) break;
                        recipeAmount = 0;
                        break;
                    }
                    case DROP:
                    case CONTROL_DROP: {
                        ItemStack cursor = event.getCursor();
                        if (cursor == null || cursor.getType() == Material.matchMaterial("AIR")) break;
                        recipeAmount = 0;
                        break;
                    }
                    case SHIFT_RIGHT:
                    case SHIFT_LEFT: {
                        if (recipeAmount == 0) break;
                        int maxCraftable = PlayerUtil.getMaxCraftAmount(event.getInventory());
                        int capacity = PlayerUtil.fits(test, event.getView().getBottomInventory());
                        if (capacity < maxCraftable) {
                            maxCraftable = (capacity + recipeAmount - 1) / recipeAmount * recipeAmount;
                        }
                        recipeAmount = maxCraftable;
                        break;
                    }
                }
                if (recipeAmount == 0) {
                    return;
                }

                if (!NBattlePass.getInstance().getQuestDataBase().hasQuest(player.getName(), QuestType.CRAFT_ITEMS)) {
                    return;
                }
                Map<Material, QuestObject> materialQuestMap = QuestUtil.getMaterialsByQuests(player, QuestType.CRAFT_ITEMS);
                if (!materialQuestMap.containsKey(test.getType())) {
                    return;
                }
                if (materialQuestMap.get(test.getType()).getStatus() == QuestStatus.PASSED) {
                    return;
                }
                int current = NBattlePass.getInstance().getQuestDataBase().getCurrentProgress(player.getName(), materialQuestMap.get(test.getType()));

                if (current >= materialQuestMap.get(test.getType()).getNeedExp()) {
                    return;
                }
                update(materialQuestMap.get(test.getType()), player, recipeAmount);
            }
        };
    }
}
