package me.youhavetrouble.thegreatmotivator;

import me.youhavetrouble.moneypit.Economy;
import me.youhavetrouble.moneypit.EconomyResponse;

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
        return CompletableFuture.supplyAsync(() -> plugin.getStorage().getPlayerBalance(uuid));

    }

    @Override
    public CompletableFuture<EconomyResponse> deposit(UUID uuid, long l) {
        return CompletableFuture.supplyAsync(() -> {
             Long newBalance = plugin.getStorage().addToPlayerBalance(uuid, l);
             return new EconomyResponse(l, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        }).exceptionally(e -> {
            return new EconomyResponse(l, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        });
    }

    @Override
    public CompletableFuture<EconomyResponse> withdraw(UUID uuid, long l) {
        return CompletableFuture.supplyAsync(() -> {
            Long newBalance = plugin.getStorage().subtractFromPlayerBalance(uuid, l);
            return new EconomyResponse(l, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        }).exceptionally(e -> {
            return new EconomyResponse(l, 0, EconomyResponse.ResponseType.FAILURE, e.getMessage());
        });
    }

    @Override
    public CompletableFuture<Boolean> has(UUID uuid, long l) {
        return CompletableFuture.supplyAsync(() -> {
            Long balance = plugin.getStorage().getPlayerBalance(uuid);
            return balance != null && balance >= l;
        });
    }
}
