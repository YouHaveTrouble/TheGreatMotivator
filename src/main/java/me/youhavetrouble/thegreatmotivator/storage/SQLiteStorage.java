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
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public void createTables() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `tgm_balances` (player_uuid varchar(36) NOT NULL PRIMARY KEY, `balance` double);")
        ) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePlayerBalance(UUID playerUuid, double balance) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `tgm_balances` (player_uuid, balance) VALUES (?, ?) ON CONFLICT (player_uuid) DO UPDATE SET balance = ?;")) {
            statement.setString(1, playerUuid.toString());
            statement.setDouble(2, balance);
            statement.setDouble(3, balance);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getPlayerBalance(UUID playerUuid) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT balance FROM `tgm_balances` WHERE player_uuid = ?;")) {
            statement.setString(1, playerUuid.toString());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getDouble("balance");
            } else return 0d;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0d;
        }
    }
}
