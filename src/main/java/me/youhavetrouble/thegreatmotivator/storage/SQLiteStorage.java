package me.youhavetrouble.thegreatmotivator.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLiteStorage implements TGMStorage {

    private final DataSource dataSource;

    public SQLiteStorage() {
        HikariConfig config = new HikariConfig();
        String url = "jdbc:sqlite:plugins/TheGreatMotivator/data.db";
        config.setJdbcUrl(url);
        config.setMaximumPoolSize(1);
        dataSource = new HikariDataSource(config);
        try {
            createTables();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createTables() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `balances` (player_uuid varchar(36) NOT NULL PRIMARY KEY, `balance` long);")
        ) {
            statement.executeUpdate();
        }
    }

    @Override
    public void savePlayerBalance(UUID playerUuid, long balance) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `balances` (player_uuid, balance) VALUES (?, ?) ON CONFLICT(player_uuid) DO UPDATE SET balance = ?;")) {
            statement.setString(1, playerUuid.toString());
            statement.setLong(2, balance);
            statement.setLong(3, balance);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public Long getPlayerBalance(UUID playerUuid) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT balance FROM `balances` WHERE player_uuid = ?;")) {
            statement.setString(1, playerUuid.toString());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getLong("balance");
            } else return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}
