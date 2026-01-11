package wtf.n1zamu.command;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.command.impl.*;

import java.util.*;

public class BattlePassCommand implements CommandExecutor, TabCompleter {
    private final List<CommandObject> commandList = Arrays.asList(new GiveCommandObject(), new ClearCommandObject(), new SetExpCommandObject());


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            val menu = NBattlePass.getInstance().getInventoryManager().show("main-gui", player);
            player.openInventory(menu);
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

    public void register() {
        val command = Bukkit.getPluginCommand("nbattlepass");
        if (command == null) {
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
    }
}
