package me.youhavetrouble.thegreatmotivator;

import me.youhavetrouble.moneypit.Economy;
import me.youhavetrouble.moneypit.EconomyResponse;
import me.youhavetrouble.thegreatmotivator.storage.SQLiteStorage;
import me.youhavetrouble.thegreatmotivator.storage.TGMStorage;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

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

    private final ConcurrentHashMap<UUID, TGMPlayerBalance> cachedBalances = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, CompletableFuture<TGMPlayerBalance>> currentlyLoading = new ConcurrentHashMap<>();

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
                        storage.savePlayerBalance(tgmPlayerBalance.getUuid(), tgmPlayerBalance.getBalance());
                    }
                    Player player = getServer().getPlayer(tgmPlayerBalance.getUuid());
                    if (player != null) return;
                    if (System.currentTimeMillis() - tgmPlayerBalance.getLastAccess() > TimeUnit.MINUTES.toMillis(2)) {
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

    /**
     * Gets the player's balance from the cache, or loads it from the database if it's not cached.
     * @param offlinePlayer The player to get the balance of.
     * @return A CompletableFuture that will be completed with the player's balance.
     */
    protected CompletableFuture<TGMPlayerBalance> getPlayerBalance(OfflinePlayer offlinePlayer) {
        if (cachedBalances.containsKey(offlinePlayer.getUniqueId())) {
            return CompletableFuture.completedFuture(cachedBalances.get(offlinePlayer.getUniqueId()));
        }
        if (currentlyLoading.containsKey(offlinePlayer.getUniqueId())) {
            return currentlyLoading.get(offlinePlayer.getUniqueId()); // return the future that's already loading
        }
        CompletableFuture<TGMPlayerBalance> future = CompletableFuture.supplyAsync(() -> {
            Long balance = storage.getPlayerBalance(offlinePlayer.getUniqueId());
            if (balance == null) return null;
            TGMPlayerBalance tgmPlayerBalance = new TGMPlayerBalance(offlinePlayer.getUniqueId(), balance);
            cachedBalances.put(offlinePlayer.getUniqueId(), tgmPlayerBalance);
            currentlyLoading.remove(offlinePlayer.getUniqueId());
            return tgmPlayerBalance;
        });
        currentlyLoading.put(offlinePlayer.getUniqueId(), future);
        return future;
    }

    public CompletableFuture<EconomyResponse> setPlayerBalance(OfflinePlayer offlinePlayer, long balance) {
        return getPlayerBalance(offlinePlayer).thenApply(tgmBalance -> {
            if (tgmBalance == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
            tgmBalance.setBalance(balance);
            return new EconomyResponse(balance, balance, EconomyResponse.ResponseType.SUCCESS, null);
        });
    }

    public CompletableFuture<EconomyResponse> addToPlayersBalance(OfflinePlayer offlinePlayer, long toAdd) {
        return getPlayerBalance(offlinePlayer).thenApply(tgmBalance -> {
            if (tgmBalance == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
            tgmBalance.setBalance(tgmBalance.getBalance() + toAdd);
            return new EconomyResponse(toAdd, tgmBalance.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
        });
    }

    public CompletableFuture<EconomyResponse> withdrawFromPlayersBalance(OfflinePlayer offlinePlayer, long toSubtract) {
        return getPlayerBalance(offlinePlayer).thenApply(tgmBalance -> {
            if (tgmBalance == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
            if (tgmBalance.getBalance() < toSubtract) return new EconomyResponse(0, tgmBalance.getBalance(), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
            tgmBalance.setBalance(tgmBalance.getBalance() - toSubtract);
            return new EconomyResponse(toSubtract, tgmBalance.getBalance(), EconomyResponse.ResponseType.SUCCESS, null);
        });
    }

}
