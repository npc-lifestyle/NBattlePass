package wtf.n1zamu.command.impl;

import org.bukkit.command.CommandSender;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.command.CommandObject;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.util.ConfigUtil;

public class ClearCommandObject implements CommandObject {
    @Override
    public String name() {
        return "clear";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nBattlePass.admin")) {
            return;
        }
        NBattlePass.getInstance().getPlayerDataBase().clear();
        NBattlePass.getInstance().getQuestDataBase().clear(QuestTime.DAY);
        NBattlePass.getInstance().getQuestDataBase().clear(QuestTime.WEEK);
        NBattlePass.getInstance().getQuestDataBase().getQuestsCache().asMap().clear();
        sender.sendMessage(ConfigUtil.getColoredString("message.successfullyClear"));
    }
}
