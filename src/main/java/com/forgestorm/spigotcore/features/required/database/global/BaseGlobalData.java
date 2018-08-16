package com.forgestorm.spigotcore.features.required.database.global;

import com.forgestorm.spigotcore.features.required.database.global.player.data.GlobalPlayerData;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface BaseGlobalData {

    void databaseLoad(Player player, Connection connection, ResultSet resultSet, GlobalPlayerData playerData) throws SQLException;

    void databaseSave(Player player, Connection connection) throws SQLException;

    void firstTimeSave(Player player, Connection connection, GlobalPlayerData playerData) throws SQLException;

    SqlSearchData searchForData(Player player, Connection connection);

}
