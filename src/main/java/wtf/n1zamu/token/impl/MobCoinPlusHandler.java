package wtf.n1zamu.token.impl;

import com.chup.mobcoinsplus.extras.Extras;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wtf.n1zamu.token.TokenHandler;

public class MobCoinPlusHandler implements TokenHandler {
    @Override
    public void handle(String name, int amount) {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) {
            return;
        }
        Extras.giveCoins(player, amount);
    }
}
