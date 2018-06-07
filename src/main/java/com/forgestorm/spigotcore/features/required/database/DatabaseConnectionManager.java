package com.forgestorm.spigotcore.features.required.database;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.util.text.Console;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

/**
 * DatabaseConnectionManager is responsible for providing access to a MySQL database.
 */
public class DatabaseConnectionManager extends FeatureRequired {

    @Getter
    private final HikariDataSource hikariDataSource = new HikariDataSource();

    private String serverName;
    private String port;
    private String databaseName;
    private String user;
    private String password;

    @Override
    public void initFeatureStart() {
        Configuration config = SpigotCore.PLUGIN.getConfig();
        String configPath = "Database.";

        serverName = config.getString(configPath + "address");
        port = config.getString(configPath + "port");
        databaseName = config.getString(configPath + "dbname");
        user = config.getString(configPath + "username");
        password = config.getString(configPath + "password");
        
        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikariDataSource.addDataSourceProperty("serverName", serverName);
        hikariDataSource.addDataSourceProperty("port", port);
        hikariDataSource.addDataSourceProperty("databaseName", databaseName);
        hikariDataSource.addDataSourceProperty("user", user);
        hikariDataSource.addDataSourceProperty("password", password);
        
        testConnection();
    }

    private void testConnection() {
        try {
            hikariDataSource.getConnection();
            Console.sendMessage(ChatColor.BLUE + "[DatabaseConnectionManager] Setup complete");
        } catch (SQLException | RuntimeException e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Console.sendMessage("&c!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    Console.sendMessage("&c!! [DatabaseConnectionManager] Could not connect to MySQL Database!");
                    Console.sendMessage("&c!! [DatabaseConnectionManager] Shutting down the server!");
                    Console.sendMessage("&c!! serverName: " + serverName);
                    Console.sendMessage("&c!! port: " + port);
                    Console.sendMessage("&c!! databaseName: " + databaseName);
                    Console.sendMessage("&c!! user: " + user);
                    Console.sendMessage("&c!! password: " + password);
                    Console.sendMessage("&c!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    Bukkit.getServer().shutdown();
                }
            }.runTaskLater(SpigotCore.PLUGIN, 0);
        }
    }

    @Override
    public void initFeatureClose() {
        hikariDataSource.close();
        Console.sendMessage(ChatColor.BLUE + "[DatabaseConnectionManager] Shut down");
    }
}
