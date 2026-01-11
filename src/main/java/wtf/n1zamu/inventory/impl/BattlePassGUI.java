package wtf.n1zamu.inventory.impl;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.inventory.InventoryGUI;
import wtf.n1zamu.inventory.holder.BattlePassHolder;
import wtf.n1zamu.level.Level;
import wtf.n1zamu.level.LevelConfiguration;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestTime;
import wtf.n1zamu.util.ConfigUtility;
import wtf.n1zamu.util.RandomizeUtility;

import java.util.List;

public class BattlePassGUI extends InventoryGUI {
    public BattlePassGUI() {
        super("main-gui");
    }

    @Override
    public Inventory show(BattlePassHolder holder, Player player) {
        Inventory inv = Bukkit.createInventory(holder, 54,
                ConfigUtility.getColoredString("battlePass.inventory.name"));


        renderInfo(inv, player);
        renderQuests(inv, player, QuestTime.DAY);
        renderQuests(inv, player, QuestTime.WEEK);

        return inv;
    }

    private void renderInfo(Inventory inv, Player player) {
        val template = items.get("info").build();

        int exp = NBattlePass.getInstance()
                .getPlayerDataBase()
                .getExp(player.getName());

        Level level = LevelConfiguration.getLevel(exp);
        String expBar = ChatColor.translateAlternateColorCodes('&', LevelConfiguration.getExperienceBar(exp, level.getNeedExp()));
        String type = player.hasPermission("nbattlepass.subscribe")
                ? ChatColor.GOLD + "Платный"
                : ChatColor.GRAY + "Бесплатный";

        replaceLore(template,
                "%name%", player.getName(),
                "%currentExp%", exp,
                "%currentLevel%", level.getNumber(),
                "%expBar%", expBar,
                "%type%", type,
                "%expToNext%", level.getNeedExp() - exp
        );

        inv.setItem(
                ConfigUtility.getInt("battlePass.inventory.info.slot"),
                template
        );
    }


    private void renderQuests(Inventory inv, Player player, QuestTime time) {
        int startSlot = ConfigUtility.getInt(
                time == QuestTime.DAY
                        ? "battlePass.inventory.dailyQuest.startSlot"
                        : "battlePass.inventory.weeklyQuest.startSlot"
        );

        ItemStack template = items.get(
                time == QuestTime.DAY ? "dailyQuest" : "weeklyQuest"
        ).build();

        List<QuestObject> quests = NBattlePass.getInstance()
                .getQuestDatabase()
                .getQuests(player.getName(), time);

        int slotOffset = 0;

        for (QuestObject quest : quests) {
            ItemStack item = template.clone();

            int current = quest.getCurrentExp();
            int need = quest.getNeedExp();

            QuestStatus status =
                    current >= need ? QuestStatus.PASSED : QuestStatus.NOT_PASSED;

            String statusText = status == QuestStatus.PASSED
                    ? ChatColor.GREEN + "Выполнено"
                    : ChatColor.RED + "Не выполнено";

            replaceLore(item,
                    "%description%", RandomizeUtility.getNameByQuest(quest),
                    "%current%", current,
                    "%need%", need,
                    "%reward%", ConfigUtility.getRawString(
                            time == QuestTime.DAY ? "expForDaily" : "expForWeek"
                    ),
                    "%status%", statusText
            );

            item.setAmount(slotOffset + 1);
            inv.setItem(startSlot + slotOffset, item);

            slotOffset++;
        }
    }
}
