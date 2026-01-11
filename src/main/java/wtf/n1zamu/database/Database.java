package wtf.n1zamu.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import wtf.n1zamu.quest.QuestObject;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Database {
    Cache<String, List<QuestObject>> questsCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    protected abstract void connect();

    protected abstract void disconnect();

    protected abstract Connection getConnection();

    protected Cache<String, List<QuestObject>> getQuestsCache() {
        return questsCache;
    }
}