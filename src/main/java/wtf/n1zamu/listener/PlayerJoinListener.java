package wtf.n1zamu.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import wtf.n1zamu.service.PlayerInitService;

public class PlayerJoinListener implements Listener {
    PlayerInitService playerInitService;

    public PlayerJoinListener() {
        this.playerInitService = new PlayerInitService();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerInitService.init(event.getPlayer());
    }
}
