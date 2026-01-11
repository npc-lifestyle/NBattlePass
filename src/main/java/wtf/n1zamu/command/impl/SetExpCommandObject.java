package wtf.n1zamu.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.command.CommandObject;
import wtf.n1zamu.util.ConfigUtility;
import wtf.n1zamu.util.ProgressUtility;

public class SetExpCommandObject implements CommandObject {
    @Override
    public String name() {
        return "set";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nbattlepass.admin")) {
            return;
        }
        if (args.length != 3) {
            sender.sendMessage(ConfigUtility.getColoredString("message.setUsage"));
            return;
        }
        String player = args[1];
        int exp;
        try {
            exp = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ConfigUtility.getColoredString("messages.notDigit"));
            return;
        }
        sender.sendMessage(ConfigUtility.getColoredString("message.successfullySet").replace("%player%", player).replace("%exp%", String.valueOf(exp)));
    }
}
