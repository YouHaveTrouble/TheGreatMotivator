package me.youhavetrouble.thegreatmotivator;

import me.youhavetrouble.moneypit.Economy;
import me.youhavetrouble.moneypit.EconomyResponse;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TGMEconomy implements Economy {

    private final TheGreatMotivator plugin;

    protected TGMEconomy(TheGreatMotivator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "The Great Motivator";
    }

    @Override
    public String format(long l) {
        return "%s".formatted(l);
    }

    @Override
    public CompletableFuture<Long> getBalance(UUID uuid) {
        return plugin.getPlayerBalance(Bukkit.getOfflinePlayer(uuid)).thenApply(TGMPlayerBalance::getBalance);
    }

    @Override
    public CompletableFuture<EconomyResponse> deposit(UUID uuid, long l) {
        long value = Math.max(0, l);
        return plugin.addToPlayersBalance(Bukkit.getOfflinePlayer(uuid), value);
    }

    @Override
    public CompletableFuture<EconomyResponse> withdraw(UUID uuid, long l) {
        long value = Math.max(0, l);
        return plugin.withdrawFromPlayersBalance(Bukkit.getOfflinePlayer(uuid), value);
    }

    @Override
    public CompletableFuture<Boolean> has(UUID uuid, long l) {
        return plugin.getPlayerBalance(Bukkit.getOfflinePlayer(uuid)).thenApply(balance -> {
            if (balance == null) return false;
            return balance.getBalance() >= l;
        });
    }
}
