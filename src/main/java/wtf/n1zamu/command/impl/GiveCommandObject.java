package wtf.n1zamu.command.impl;

import org.bukkit.command.CommandSender;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.command.CommandObject;
import wtf.n1zamu.util.ConfigUtil;

public class GiveCommandObject implements CommandObject {
    @Override
    public String name() {
        return "give";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nbattlepass.admin")) {
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(ConfigUtil.getColoredString("message.giveUsage"));
            return;
        }

        String playerName = args[1];
        NBattlePass.getInstance().getPlayerDataBase().giveSubscribe(playerName);
        sender.sendMessage(ConfigUtil.getColoredString("message.successfullyGive"));
    }
}
