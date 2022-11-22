package me.youhavetrouble.thegreatmotivator.storage;

import java.sql.SQLException;
import java.util.UUID;

public interface TGMStorage {

    void createTables() throws SQLException;

    void savePlayerBalance(UUID playerUuid, double balance);

    double getPlayerBalance(UUID playerUuid);

}
