package me.youhavetrouble.thegreatmotivator;

import me.youhavetrouble.moneypit.Economy;
import me.youhavetrouble.thegreatmotivator.storage.SQLiteStorage;
import me.youhavetrouble.thegreatmotivator.storage.TGMStorage;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class TheGreatMotivator extends JavaPlugin {

    private static Economy economy = null;
    private TGMConfig config;
    private TGMStorage storage;

    @Override
    public void onEnable() {

        reloadTGMConfig();

        storage = switch (config.getDatabaseType()) {
            case SQLITE -> new SQLiteStorage();
            case MYSQL -> null;
            default -> null;
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

        if (getServer().getPluginManager().isPluginEnabled("MoneyPit") ) {
            getLogger().info("MoneyPit not detected");
            getServer().getServicesManager().register(Economy.class, new TGMEconomy(this), this, ServicePriority.Highest);
        } else {
            getLogger().info("MoneyPit detected");
        }

    }

    @Override
    public void onDisable() {
        getLogger().info("Saving player data...");
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
