package com.forgestorm.spigotcore.features.required.database.global;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.features.required.database.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.features.required.database.global.player.data.GlobalPlayerData;
import com.forgestorm.spigotcore.features.required.database.global.player.sql.PlayerAccountSQL;
import com.forgestorm.spigotcore.features.required.database.global.player.sql.PlayerEconomySQL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GlobalDataManager extends FeatureRequired implements Listener {

    private final Map<Player, GlobalPlayerData> playerProfileDataMap = new ConcurrentHashMap<>();
    private final List<BaseGlobalData> globalDataLoaders = new ArrayList<>();

    private Queue<AsyncLoad> asyncLoadQueue = new ConcurrentLinkedQueue<>();
    private Queue<AsyncSave> asyncSaveQueue = new ConcurrentLinkedQueue<>();
    private Queue<PostLoadData> syncPostLoadQueue = new ConcurrentLinkedQueue<>();

    private BukkitTask asyncLoadRunnable;
    private BukkitTask asyncSaveRunnable;
    private BukkitTask syncPostLoadRunnable;

    @Override
    public void initFeatureStart() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        globalDataLoaders.add(new PlayerAccountSQL());
        globalDataLoaders.add(new PlayerEconomySQL());

        asyncLoadRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Iterator<AsyncLoad> iterator = asyncLoadQueue.iterator(); iterator.hasNext(); ) {
                    AsyncLoad asyncLoad = iterator.next();
                    asyncLoad.run();
                    iterator.remove();
                }
            }
        }.runTaskTimerAsynchronously(SpigotCore.PLUGIN, 0, 1);

        asyncSaveRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Iterator<AsyncSave> iterator = asyncSaveQueue.iterator(); iterator.hasNext(); ) {
                    AsyncSave asyncSave = iterator.next();
                    asyncSave.run();
                    iterator.remove();
                }
            }
        }.runTaskTimerAsynchronously(SpigotCore.PLUGIN, 0, 1);

        syncPostLoadRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Iterator<PostLoadData> iterator = syncPostLoadQueue.iterator(); iterator.hasNext(); ) {
                    PostLoadData data = iterator.next();
                    playerProfileDataMap.put(data.getPlayer(), data.profileData);
                    Bukkit.getPluginManager().callEvent(new GlobalProfileDataLoadEvent(data.getPlayer(), data.profileData));
                    iterator.remove();
                }
            }
        }.runTaskTimer(SpigotCore.PLUGIN, 0, 1);
    }

    @Override
    public void initFeatureClose() {
        PlayerLoginEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);

        asyncLoadRunnable.cancel();
        asyncSaveRunnable.cancel();
        syncPostLoadRunnable.cancel();

        asyncLoadQueue.clear();
        asyncSaveQueue.clear();
        syncPostLoadQueue.clear();
    }

    public GlobalPlayerData getGlobalPlayerData(Player player) {
        return playerProfileDataMap.get(player);
    }

    public boolean hasGlobalPlayerData(Player player) {
        return playerProfileDataMap.containsKey(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        asyncLoadQueue.add(new AsyncLoad(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        asyncSaveQueue.add(new AsyncSave(event.getPlayer()));
    }

    /**
     * Runs an {@link AbstractDatabaseFeature} MySQL load query asynchronously.
     */
    @AllArgsConstructor
    private class AsyncLoad {

        private Player player;

        public void run() {

            GlobalPlayerData profileData = new GlobalPlayerData();

            try (Connection connection = SpigotCore.PLUGIN.getDatabaseConnectionManager().getHikariDataSource().getConnection()) {

                for (BaseGlobalData baseGlobalData : globalDataLoaders) {
                    SqlSearchData sqlSearchData = baseGlobalData.searchForData(player, connection);

                    PreparedStatement searchStatement = connection.prepareStatement("SELECT * FROM " + sqlSearchData.getTableName() + " WHERE " + sqlSearchData.getColumnName() + "=?");
                    searchStatement.setObject(1, sqlSearchData.getSetData());
                    ResultSet resultSet = searchStatement.executeQuery();

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

            syncPostLoadQueue.add(new PostLoadData(player, profileData));
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class PostLoadData {
        private Player player;
        private GlobalPlayerData profileData;
    }

    /**
     * Runs an {@link AbstractDatabaseFeature} MySQL save query asynchronously.
     */
    @AllArgsConstructor
    private class AsyncSave {

        private Player player;

        public void run() {
            try (Connection connection = SpigotCore.PLUGIN.getDatabaseConnectionManager().getHikariDataSource().getConnection()) {
                for (BaseGlobalData baseGlobalData : globalDataLoaders) {
                    baseGlobalData.databaseSave(player, connection);
                }
            } catch (SQLException exe) {
                exe.printStackTrace();
            }
        }
    }
}
