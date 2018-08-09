package com.forgestorm.spigotcore.features.required.database.global.player.sql;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.database.global.BaseGlobalData;
import com.forgestorm.spigotcore.features.required.database.global.SqlSearchData;
import com.forgestorm.spigotcore.features.required.database.global.player.data.GlobalPlayerData;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerEconomy;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerEconomySQL implements BaseGlobalData {

    @Override
    public void databaseLoad(Player player, Connection connection, ResultSet resultSet, GlobalPlayerData playerData) throws SQLException {
        PlayerEconomy playerEconomy = new PlayerEconomy();
        playerEconomy.setGems(resultSet.getInt("gems"));

        playerData.setPlayerEconomy(playerEconomy);
    }

    @Override
    public void databaseSave(Player player, Connection connection) throws SQLException {

        PlayerEconomy playerEconomy = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player).getPlayerEconomy();
        if (playerEconomy == null) return;

        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE fs_player_economy" +
                " SET gems=?" +
                " WHERE player_uuid=?");
        preparedStatement.setInt(1, playerEconomy.getGems());
        preparedStatement.setString(2, player.getUniqueId().toString());

        preparedStatement.execute();
    }

    @Override
    public void firstTimeSave(Player player, Connection connection, GlobalPlayerData globalPlayerData) throws SQLException {
        final int startingGems = 300;

        PreparedStatement newPlayerStatement = connection.prepareStatement("INSERT INTO fs_player_economy " +
                "(player_uuid, gems) " +
                "VALUES(?, ?)");

        newPlayerStatement.setString(1, player.getUniqueId().toString());
        newPlayerStatement.setInt(2, startingGems);

        newPlayerStatement.execute();

        PlayerEconomy playerEconomy = new PlayerEconomy();
        playerEconomy.setGems(startingGems);
        globalPlayerData.setPlayerEconomy(playerEconomy);
    }

    @Override
    public SqlSearchData searchForData(Player player, Connection connection) {
        return new SqlSearchData("fs_player_economy", "player_uuid", player.getUniqueId().toString());
    }
}
