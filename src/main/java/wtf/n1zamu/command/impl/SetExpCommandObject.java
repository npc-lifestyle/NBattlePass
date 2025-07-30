package wtf.n1zamu.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.command.CommandObject;
import wtf.n1zamu.util.ConfigUtil;
import wtf.n1zamu.util.LevelFormatUtil;

public class SetExpCommandObject implements CommandObject {
    @Override
    public String name() {
        return "set";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nBattlePass.admin")) {
            return;
        }
        if (args.length != 3) {
            sender.sendMessage(ConfigUtil.getColoredString("message.setUsage"));
            return;
        }
        String player = args[1];
        int exp;
        try {
            exp = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ConfigUtil.getColoredString("messages.notDigit"));
            return;
        }
        boolean isLevelUp = LevelFormatUtil.getLevel(NBattlePass.getInstance().getPlayerDataBase().getExp(player)).getNumber() < LevelFormatUtil.getLevel(NBattlePass.getInstance().getPlayerDataBase().getExp(player) + exp).getNumber();
        if (isLevelUp) {
            NBattlePass.getInstance().getPlayerDataBase().upLevel(player, LevelFormatUtil.getLevel(NBattlePass.getInstance().getPlayerDataBase().getExp(player)).getNumber());
            Bukkit.getLogger().info("Уровень игрока повышен! Новый уровень " + LevelFormatUtil.getLevel(NBattlePass.getInstance().getPlayerDataBase().getExp(player)).getNumber());
        }
        NBattlePass.getInstance().getPlayerDataBase().setExp(player, exp);
        sender.sendMessage(ConfigUtil.getColoredString("message.successfullySet").replace("%player%", player).replace("%exp%", String.valueOf(exp)));
    }
}
