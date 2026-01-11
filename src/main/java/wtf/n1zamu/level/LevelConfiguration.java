package wtf.n1zamu.level;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import wtf.n1zamu.NBattlePass;

import java.io.File;
import java.util.*;

@Getter
public class LevelConfiguration {
    private static final List<Level> levels = new ArrayList<>();

    static {
        File configurationFile = new File(
                NBattlePass.getInstance().getDataFolder(),
                "levels.yml"
        );

        if (!configurationFile.exists()) {
            NBattlePass.getInstance().saveResource("levels.yml", false);
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(configurationFile);
        configuration.getConfigurationSection("levels").getKeys(false).forEach(key -> {
            if (key == null) {
                return;
            }

            int level = Integer.parseInt(key);
            int exp = configuration.getInt("levels." + key + ".exp");
            levels.add(new Level(level, exp));
        });
    }

    public static String getExperienceBar(int currentExperience, int maxExperience) {
        int totalSticks = 100;

        int filledSticks = (int) ((double) currentExperience / maxExperience * totalSticks);
        int emptySticks = totalSticks - filledSticks;

        return "&6" +
                "|".repeat(Math.max(0, filledSticks)) +
                "&7" +
                "|".repeat(Math.max(0, emptySticks));
    }

    public static Level getLevel(int exp) {
        if (levels.isEmpty()) {
            return null;
        }

        if (exp <= 0) {
            return levels.stream()
                    .filter(level -> level.getNumber() == 1)
                    .findFirst()
                    .orElse(null);
        }

        Level result = null;
        Level maxLevel = null;

        for (Level level : levels) {
            if (maxLevel == null || level.getNeedExp() > maxLevel.getNeedExp()) {
                maxLevel = level;
            }

            if (level.getNeedExp() >= exp) {
                if (result == null || level.getNeedExp() < result.getNeedExp()) {
                    result = level;
                }
            }
        }

        return result != null ? result : maxLevel;
    }

}
