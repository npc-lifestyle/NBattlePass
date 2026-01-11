package wtf.n1zamu.inventory;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.inventory.config.ConfigurationItem;
import wtf.n1zamu.inventory.holder.BattlePassHolder;

import java.util.HashMap;
import java.util.Map;

public abstract class InventoryGUI {
    @Getter
    private final String id;
    @Getter
    private String title;
    protected final Map<String, ConfigurationItem> items;

    public InventoryGUI(String id) {
        this.id = id;
        this.items = new HashMap<>();

        ConfigurationSection section = NBattlePass.getInstance().getInventoryManager().getConfig().getConfigurationSection(this.id);

        if (section == null) {
            Bukkit.getLogger().warning("couldn't found section for path: " + this.id);
            return;
        }

        ConfigurationSection itemsSection = section.getConfigurationSection("items");

        if (itemsSection == null) {
            Bukkit.getLogger().warning("couldn't found section for path: " + section.getCurrentPath() + ".items");
            return;
        }
        this.title = section.getString("title");

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection s = itemsSection.getConfigurationSection(key);

            if (s == null) {
                Bukkit.getLogger().warning("couldn't found section for path: " + itemsSection.getCurrentPath() + ".key");
                continue;
            }

            ConfigurationItem item = new ConfigurationItem(s);
            this.items.put(key, item);
        }
    }

    protected void replaceLore(ItemStack item, Object... replacements) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return;

        var meta = item.getItemMeta();
        var lore = meta.getLore();

        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            for (int r = 0; r < replacements.length; r += 2) {
                line = line.replace(
                        String.valueOf(replacements[r]),
                        String.valueOf(replacements[r + 1])
                );
            }
            lore.set(i, line);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public abstract Inventory show(BattlePassHolder holder, Player player);
}