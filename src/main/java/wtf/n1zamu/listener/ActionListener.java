package wtf.n1zamu.listener;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import lombok.AllArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;

import org.bukkit.event.player.PlayerItemConsumeEvent;
import wtf.n1zamu.quest.impl.*;

@AllArgsConstructor
public class ActionListener implements Listener {
    private final BlockBreakQuest blockBreakQuest;
    private final BlockPlaceQuest blockPlaceQuest;
    private final CraftQuest craftQuest;
    private final KillQuest killQuest;
    private final EatQuest eatQuest;
    private final ExperienceQuest experienceQuest;

    @EventHandler
    public void on(PlayerPickupExperienceEvent event) {
        experienceQuest.getExecutionTask(event).run();
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        blockBreakQuest.getExecutionTask(event).run();
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        killQuest.getExecutionTask(event).run();
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        blockPlaceQuest.getExecutionTask(event).run();
    }

    @EventHandler
    public void on(CraftItemEvent event) {
       craftQuest.getExecutionTask(event).run();
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
       eatQuest.getExecutionTask(event).run();
    }
}
