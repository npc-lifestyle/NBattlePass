package wtf.n1zamu.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import wtf.n1zamu.NBattlePass;

import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.util.QuestUtil;
import wtf.n1zamu.util.RandomizeUtil;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        giveQuest(player, QuestTime.DAY);
        giveQuest(player, QuestTime.WEEK);
    }

    private void giveQuest(Player player, QuestTime time) {
        if (!NBattlePass.getInstance().getQuestDataBase().getQuests(player.getName(), time).isEmpty()) {
            return;
        }
        while (NBattlePass.getInstance().getQuestDataBase().getQuests(player.getName(), time).size() < 3) {
            QuestObject questObject = RandomizeUtil.getRandomQuest(time);
            if (questObject.getType().isEntity()) {
                if (QuestUtil.getEntitiesByQuests(player, questObject.getType()).containsKey(questObject.getEntityType()))
                    continue;
            } else {
                if (QuestUtil.getMaterialsByQuests(player, questObject.getType()).containsKey(questObject.getMaterial()))
                    continue;
            }
            if (QuestUtil.getQuestsByType(player, questObject.getType()).contains(questObject)) {
                continue;
            }
            NBattlePass.getInstance().getQuestDataBase().giveQuest(player.getName(), questObject);
        }
    }
}
