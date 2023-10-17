package me.youhavetrouble.thegreatmotivator;

import java.util.UUID;

public class TGMPlayerBalance {

    private final UUID uuid;
    private long balance, lastAccess;
    private boolean dirty = false;

    protected TGMPlayerBalance(UUID uuid, long balance) {
        this.uuid = uuid;
        this.balance = balance;
        this.updateLastAccess();
    }

    public UUID getUuid() {
        updateLastAccess();
        return uuid;
    }

    public long getBalance() {
        updateLastAccess();
        return balance;
    }

    public void setBalance(long balance) {
        updateLastAccess();
        this.dirty = true;
        this.balance = balance;
    }

    private void updateLastAccess() {
        this.lastAccess = System.currentTimeMillis();
    }

    public long getLastAccess() {
        return lastAccess;
    }

    protected boolean isDirty() {
        return dirty;
    }
}
