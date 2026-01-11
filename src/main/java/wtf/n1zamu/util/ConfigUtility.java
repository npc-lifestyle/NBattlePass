package wtf.n1zamu.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import wtf.n1zamu.NBattlePass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ConfigUtility {
    public String getColoredString(String path) {
        return ChatColor.translateAlternateColorCodes('&', NBattlePass.getInstance().getConfig().getString(path));
    }

    public String getRawString(String path) {
        return NBattlePass.getInstance().getConfig().getString(path);
    }

    public int getInt(String path) {
        return NBattlePass.getInstance().getConfig().getInt(path);
    }
}
