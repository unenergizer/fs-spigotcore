package com.forgestorm.spigotcore.features.required.database.global;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.database.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.features.events.ProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.required.database.global.player.data.GlobalPlayerData;
import com.forgestorm.spigotcore.features.required.database.global.player.sql.PlayerAccountSQL;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalDataManager implements FeatureRequired, Listener {

    private final Map<Player, GlobalPlayerData> playerProfileDataMap = new ConcurrentHashMap<>();
    private final List<BaseGlobalData> globalDataLoaders = new ArrayList<>();

    @Override
    public void onServerStartup() {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        globalDataLoaders.add(new PlayerAccountSQL());
    }

    @Override
    public void onServerShutdown() {
        PlayerLoginEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerKickEvent.getHandlerList().unregister(this);
    }

    public GlobalPlayerData getGlobalPlayerData(Player player) {
        return playerProfileDataMap.get(player);
    }

    public boolean hasGlobalPlayerData(Player player) {
        return playerProfileDataMap.containsKey(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new AsyncLoad(event.getPlayer()).runTaskAsynchronously(SpigotCore.PLUGIN);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        new AsyncSave(event.getPlayer()).runTaskAsynchronously(SpigotCore.PLUGIN);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        new AsyncSave(event.getPlayer()).runTaskAsynchronously(SpigotCore.PLUGIN);
    }

    /**
     * Runs an {@link AbstractDatabaseFeature} MySQL load query asynchronously.
     */
    @AllArgsConstructor
    private class AsyncLoad extends BukkitRunnable {

        private Player player;

        @Override
        public void run() {

            GlobalPlayerData profileData = new GlobalPlayerData(); // TODO: Run database query

            try (Connection connection = SpigotCore.PLUGIN.getDatabaseConnectionManager().getHikariDataSource().getConnection()) {

                for (BaseGlobalData baseGlobalData : globalDataLoaders) {
                    ResultSet resultSet = baseGlobalData.searchForData(player, connection);

                    // They are a new user and we must generate new tables for them
                    if (!resultSet.next()) {
                        baseGlobalData.firstTimeSave(player, connection, profileData);
                    } else {
                        baseGlobalData.databaseLoad(player, connection, resultSet, profileData);
                    }
                }
            } catch (SQLException exe) {
                exe.printStackTrace();
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    playerProfileDataMap.put(player, profileData);
                    Bukkit.getPluginManager().callEvent(new ProfileDataLoadEvent(player, profileData));
                }
            }.runTask(SpigotCore.PLUGIN);
        }
    }

    /**
     * Runs an {@link AbstractDatabaseFeature} MySQL save query asynchronously.
     */
    @AllArgsConstructor
    private class AsyncSave extends BukkitRunnable {

        private Player player;

        @Override
        public void run() {
            // TODO: Run database query
        }
    }
}