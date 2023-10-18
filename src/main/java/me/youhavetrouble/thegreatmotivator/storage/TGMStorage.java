package me.youhavetrouble.thegreatmotivator.storage;


import java.util.UUID;

public interface TGMStorage {

    void savePlayerBalance(UUID playerUuid, long balance);

    Long getPlayerBalance(UUID playerUuid);

}
