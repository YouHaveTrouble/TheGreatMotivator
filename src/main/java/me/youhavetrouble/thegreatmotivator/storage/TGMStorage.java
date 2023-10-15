package me.youhavetrouble.thegreatmotivator.storage;

import java.sql.SQLException;
import java.util.UUID;

public interface TGMStorage {

    void createTables() throws SQLException;

    /**
     * Adds the specified amount to the player's balance.
     * @param playerUuid The UUID of the player to add to
     * @param amount The amount to add
     * @return The new balance of the player, or null if the sql query failed
     */
    Long addToPlayerBalance(UUID playerUuid, long amount);

    /**
     * Subtracts the specified amount from the player's balance.
     * @param playerUuid The UUID of the player to subtract from
     * @param amount The amount to subtract
     * @return The new balance of the player, or null if the sql query failed
     */
    Long subtractFromPlayerBalance(UUID playerUuid, long amount);

    Long getPlayerBalance(UUID playerUuid);

}
