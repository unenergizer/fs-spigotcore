package com.forgestorm.spigotcore.features.required.database.global.player.sql;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.required.database.global.BaseGlobalData;
import com.forgestorm.spigotcore.features.required.database.global.player.data.GlobalPlayerData;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerAccount;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class PlayerAccountSQL implements BaseGlobalData {

    @Override
    public void databaseLoad(Player player, Connection connection, ResultSet resultSet, GlobalPlayerData playerData) throws SQLException {
        PlayerAccount playerAccount = new PlayerAccount();

        playerAccount.setFirstJoinDate(resultSet.getTimestamp("first_join_date"));
        playerAccount.setRank(PlayerRanks.valueOf(resultSet.getString("rank")));
        playerAccount.setBanned(resultSet.getBoolean("is_banned"));
        playerAccount.setAdmin(resultSet.getBoolean("is_admin"));
        playerAccount.setModerator(resultSet.getBoolean("is_moderator"));
        playerAccount.setWarningPoints(resultSet.getInt("warning_points"));

        // Setting them a new last join date
        PreparedStatement updateJoinStatement = connection.prepareStatement("UPDATE fs_player_account SET last_join_date=? WHERE player_uuid=?");
        updateJoinStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))));
        updateJoinStatement.setString(2, player.getUniqueId().toString());
        updateJoinStatement.execute();

        playerData.setPlayerAccount(playerAccount);
    }

    @Override
    public void databaseSave(Player player, Connection connection) throws SQLException {

        PlayerAccount playerAccount = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player).getPlayerAccount();
        if (playerAccount == null) return;

        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE fs_player_account" +
                " SET username=? rank=? isBanned=? warning_points=?" +
                " WHERE player_uuid=?");
        preparedStatement.setString(1, player.getName());
        preparedStatement.setString(2, playerAccount.getRank().toString());
        preparedStatement.setBoolean(3, playerAccount.isBanned());
        preparedStatement.setInt(4, playerAccount.getWarningPoints());
        preparedStatement.setString(5, player.getUniqueId().toString());

        preparedStatement.execute();
    }

    @Override
    public void firstTimeSave(Player player, Connection connection, GlobalPlayerData globalPlayerData) throws SQLException {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC")));

        PreparedStatement newPlayerStatement = connection.prepareStatement("INSERT INTO fs_player_account " +
                "(player_uuid, username, ip_address, first_join_date, last_join_date, rank, is_banned, is_admin, is_moderator, warning_points) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        newPlayerStatement.setString(1, player.getUniqueId().toString());
        newPlayerStatement.setString(2, player.getName());
        newPlayerStatement.setString(3, player.getAddress().getAddress().toString());
        newPlayerStatement.setTimestamp(4, timestamp);
        newPlayerStatement.setTimestamp(5, timestamp);
        newPlayerStatement.setString(6, PlayerRanks.NEW_PLAYER.toString());
        newPlayerStatement.setBoolean(7, false);
        newPlayerStatement.setBoolean(8, false);
        newPlayerStatement.setBoolean(9, false);
        newPlayerStatement.setInt(10, 0);

        newPlayerStatement.execute();

        PlayerAccount playerAccount = new PlayerAccount();
        playerAccount.setFirstJoinDate(timestamp);
        playerAccount.setLastJoinDate(timestamp);
        playerAccount.setRank(PlayerRanks.NEW_PLAYER);
        playerAccount.setBanned(false);
        playerAccount.setAdmin(false);
        playerAccount.setModerator(false);
        playerAccount.setWarningPoints(0);

        globalPlayerData.setPlayerAccount(playerAccount);
    }

    @Override
    public ResultSet searchForData(Player player, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM fs_player_account WHERE player_uuid=?");
        preparedStatement.setString(1, player.getUniqueId().toString());
        return preparedStatement.executeQuery();
    }
}
