package wtf.n1zamu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.n1zamu.command.BattlePassCommand;
import wtf.n1zamu.config.impl.LevelConfiguration;
import wtf.n1zamu.database.DataBase;
import wtf.n1zamu.database.player.PlayerDataBase;
import wtf.n1zamu.database.player.impl.MySQLDataBase;
import wtf.n1zamu.database.player.impl.PostgressDataBase;
import wtf.n1zamu.database.player.impl.SQLiteDataBase;
import wtf.n1zamu.database.quest.QuestDataBase;
import wtf.n1zamu.listener.ActionListener;
import wtf.n1zamu.listener.InventoryClickListener;
import wtf.n1zamu.listener.PlayerJoinListener;
import wtf.n1zamu.placeholder.BattlePassPlaceHolder;
import wtf.n1zamu.quest.impl.*;
import wtf.n1zamu.quest.time.QuestTime;
import wtf.n1zamu.token.TokenHandler;
import wtf.n1zamu.token.impl.MobCoinPlusHandler;
import wtf.n1zamu.token.impl.TokenManagerHandler;
import wtf.n1zamu.util.TimeUtil;

import java.util.Arrays;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PUBLIC)
@Getter
public final class NBattlePass extends JavaPlugin {
    static NBattlePass INSTANCE;
    PlayerDataBase playerDataBase;
    QuestDataBase questDataBase;
    LevelConfiguration levelConfiguration;
    Map<String, Integer> playerExp;
    TokenHandler handler;

    @Override
    public void onEnable() {
        INSTANCE = this;
        try {
            Class.forName("com.chup.mobcoinsplus.Main");
            handler = new MobCoinPlusHandler();
        } catch (ClassNotFoundException e) {
            handler = new TokenManagerHandler();
        }
        playerDataBase = (PlayerDataBase) getByConfig("player");
        questDataBase = (QuestDataBase) getByConfig("quests");
        saveDefaultConfig();
        saveResource("levels.yml", false);
        levelConfiguration = new LevelConfiguration();
        getCommand("nBattlePass").setExecutor(new BattlePassCommand());
        getCommand("nBattlePass").setTabCompleter(new BattlePassCommand());
        Arrays.asList(playerDataBase, questDataBase).forEach(DataBase::connect);
        playerExp = playerDataBase.getPlayerExp();
        Arrays.asList(new InventoryClickListener(), new PlayerJoinListener(),
                        new ActionListener(new BlockBreakQuest(), new BlockPlaceQuest(), new CraftQuest(), new KillQuest(), new EatQuest(), new ExperienceQuest()))
                .forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        new BattlePassPlaceHolder().register();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> playerExp = playerDataBase.getPlayerExp(), 0, 300 * 20L);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            this.getQuestDataBase().getQuestsCache().asMap().clear();
            this.getQuestDataBase().fillCache();
            Bukkit.getLogger().info("[NBattlePass] Перезапись кэша!");
        }, 20 * 60, 15 * 60 * 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> this.getQuestDataBase().clear(QuestTime.DAY), TimeUtil.calculateTimeToNextDay() * 20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> this.getQuestDataBase().clear(QuestTime.WEEK), TimeUtil.calculateTimeToSaturday() * 20L);
    }

    @Override
    public void onDisable() {
        Arrays.asList(playerDataBase, questDataBase).forEach(DataBase::disconnect);
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static NBattlePass getInstance() {
        return INSTANCE;
    }

    private DataBase getByConfig(String type) {
        String dbType = getConfig().getString("dataBase");
        if (getConfig().getString("dataBase") == null) {
            Bukkit.getLogger().info("Ошибка");
            return null;
        }
        DataBase dataBase = null;

        switch (dbType.toLowerCase()) {
            case "sqlite":
                dataBase = type.equalsIgnoreCase("player") ? new SQLiteDataBase() : new wtf.n1zamu.database.quest.impl.SQLiteDataBase();
                break;
            case "mysql":
                dataBase = type.equalsIgnoreCase("player") ? new MySQLDataBase() : new wtf.n1zamu.database.quest.impl.MySQLDataBase();
                break;
            case "postgress":
                dataBase = type.equalsIgnoreCase("player") ? new PostgressDataBase() : new wtf.n1zamu.database.quest.impl.PostgressDataBase();
                break;
            default:
                Bukkit.getLogger().severe("[NBattlePass] Неизвестный тип базы данных: " + dbType);
                break;
        }

        return dataBase;
    }
}
