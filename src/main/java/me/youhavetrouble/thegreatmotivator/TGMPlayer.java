package me.youhavetrouble.thegreatmotivator;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TGMPlayer {

    private final static ConcurrentHashMap<UUID, TGMPlayer> playerCache = new ConcurrentHashMap<>();

    private double balance;
    private boolean dirty = false;

    TGMPlayer(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void add(double toAdd) {
        this.balance += toAdd;
        this.dirty = true;
    }

    public void subtract(double toSubtract) {
        this.balance -= toSubtract;
        this.dirty = true;
    }

    public boolean has(double amount) {
        return this.balance >= amount;
    }

    protected boolean isDirty() {
        return dirty;
    }

    protected void clean() {
        this.dirty = false;
    }

    protected static void trackPlayer(UUID uuid, TGMPlayer tgmPlayer) {
        playerCache.put(uuid, tgmPlayer);
    }

    protected static void untrackPlayer(UUID uuid) {
        playerCache.remove(uuid);
    }

    protected static ConcurrentHashMap<UUID, TGMPlayer> getPlayerCache() {
        return playerCache;
    }
}
