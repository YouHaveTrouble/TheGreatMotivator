package me.youhavetrouble.thegreatmotivator;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class TGMConfig {

    private final DatabaseType databaseType;

    protected TGMConfig(FileConfiguration config) {
        String databaseString = config.getString("database", "sqlite");
        databaseType = DatabaseType.valueOf(databaseString.toUpperCase(Locale.ENGLISH));
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public enum DatabaseType {

        SQLITE,
        MYSQL,

    }

}
