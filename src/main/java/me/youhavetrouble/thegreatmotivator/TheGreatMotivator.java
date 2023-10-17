package me.youhavetrouble.thegreatmotivator;

import me.youhavetrouble.moneypit.Economy;
import me.youhavetrouble.thegreatmotivator.storage.SQLiteStorage;
import me.youhavetrouble.thegreatmotivator.storage.TGMStorage;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TheGreatMotivator extends JavaPlugin implements Listener {

    private TGMConfig config;
    private static TGMStorage storage;

    private static final ConcurrentHashMap<UUID, TGMPlayerBalance> cachedBalances = new ConcurrentHashMap<>();

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
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (getServer().getPluginManager().isPluginEnabled("MoneyPit") ) {
            getLogger().info("MoneyPit detected, registering economy service");
            getServer().getServicesManager().register(Economy.class, new TGMEconomy(this), this, ServicePriority.Highest);
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Iterator<TGMPlayerBalance> iterator = cachedBalances.values().iterator();
                while (iterator.hasNext()) {
                    TGMPlayerBalance tgmPlayerBalance = iterator.next();
                    if (tgmPlayerBalance.isDirty()) {
                        // TODO save data
                    }
                    if (System.currentTimeMillis() - tgmPlayerBalance.getLastAccess() > TimeUnit.MINUTES.toMillis(5)) {
                        iterator.remove();
                    }
                }
            }
        }, TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(10));

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

    protected static TGMStorage getStorage() {
        return storage;
    }

    public static CompletableFuture<Long> getPlayerBalance(OfflinePlayer offlinePlayer) {
        if (cachedBalances.containsKey(offlinePlayer.getUniqueId())) {
            return CompletableFuture.completedFuture(cachedBalances.get(offlinePlayer.getUniqueId()).getBalance());
        }
        return CompletableFuture.supplyAsync(() -> {
            Long balance = storage.getPlayerBalance(offlinePlayer.getUniqueId());
            if (balance == null) return null;
            TGMPlayerBalance tgmPlayerBalance = new TGMPlayerBalance(offlinePlayer.getUniqueId(), balance);
            cachedBalances.put(offlinePlayer.getUniqueId(), tgmPlayerBalance);
            return balance;
        });
    }

}
