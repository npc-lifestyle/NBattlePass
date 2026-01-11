package wtf.n1zamu.database.impl.quest;

import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.enums.QuestTime;
import wtf.n1zamu.quest.enums.QuestType;

public record QuestKey(QuestTime time, QuestType type, String object) {

    public static QuestKey from(QuestObject q) {
        return new QuestKey(
                q.getTime(),
                q.getType(),
                q.getEntityType() != null ? q.getEntityType().name() : q.getMaterial().name()
        );
    }
}

