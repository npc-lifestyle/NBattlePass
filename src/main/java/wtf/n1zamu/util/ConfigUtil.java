package wtf.n1zamu.util;

import org.bukkit.ChatColor;
import wtf.n1zamu.NBattlePass;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {
    public static String getColoredString(String path) {
        return ChatColor.translateAlternateColorCodes('&', NBattlePass.getInstance().getConfig().getString(path));
    }

    public static String getRawString(String path) {
        return NBattlePass.getInstance().getConfig().getString(path);
    }

    public static List<String> getColoredList(String path) {
        List<String> formattedList = new ArrayList<>();
        NBattlePass.getInstance().getConfig().getStringList(path)
                .forEach(line -> formattedList.add(ChatColor.translateAlternateColorCodes('&', line)));
        return formattedList;
    }

    public static int getInt(String path) {
        return NBattlePass.getInstance().getConfig().getInt(path);
    }
}
