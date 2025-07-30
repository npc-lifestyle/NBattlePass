package wtf.n1zamu.quest;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.util.ConfigUtil;
import wtf.n1zamu.util.LevelFormatUtil;

public interface Quest<E extends Event> {
    Runnable getExecutionTask(@NonNull E event);

    default void update(QuestObject quest, Player player, int addedExp) {
        int current = NBattlePass.getInstance().getQuestDataBase().getCurrentProgress(player.getName(), quest);
        NBattlePass.getInstance().getQuestDataBase().updateProgress(player.getName(), quest, NBattlePass.getInstance().getQuestDataBase().getCurrentProgress(player.getName(), quest) + addedExp);
        if (quest.getNeedExp() <= current + addedExp) {
            boolean isLevelUp;
            NBattlePass.getInstance().getQuestDataBase().updateProgress(player.getName(), quest, quest.getNeedExp());
            NBattlePass.getInstance().getQuestDataBase().completeQuest(player.getName(), quest);
            int reward = quest.getTime() == QuestTime.DAY ? ConfigUtil.getInt("expForDaily") : ConfigUtil.getInt("expForWeek");
            int tokens = quest.getTime() == QuestTime.DAY ? ConfigUtil.getInt("tokensForDaily") : ConfigUtil.getInt("tokensForWeek");
            isLevelUp = LevelFormatUtil.getLevel(NBattlePass.getInstance().getPlayerDataBase().getExp(player.getName())).getNumber() < LevelFormatUtil.getLevel(NBattlePass.getInstance().getPlayerDataBase().getExp(player.getName()) + reward).getNumber();
            if (isLevelUp) {
                NBattlePass.getInstance().getPlayerDataBase().upLevel(player.getName(), LevelFormatUtil.getLevel(NBattlePass.getInstance().getPlayerDataBase().getExp(player.getName())).getNumber());
            }
            NBattlePass.getInstance().getPlayerDataBase().setExp(player.getName(), NBattlePass.getInstance().getPlayerDataBase().getExp(player.getName()) + reward);
            NBattlePass.getInstance().getHandler().handle(player.getName(), tokens);
        }
    }
}

