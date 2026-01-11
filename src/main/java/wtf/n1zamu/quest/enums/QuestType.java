package wtf.n1zamu.quest.enums;

import lombok.Getter;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.util.ConfigUtility;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum QuestType {
    BREAK_BLOCKS("breakBlocks"),
    CRAFT_ITEMS("craftItems"),
    EAT("eat"),
    PICK_EXP("exp"),
    KILL_MOBS("killMobs", true),
    PLACE_BLOCKS("placeBlocks");

    private final String identifier;
    private final String name;
    private final boolean isEntity;
    private Map<Material, String> materials;
    private Map<EntityType, String> entityTypes;

    QuestType(String identifier, boolean isEntity) {
        Map<EntityType, String> map = new HashMap<>();
        val mapFromConfig = NBattlePass.getInstance().getConfig().getConfigurationSection(identifier + ".map");
        if (mapFromConfig == null) {
            Bukkit.getLogger().info("[NBattlePass] Дасвидос! Мапы не найдено!");
            this.identifier = identifier;
            this.entityTypes = map;
            this.name = ConfigUtility.getColoredString(identifier + ".name");
            this.isEntity = isEntity;
            return;
        }
        mapFromConfig.getKeys(false).forEach(key -> {
            EntityType entityType = EntityType.valueOf(key);
            String name = mapFromConfig.getString(key);
            map.put(entityType, name);
        });
        this.identifier = identifier;
        this.entityTypes = map;
        this.name = ConfigUtility.getColoredString(identifier + ".name");
        this.isEntity = isEntity;
    }

    QuestType(String identifier) {
        Map<Material, String> map = new HashMap<>();
        val mapFromConfig = NBattlePass.getInstance().getConfig().getConfigurationSection(identifier + ".map");
        if (mapFromConfig == null) {
            Bukkit.getLogger().info("[NBattlePass] Дасвидос! Мапы не найдено!");
            this.identifier = identifier;
            this.materials = map;
            this.name = ConfigUtility.getColoredString(identifier + ".name");
            this.isEntity = false;
            return;
        }
        mapFromConfig.getKeys(false).forEach(key -> {
            Material material = Material.valueOf(key);
            String name = mapFromConfig.getString(key);
            map.put(material, name);
        });
        this.identifier = identifier;
        this.materials = map;
        this.name = ConfigUtility.getColoredString(identifier + ".name");
        this.isEntity = false;
    }
}
