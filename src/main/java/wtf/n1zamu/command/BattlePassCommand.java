package wtf.n1zamu.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.n1zamu.command.impl.*;
import wtf.n1zamu.inventory.BattlePassHolder;

import java.util.*;

public class BattlePassCommand implements CommandExecutor, TabCompleter {
    private final List<CommandObject> commandList = Arrays.asList(new GiveCommandObject(), new ClearCommandObject(), new SetExpCommandObject());

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            player.openInventory(new BattlePassHolder(player).getInventory());
            return false;
        }
        commandList.forEach(commandObject -> {
            if (args[0].equalsIgnoreCase(commandObject.name())) {
                commandObject.execute(sender, args);
            }
        });
        return false;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completes = new ArrayList<>();
        if (args.length == 1) {
            commandList.forEach(commandObject -> completes.add(commandObject.name()));
            return completes;
        }
        if (Arrays.asList("set", "give").contains(args[0])) {
            List<String> playerNames = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> playerNames.add(player.getName()));
            return playerNames;
        }
        return completes;
    }
}
