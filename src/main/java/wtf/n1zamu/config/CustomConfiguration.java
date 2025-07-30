package wtf.n1zamu.config;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.n1zamu.NBattlePass;

import java.io.File;

public interface CustomConfiguration {
    @NonNull
    String getName();

    @NonNull
    FileConfiguration getConfig();

    @SneakyThrows
    default void setUp() {
        File configFile = new File(NBattlePass.getInstance().getDataFolder(), getName() + ".yml");
        if (configFile.exists()) {
            return;
        }
        configFile.getParentFile().mkdirs();
        configFile.createNewFile();
    }
}

