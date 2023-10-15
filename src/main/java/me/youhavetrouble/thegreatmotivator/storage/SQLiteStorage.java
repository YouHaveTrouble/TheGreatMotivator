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
    }

    @Override
    public void createTables() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `balances` (player_uuid varchar(36) NOT NULL PRIMARY KEY, `balance` long);")
        ) {
            statement.executeUpdate();
        }
    }

    @Override
    public Long addToPlayerBalance(UUID playerUuid, long amount) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `balances` (player_uuid, balance) VALUES (?, ?) ON CONFLICT (player_uuid) DO UPDATE SET balance = balance + ?;")) {
            statement.setString(1, playerUuid.toString());
            statement.setLong(2, amount);
            statement.setLong(3, amount);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) return null;
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) return generatedKeys.getLong(1);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Long subtractFromPlayerBalance(UUID playerUuid, long amount) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE `balances` SET balance = CASE WHEN (balance - ? >= 0) THEN balance - ? ELSE balance END, balance as current_value WHERE id = ?;")) {
            statement.setLong(1, amount);
            statement.setString(2, playerUuid.toString());
            statement.setLong(3, amount);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) return resultSet.getLong("current_value");
            return null;
        } catch (SQLException e) {
            return null;
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
