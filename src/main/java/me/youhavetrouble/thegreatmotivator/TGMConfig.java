package me.youhavetrouble.thegreatmotivator;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class TGMConfig {

    private final DatabaseType databaseType;
    private final int saveInterval;

    protected TGMConfig(FileConfiguration config) {
        String databaseString = config.getString("database", "sqlite");
        databaseType = DatabaseType.valueOf(databaseString.toUpperCase(Locale.ENGLISH));
        saveInterval = config.getInt("saveInterval", 10);
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    enum DatabaseType {

        SQLITE,
        MYSQL,

    }

}
