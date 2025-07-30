package wtf.n1zamu.inventory;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.n1zamu.NBattlePass;

import wtf.n1zamu.config.object.Level;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.status.QuestStatus;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.util.*;

import java.util.ArrayList;
import java.util.List;

public class BattlePassHolder implements InventoryHolder {
    private final Inventory battlePassInventory;
    private final int currentExp;
    private final String playerName;

    public BattlePassHolder(Player player) {
        this.currentExp = NBattlePass.getInstance().getPlayerDataBase().getExp(player.getName());
        this.playerName = player.getName();
        this.battlePassInventory = generateInventory(player);
    }

    @Override
    public @NonNull Inventory getInventory() {
        return battlePassInventory;
    }

    private Inventory generateInventory(Player player) {
        Inventory battlePassInventory = Bukkit.createInventory(this, 54, ConfigUtil.getColoredString("battlePass.inventory.name"));
        // Инфа о БП
        ItemStack infoItem = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setCustomModelData(ConfigUtil.getInt("battlePass.inventory.info.modelData"));
        infoMeta.setDisplayName(ConfigUtil.getColoredString("battlePass.inventory.info.name"));
        Level currentBPLevel = LevelFormatUtil.getLevel(currentExp);
        String expBar = LevelFormatUtil.getExperienceBar(currentExp, currentBPLevel.getNeedExp());
        String type = player.hasPermission("nbattlepass.subscribe") ? ChatColor.GOLD + "Платный" : ChatColor.GOLD + "Бесплатный";
        List<String> lore = new ArrayList<>();
        ConfigUtil.getColoredList("battlePass.inventory.info.lore").forEach(line -> lore.add(ChatColor.translateAlternateColorCodes('&', line
                .replace("%currentExp%", String.valueOf(currentExp))
                .replace("%currentLevel%", String.valueOf(currentBPLevel.getNumber()))
                .replace("%expBar%", expBar)
                .replace("%name%", playerName)
                .replace("%type%", type)
                .replace("%expToNext%", String.valueOf(currentBPLevel.getNeedExp() - currentExp)))));
        infoMeta.setLore(lore);
        infoItem.setItemMeta(infoMeta);
        battlePassInventory.setItem(ConfigUtil.getInt("battlePass.inventory.info.slot"), infoItem);

        // Квесты
        int currentDailySlot = 0;
        for (QuestObject questObject : NBattlePass.getInstance().getQuestDataBase().getQuests(playerName, QuestTime.DAY)) {
            ItemStack dailyQuestItem = new ItemStack(Material.SUNFLOWER);
            ItemMeta dailyQuestMeta = infoItem.getItemMeta();
            dailyQuestMeta.setDisplayName(ConfigUtil.getColoredString("battlePass.inventory.dailyQuest.name"));
            int currentProgress = NBattlePass.getInstance().getQuestDataBase().getCurrentProgress(playerName, questObject);
            int needProgress = questObject.getNeedExp();
            if (currentProgress < needProgress) {
                questObject.setStatus(QuestStatus.NOT_PASSED);
            }
            String dailyStatus = questObject.getStatus() == QuestStatus.NOT_PASSED ? ChatColor.RED + "Не выполнено" : ChatColor.GREEN + "Выполнено";
            List<String> dailyLore = new ArrayList<>();
            ConfigUtil.getColoredList("battlePass.inventory.dailyQuest.lore").forEach(line -> dailyLore.add(ChatColor.translateAlternateColorCodes('&', line
                    .replace("%description%", RandomizeUtil.getNameByQuest(questObject))
                    .replace("%need%", String.valueOf(needProgress))
                    .replace("%current%", String.valueOf(currentProgress))
                    .replace("%reward%", ConfigUtil.getRawString("expForDaily"))
                    .replace("%status%", dailyStatus))));
            dailyQuestMeta.setLore(dailyLore);
            dailyQuestItem.setAmount(currentDailySlot + 1);
            dailyQuestItem.setItemMeta(dailyQuestMeta);
            battlePassInventory.setItem(ConfigUtil.getInt("battlePass.inventory.dailyQuest.startSlot") + currentDailySlot, dailyQuestItem);
            currentDailySlot++;
        }

        int currentWeeklySlot = 0;
        for (QuestObject questObject : NBattlePass.getInstance().getQuestDataBase().getQuests(playerName, QuestTime.WEEK)) {
            ItemStack weekItem = new ItemStack(Material.SUNFLOWER);
            ItemMeta weekItemMeta = infoItem.getItemMeta();
            weekItemMeta.setDisplayName(ConfigUtil.getColoredString("battlePass.inventory.weeklyQuest.name"));
            int currentProgress = NBattlePass.getInstance().getQuestDataBase().getCurrentProgress(playerName, questObject);
            int needProgress = questObject.getNeedExp();
            if (currentProgress < needProgress) {
                questObject.setStatus(QuestStatus.NOT_PASSED);
            }
            String dailyStatus = questObject.getStatus() == QuestStatus.NOT_PASSED ? ChatColor.RED + "Не выполнено" : ChatColor.GREEN + "Выполнено";
            List<String> weeklyLore = new ArrayList<>();
            ConfigUtil.getColoredList("battlePass.inventory.weeklyQuest.lore").forEach(line -> weeklyLore.add(ChatColor.translateAlternateColorCodes('&', line
                    .replace("%description%", RandomizeUtil.getNameByQuest(questObject))
                    .replace("%need%", String.valueOf(needProgress))
                    .replace("%current%", String.valueOf(currentProgress))
                    .replace("%reward%", ConfigUtil.getRawString("expForWeek"))
                    .replace("%status%", dailyStatus))));
            weekItemMeta.setLore(weeklyLore);
            weekItem.setAmount(currentWeeklySlot + 1);
            weekItem.setItemMeta(weekItemMeta);
            battlePassInventory.setItem(ConfigUtil.getInt("battlePass.inventory.weeklyQuest.startSlot") + currentWeeklySlot, weekItem);
            currentWeeklySlot++;
        }

        return battlePassInventory;
    }
}
