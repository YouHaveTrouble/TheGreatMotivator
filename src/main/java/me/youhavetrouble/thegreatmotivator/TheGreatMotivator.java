package me.youhavetrouble.thegreatmotivator;

import me.youhavetrouble.thegreatmotivator.storage.SQLiteStorage;
import me.youhavetrouble.thegreatmotivator.storage.TGMStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public final class TheGreatMotivator extends JavaPlugin {

    private TGMConfig config;
    private TGMStorage storage;

    @Override
    public void onEnable() {

        reloadTGMConfig();

        storage = switch (config.getDatabaseType()) {
            case SQLITE -> new SQLiteStorage();
            case MYSQL -> null;
        };

        if (storage == null) {
            getLogger().severe("Invalid storage method chosen. Disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            storage.createTables();
        } catch (SQLException e) {
            getLogger().severe("Cannot connect to database. Disabling.");
            return;
        }

        getServer().getPluginManager().registerEvents(new PlayerTrackerListener(), this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Map.Entry<UUID, TGMPlayer> entry : TGMPlayer.getPlayerCache().entrySet()) {
                if (entry.getValue().isDirty()) {
                    storage.savePlayerBalance(entry.getKey(), entry.getValue().getBalance());
                }
                if (Bukkit.getPlayer(entry.getKey()) == null) {
                    TGMPlayer.untrackPlayer(entry.getKey());
                }
            }
        }, config.getSaveInterval() * 20L, config.getSaveInterval() * 20L);

    }

    @Override
    public void onDisable() {
        getLogger().info("Saving player data...");
        for (Map.Entry<UUID, TGMPlayer> entry : TGMPlayer.getPlayerCache().entrySet()) {
            if (entry.getValue().isDirty()) {
                storage.savePlayerBalance(entry.getKey(), entry.getValue().getBalance());
            }
        }
    }

    private void reloadTGMConfig() {
        saveDefaultConfig();
        reloadConfig();
        config = new TGMConfig(getConfig());
    }

    protected TGMStorage getStorage() {
        return storage;
    }
}
