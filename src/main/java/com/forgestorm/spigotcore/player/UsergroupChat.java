package com.forgestorm.spigotcore.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.UserGroup;
import com.forgestorm.spigotcore.database.ProfileData;
import com.forgestorm.spigotcore.feature.AbstractDatabaseFeature;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides very basic chat formatting.
 */
public class UsergroupChat extends AbstractDatabaseFeature implements Listener {

    private static final String INSERT = "INSERT INTO fs_players VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE name=?";
    private static final String SELECT = "SELECT usergroup FROM fs_players WHERE uuid=?";

    @Override
    public void onEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        Bukkit.getOnlinePlayers().forEach(this::asyncDatastoreLoad);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
    }


    @Override
    public ProfileData databaseLoad(Player player, HikariDataSource hikariDataSource) {
        UsergroupData usergroupData = new UsergroupData();

        try (Connection connection = hikariDataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(INSERT);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.setString(3, UserGroup.USER_GROUP_NEW.toString());
            preparedStatement.setString(4, player.getName());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(SELECT);
            preparedStatement.setString(1, player.getUniqueId().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                usergroupData.setUserGroup(UserGroup.valueOf(resultSet.getString("usergroup")));
                usergroupData.setDataLoaded(true);
            }

            preparedStatement.close();
            resultSet.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return usergroupData;
    }

    @Override
    public void databaseSave(Player player, ProfileData profileData, HikariDataSource hikariDataSource) {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //if (!isProfileDataLoaded(event.getPlayer()))
        asyncDatastoreLoad(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UsergroupData usergroupData = (UsergroupData) getProfileData(player);

        event.setCancelled(true);

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.sendMessage(new StringBuilder().append(usergroupData.userGroup.getUserGroupPrefix())
                    .append(ChatColor.GRAY)
                    .append(player.getName())
                    .append(ChatColor.DARK_GRAY)
                    .append(": ")
                    .append(usergroupData.userGroup.getMessageColor())
                    .append(event.getMessage()).toString());
        }
    }

    @Setter
    @Getter
    private class UsergroupData extends ProfileData {
        private UserGroup userGroup;
    }
}
