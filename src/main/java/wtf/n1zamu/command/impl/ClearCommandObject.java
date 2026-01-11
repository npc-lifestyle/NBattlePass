package wtf.n1zamu.command.impl;

import org.bukkit.command.CommandSender;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.command.CommandObject;
import wtf.n1zamu.quest.enums.QuestTime;
import wtf.n1zamu.util.ConfigUtility;

public class ClearCommandObject implements CommandObject {
    @Override
    public String name() {
        return "clear";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nbattlepass.admin")) {
            return;
        }
        sender.sendMessage(ConfigUtility.getColoredString("message.successfullyClear"));
    }
}
