package me.youhavetrouble.thegreatmotivator.baltop;

import javax.annotation.Nullable;
import java.util.UUID;

public class TGMBaltopEntry {

    private String name;
    private UUID uuid;
    private double amount = 0;

    protected TGMBaltopEntry(UUID uuid, String name, double amount) {
        setNewUser(uuid, name, amount);
    }

    public @Nullable UUID getUuid() {
        return uuid;
    }

    public @Nullable String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    protected void setNewUser(UUID uuid, String name, double amount) {
        this.uuid = uuid;
        this.name = name;
        this.amount = amount;
    }
}
