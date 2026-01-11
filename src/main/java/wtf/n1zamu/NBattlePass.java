package wtf.n1zamu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.n1zamu.command.BattlePassCommand;
import wtf.n1zamu.database.impl.PlayerDatabase;
import wtf.n1zamu.database.impl.quest.QuestDatabase;
import wtf.n1zamu.level.LevelConfiguration;

import wtf.n1zamu.inventory.InventoryManager;
import wtf.n1zamu.listener.ActionListener;
import wtf.n1zamu.listener.InventoryClickListener;
import wtf.n1zamu.listener.PlayerJoinListener;
import wtf.n1zamu.placeholder.BattlePassPlaceHolder;
import wtf.n1zamu.quest.QuestProgressHandler;
import wtf.n1zamu.quest.impl.*;

import wtf.n1zamu.token.TokenHandler;
import wtf.n1zamu.token.impl.MobCoinPlusHandler;
import wtf.n1zamu.token.impl.TokenManagerHandler;

import java.util.Arrays;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class NBattlePass extends JavaPlugin {
    PlayerDatabase playerDataBase;
    QuestDatabase questDatabase;
    static NBattlePass INSTANCE;
    InventoryManager inventoryManager;
    LevelConfiguration levelConfiguration;
    TokenHandler handler;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.handler = Bukkit.getPluginManager().isPluginEnabled("TokenManager") ? new TokenManagerHandler() : new MobCoinPlusHandler();
        this.playerDataBase = new PlayerDatabase();
        this.questDatabase = new QuestDatabase();
        playerDataBase.connect();
        questDatabase.connect();
        saveDefaultConfig();
        this.loadManagers();
        this.registerCommands();
        this.registerListeners();
        new BattlePassPlaceHolder().register();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void registerCommands() {
        BattlePassCommand battlePassCommand = new BattlePassCommand();
        battlePassCommand.register();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new ActionListener(
                new BlockBreakQuest(),
                new BlockPlaceQuest(),
                new CraftQuest(),
                new KillQuest(),
                new EatQuest(),
                new ExperienceQuest(),
                new QuestProgressHandler()
        ), this);
    }

    private void loadManagers() {
        this.levelConfiguration = new LevelConfiguration();
        this.inventoryManager = new InventoryManager();
        this.inventoryManager.load();
    }


    public static NBattlePass getInstance() {
        return INSTANCE;
    }
}
