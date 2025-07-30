package wtf.n1zamu.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wtf.n1zamu.NBattlePass;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BattlePassPlaceHolder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "nBattlePass";
    }

    @Override
    public @NotNull String getAuthor() {
        return "n1zamu";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.startsWith("top_exp_")) {
            return getTopPlayer(identifier, Integer.parseInt(identifier.replace("top_exp_", "")));
        }
        if (identifier.startsWith("top_name_")) {
            return getTopPlayer(identifier, Integer.parseInt(identifier.replace("top_name_", "")));

        }
        return identifier;
    }

    private String getTopPlayer(String identifier, int place) {
        Map<String, Integer> playerMap = NBattlePass.getInstance().getPlayerExp();
        if (playerMap.isEmpty()) return ChatColor.GRAY + "Loading...";

        List<Map.Entry<String, Integer>> sortedPlayers = playerMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        if (place < 1 || place > sortedPlayers.size()) return ChatColor.GRAY + "Loading...";

        Map.Entry<String, Integer> selectedEntry = sortedPlayers.get(place - 1);
        String name = selectedEntry.getKey();
        Integer exp = selectedEntry.getValue();

        if (identifier.contains("exp")) {
            return String.valueOf(exp);
        }

        return name;
    }
}
