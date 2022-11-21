package me.youhavetrouble.thegreatmotivator.storage;

import java.util.UUID;

public interface TGMStorage {

    void createTables();

    void savePlayerBalance(UUID playerUuid, double balance);

    double getPlayerBalance(UUID playerUuid);

}
