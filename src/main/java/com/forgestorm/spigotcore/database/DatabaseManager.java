package com.forgestorm.spigotcore.database;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.feature.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.feature.FeatureRequired;
import com.forgestorm.spigotcore.util.text.Console;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DatabaseManager implements FeatureRequired {

    private final HikariDataSource hikariDataSource = new HikariDataSource();

    @Override
    public void onServerStartup() {
        Configuration config = SpigotCore.PLUGIN.getConfig();
        String configPath = "Database.";

        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikariDataSource.addDataSourceProperty("serverName", config.getString(configPath + "address"));
        hikariDataSource.addDataSourceProperty("port", config.getString(configPath + "port"));
        hikariDataSource.addDataSourceProperty("databaseName", config.getString(configPath + "dbname"));
        hikariDataSource.addDataSourceProperty("user", config.getString(configPath + "username"));
        hikariDataSource.addDataSourceProperty("password", config.getString(configPath + "password"));
        Console.sendMessage(ChatColor.BLUE + "[DatabaseManager] Setup complete");
    }

    @Override
    public void onServerShutdown() {
        hikariDataSource.close();
        Console.sendMessage(ChatColor.BLUE + "[DatabaseManager] Shut down");
    }

    /**
     * Async loads data from MySQL.
     *
     * @param player  The player to get data for.
     * @param feature The feature to get data for.
     */
    public void asyncDatastoreLoad(Player player, AbstractDatabaseFeature feature) {
        new AsyncLoad(player, feature).runTaskAsynchronously(SpigotCore.PLUGIN);
    }

    /**
     * Async saves data from MySQL.
     *
     * @param player  The player to save data for.
     * @param feature The feature to save data for.
     */
    public void asyncDatastoreSave(Player player, AbstractDatabaseFeature feature, ProfileData profileData) {
        new AsyncSave(player, feature, profileData).runTaskAsynchronously(SpigotCore.PLUGIN);
    }

    /**
     * Runs an {@link AbstractDatabaseFeature} MySQL load query.
     */
    @AllArgsConstructor
    private class AsyncLoad extends BukkitRunnable {

        private Player player;
        private AbstractDatabaseFeature feature;

        @Override
        public void run() {
            ProfileData profileData = feature.databaseLoad(player, hikariDataSource);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(new ProfileDataLoadEvent(player, feature, profileData));
                }
            }.runTask(SpigotCore.PLUGIN);
        }
    }

    /**
     * Runs an {@link AbstractDatabaseFeature} MySQL save query.
     */
    @AllArgsConstructor
    private class AsyncSave extends BukkitRunnable {

        private Player player;
        private AbstractDatabaseFeature feature;
        private ProfileData profileData;

        @Override
        public void run() {
            feature.databaseSave(player, profileData, hikariDataSource);
        }
    }
}
