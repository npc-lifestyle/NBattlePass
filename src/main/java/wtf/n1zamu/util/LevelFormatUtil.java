package wtf.n1zamu.util;

import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.config.object.Level;

import java.util.Map;

public class LevelFormatUtil {
    private static Map<Integer, Level> levelMap;

    static {
        levelMap = NBattlePass.getInstance().getLevelConfiguration().getLevels();
    }

    public static String getExperienceBar(int currentExperience, int maxExperience) {
        int totalSticks = 100;

        int filledSticks = (int) ((double) currentExperience / maxExperience * totalSticks);
        int emptySticks = totalSticks - filledSticks;

        StringBuilder experienceBar = new StringBuilder();
        experienceBar.append("&6");
        for (int i = 0; i < filledSticks; i++) {
            experienceBar.append("|");
        }

        experienceBar.append("&7");
        for (int i = 0; i < emptySticks; i++) {
            experienceBar.append("|");
        }

        return experienceBar.toString();
    }

    public static Level getLevel(int exp) {
        if (exp <= 0) {
            return levelMap.values()
                    .stream()
                    .filter(level -> level.getNumber() == 1)
                    .findFirst()
                    .orElse(null);
        }

        Level level = null;
        Level maxLevel = null;

        for (Map.Entry<Integer, Level> entry : levelMap.entrySet()) {
            int requiredExp = entry.getKey();
            Level currentLevel = entry.getValue();

            if (maxLevel == null || currentLevel.getNeedExp() > maxLevel.getNeedExp()) {
                maxLevel = currentLevel;
            }

            if (requiredExp > exp && level == null) {
                level = currentLevel;
            }

            if (level != null && level.getNeedExp() > currentLevel.getNeedExp() && currentLevel.getNeedExp() >= exp) {
                level = currentLevel;
            }
        }

        return (level != null) ? level : maxLevel;
    }

    public static void setLevelMap(Map<Integer, Level> levelMap) {
        LevelFormatUtil.levelMap = levelMap;
    }
}
