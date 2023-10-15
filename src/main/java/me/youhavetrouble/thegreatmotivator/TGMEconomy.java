package me.youhavetrouble.thegreatmotivator;

import me.youhavetrouble.moneypit.Economy;
import me.youhavetrouble.moneypit.EconomyResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TGMEconomy implements Economy {

    TheGreatMotivator plugin;

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

        plugin.getStorage().getPlayerBalance(uuid);

        return null;
    }

    @Override
    public CompletableFuture<EconomyResponse> deposit(UUID uuid, long l) {
        return null;
    }

    @Override
    public CompletableFuture<EconomyResponse> withdraw(UUID uuid, long l) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> has(UUID uuid, long l) {
        return null;
    }
}
