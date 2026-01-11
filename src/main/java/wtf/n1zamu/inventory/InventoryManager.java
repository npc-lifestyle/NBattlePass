package wtf.n1zamu.inventory;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.inventory.holder.BattlePassHolder;
import wtf.n1zamu.inventory.impl.BattlePassGUI;


import java.io.File;
import java.util.HashMap;

public class InventoryManager {
    @Getter
    private HashMap<String, InventoryGUI> guis;
    private FileConfiguration configuration;

    public InventoryManager() {
        File configurationFile = new File(
                NBattlePass.getInstance().getDataFolder(),
                "admin.yml"
        );

        if (!configurationFile.exists()) {
            NBattlePass.getInstance().saveResource("admin.yml", false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(configurationFile);
    }

    public void load() {
        this.guis = new HashMap<>();
        InventoryGUI gui = new BattlePassGUI();
        this.guis.put(gui.getId(), gui);
    }

    public Inventory show(String id, Player player) {
        return this.guis.get(id).show(new BattlePassHolder(player), player);
    }

    public void reloadConfig() {
        File configurationFile = new File(
                NBattlePass.getInstance().getDataFolder(),
                "admin.yml"
        );
        this.configuration = YamlConfiguration.loadConfiguration(configurationFile);
        load();
    }

    public FileConfiguration getConfig() {
        return configuration;
    }
}
