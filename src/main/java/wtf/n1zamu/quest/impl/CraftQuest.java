package wtf.n1zamu.quest.impl;

import lombok.val;
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
import wtf.n1zamu.quest.QuestProgressHandler;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestType;
import wtf.n1zamu.util.PlayerCraftUtility;

import java.util.Map;

public class CraftQuest implements Quest<CraftItemEvent> {
    @Override
    public void handle(@NonNull CraftItemEvent event, QuestProgressHandler handler) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (player.getInventory().firstEmpty() == -1) {
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
                int maxCraftable = PlayerCraftUtility.getMaxCraftAmount(event.getInventory());
                int capacity = PlayerCraftUtility.fits(test, event.getView().getBottomInventory());
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
        val quests = getQuests(player, QuestType.CRAFT_ITEMS);
        int finalRecipeAmount = recipeAmount;
        quests.forEach(questData -> {
            if (questData.getMaterial() != test.getType()) {
                return;
            }
            handler.addProgress(player, questData, finalRecipeAmount);
        });
    }
}
