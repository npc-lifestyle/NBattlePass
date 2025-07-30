package wtf.n1zamu.command;

import org.bukkit.command.CommandSender;

public interface CommandObject {
    String name();

    void execute(CommandSender sender, String[] args);
}
