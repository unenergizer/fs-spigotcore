package com.forgestorm.spigotcore.features.required.database;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.util.text.Console;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * DatabaseManager is responsible for providing access to a MySQL database.
 * <p>
 * RULES:
 * <ul>
 *      <li>No class should directly access the {@link HikariDataSource} used in this class.</li>
 * </ul>
 */
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
     * @param feature The features to get data for.
     */
    public void asyncDatastoreLoad(Player player, AbstractDatabaseFeature feature) {
        new AsyncLoad(player, feature).runTaskAsynchronously(SpigotCore.PLUGIN);
    }

    /**
     * Async saves data from MySQL.
     *
     * @param player      The player to save data for.
     * @param feature     The features to save data for.
     * @param profileData The data we intend to save.
     */
    public void asyncDatastoreSave(Player player, AbstractDatabaseFeature feature, ProfileData profileData) {
        new AsyncSave(player, feature, profileData).runTaskAsynchronously(SpigotCore.PLUGIN);
    }

    /**
     * Runs an {@link AbstractDatabaseFeature} MySQL load query asynchronously.
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
     * Runs an {@link AbstractDatabaseFeature} MySQL save query asynchronously.
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
