package wtf.n1zamu.quest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wtf.n1zamu.quest.status.QuestStatus;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.quest.type.QuestType;

@Getter
@ToString
@Setter
public class QuestObject {
    private int currentExp;
    private int needExp;
    private final QuestType type;
    private final QuestTime time;
    private QuestStatus status;
    private Material material;
    private EntityType entityType;

    public QuestObject(int currentExp, int needExp, QuestType type, QuestTime time, QuestStatus status, Material material) {
        this.currentExp = currentExp;
        this.type = type;
        this.needExp = needExp;
        this.time = time;
        this.status = status;
        this.material = material;
    }

    public QuestObject(int currentExp, int needExp, QuestType type, QuestTime time, QuestStatus status, EntityType entityType) {
        this.currentExp = currentExp;
        this.needExp = needExp;
        this.time = time;
        this.type = type;
        this.status = status;
        this.entityType = entityType;
    }
}
