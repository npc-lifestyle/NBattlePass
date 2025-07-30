package wtf.n1zamu.config.impl;

import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.config.CustomConfiguration;
import wtf.n1zamu.config.object.Level;

import java.io.File;
import java.util.*;

public class LevelConfiguration implements CustomConfiguration {
    public LevelConfiguration() {
        setUp();
    }

    @Override
    public @NonNull String getName() {
        return "levels";
    }

    @Override
    public @NonNull FileConfiguration getConfig() {
        File configFile = new File(NBattlePass.getInstance().getDataFolder(), getName() + ".yml");
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public Map<Integer, Level> getLevels() {
        Map<Integer, Level> levels = new HashMap<>();
        for (String sec : getConfig().getConfigurationSection("levels").getKeys(false)) {
            if (sec == null) {
                continue;
            }

            int level = Integer.parseInt(sec);
            int exp = getConfig().getInt("levels." + sec + ".exp");
            levels.put(exp, new Level(level, exp));
        }
        return levels;
    }
}
