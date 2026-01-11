package wtf.n1zamu.database.impl.quest;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wtf.n1zamu.quest.QuestObject;
import wtf.n1zamu.quest.enums.QuestStatus;
import wtf.n1zamu.quest.enums.QuestTime;
import wtf.n1zamu.quest.enums.QuestType;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class QuestMapper {

    public static QuestObject from(ResultSet rs) throws SQLException {
        QuestType type = QuestType.valueOf(rs.getString("type"));
        QuestTime time = QuestTime.valueOf(rs.getString("time"));
        QuestStatus status = QuestStatus.valueOf(rs.getString("state"));
        int current = rs.getInt("currentExp");
        int need = rs.getInt("needExp");
        String obj = rs.getString("object");

        return type.isEntity()
                ? new QuestObject(current, need, type, time, status, EntityType.valueOf(obj))
                : new QuestObject(current, need, type, time, status, Material.valueOf(obj));
    }

    public static String objectName(QuestObject q) {
        return q.getEntityType() != null
                ? q.getEntityType().name()
                : q.getMaterial().name();
    }
}

