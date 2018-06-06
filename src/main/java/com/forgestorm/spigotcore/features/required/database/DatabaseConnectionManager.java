package com.forgestorm.spigotcore.features.required.database;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.util.text.Console;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

/**
 * DatabaseConnectionManager is responsible for providing access to a MySQL database.
 */
public class DatabaseConnectionManager implements FeatureRequired {

    @Getter
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
        Console.sendMessage(ChatColor.BLUE + "[DatabaseConnectionManager] Setup complete");
    }

    @Override
    public void onServerShutdown() {
        hikariDataSource.close();
        Console.sendMessage(ChatColor.BLUE + "[DatabaseConnectionManager] Shut down");
    }
}