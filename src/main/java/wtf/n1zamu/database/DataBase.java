package wtf.n1zamu.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import wtf.n1zamu.quest.QuestObject;

import java.sql.Connection;
import java.util.List;

public interface DataBase {
    Cache<String, List<QuestObject>> questsCache = CacheBuilder.newBuilder()
            .build();

    void connect();

    void disconnect();

    Connection getConnection();

    default Cache<String, List<QuestObject>> getQuestsCache() {
        return questsCache;
    }
}
