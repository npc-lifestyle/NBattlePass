package wtf.n1zamu.listener;

import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import wtf.n1zamu.inventory.BattlePassHolder;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void on(InventoryClickEvent event) {
        val clicker = event.getWhoClicked();
        Player player = (Player) clicker;
        val openInventory = player.getOpenInventory().getTopInventory();
        if (!(openInventory.getHolder() instanceof BattlePassHolder)) {
            return;
        }
        event.setCancelled(true);
    }
}
