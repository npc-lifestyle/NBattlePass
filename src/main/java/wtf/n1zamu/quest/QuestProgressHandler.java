package wtf.n1zamu.quest;

import lombok.val;
import org.bukkit.entity.Player;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.database.impl.quest.QuestDatabase;
import wtf.n1zamu.level.LevelConfiguration;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestTime;
import wtf.n1zamu.util.ConfigUtility;

public final class QuestProgressHandler {

    private final QuestDatabase db = NBattlePass.getInstance().getQuestDatabase();

    public void addProgress(Player player, QuestObject quest, int delta) {
        if (quest.getStatus() == QuestStatus.PASSED || delta <= 0) {
            return;
        }

        int newProgress = Math.min(
                quest.getCurrentExp() + delta,
                quest.getNeedExp()
        );

        db.updateProgress(player.getName(), quest, newProgress);

        if (newProgress >= quest.getNeedExp()) {
            complete(player, quest);
        }
    }

    private void complete(Player player, QuestObject quest) {
        db.completeQuest(player.getName(), quest);

        int rewardExp = quest.getTime() == QuestTime.DAY
                ? ConfigUtility.getInt("expForDaily")
                : ConfigUtility.getInt("expForWeek");

        int tokens = quest.getTime() == QuestTime.DAY
                ? ConfigUtility.getInt("tokensForDaily")
                : ConfigUtility.getInt("tokensForWeek");

        var playerDb = NBattlePass.getInstance().getPlayerDataBase();

        int oldExp = playerDb.getExp(player.getName());
        int newExp = oldExp + rewardExp;

        val oldLevel = LevelConfiguration.getLevel(oldExp);
        val newLevel = LevelConfiguration.getLevel(newExp);


        boolean levelUp = oldLevel != null
                && newLevel != null
                && oldLevel.getNumber() < newLevel.getNumber();

        if (levelUp) {
            playerDb.upLevel(
                    player.getName(),
                    oldLevel.getNumber()
            );
        }

        playerDb.setExp(player.getName(), newExp);
        NBattlePass.getInstance().getHandler().handle(player.getName(), tokens);
    }
}