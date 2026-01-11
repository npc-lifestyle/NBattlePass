package wtf.n1zamu.listener;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;

import org.bukkit.event.player.PlayerItemConsumeEvent;
import wtf.n1zamu.quest.QuestProgressHandler;
import wtf.n1zamu.quest.impl.*;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ActionListener implements Listener {
    BlockBreakQuest blockBreakQuest;
    BlockPlaceQuest blockPlaceQuest;
    CraftQuest craftQuest;
    KillQuest killQuest;
    EatQuest eatQuest;
    ExperienceQuest experienceQuest;
    QuestProgressHandler handler;

    @EventHandler
    public void on(PlayerPickupExperienceEvent event) {
        experienceQuest.handle(event, handler);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        blockBreakQuest.handle(event, handler);
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        killQuest.handle(event, handler);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        blockPlaceQuest.handle(event, handler);
    }

    @EventHandler
    public void on(CraftItemEvent event) {
        craftQuest.handle(event, handler);
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        eatQuest.handle(event, handler);
    }
}
