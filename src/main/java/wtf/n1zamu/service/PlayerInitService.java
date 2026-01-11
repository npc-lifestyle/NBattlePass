package wtf.n1zamu.service;

import lombok.val;
import org.bukkit.entity.Player;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.database.impl.PlayerDatabase;
import wtf.n1zamu.database.impl.quest.QuestDatabase;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.enums.QuestTime;
import wtf.n1zamu.util.RandomizeUtility;

import java.util.Arrays;
import java.util.List;

public final class PlayerInitService {

    private final PlayerDatabase playerDb =
            NBattlePass.getInstance().getPlayerDataBase();

    private final QuestDatabase questDb =
            NBattlePass.getInstance().getQuestDatabase();

    public void init(Player player) {
        String name = player.getName();
        List<QuestObject> quests = questDb.getQuests(player.getName(), QuestTime.DAY);
        if (!quests.isEmpty()) return;
        playerDb.addNewPlayer(name);
        Arrays.stream(QuestTime.values()).forEach(time -> initQuests(name, time));
    }

    private void initQuests(String player, QuestTime time) {
        for (int i = 0; i < 3; i++) {
            val quest = RandomizeUtility.getRandomQuest(time);
            if (questDb.hasQuest(player, quest.getType())) {
                i--;
                continue;
            }
            questDb.giveQuest(
                    player, quest
            );
        }
    }
}