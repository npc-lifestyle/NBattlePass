package wtf.n1zamu.quest;

import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.NBattlePass;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestTime;
import wtf.n1zamu.quest.enums.QuestType;

import java.util.ArrayList;
import java.util.List;

public interface Quest<E extends Event> {

    void handle(@NonNull E event, QuestProgressHandler handler);

    default List<QuestObject> getQuests(Player player, QuestType type) {
        List<QuestObject> dayQuests = NBattlePass.getInstance()
                .getQuestDatabase()
                .getQuests(player.getName(), QuestTime.DAY);
        List<QuestObject> weekQuests = NBattlePass.getInstance()
                .getQuestDatabase()
                .getQuests(player.getName(), QuestTime.WEEK);
        List<QuestObject> allQuests = new ArrayList<>();
        allQuests.addAll(dayQuests);
        allQuests.addAll(weekQuests);
        return allQuests
                .stream()
                .filter(q -> q.getType() == type
                        && q.getStatus() != QuestStatus.PASSED
                )
                .toList();
    }
}


