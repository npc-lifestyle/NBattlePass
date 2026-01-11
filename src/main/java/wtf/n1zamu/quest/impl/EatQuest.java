package wtf.n1zamu.quest.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.jetbrains.annotations.NotNull;
import wtf.n1zamu.quest.Quest;
import wtf.n1zamu.quest.QuestProgressHandler;
import wtf.n1zamu.quest.enums.QuestType;

public class EatQuest implements Quest<PlayerItemConsumeEvent> {
    @Override
    public void handle(@NotNull PlayerItemConsumeEvent event, QuestProgressHandler handler) {
        Player player = event.getPlayer();
        Material eaten = event.getItem().getType();

        getQuests(player, QuestType.EAT)
                .stream()
                .filter(q -> q.getMaterial() == eaten)
                .forEach(q -> handler.addProgress(player, q, 1));
    }
}
