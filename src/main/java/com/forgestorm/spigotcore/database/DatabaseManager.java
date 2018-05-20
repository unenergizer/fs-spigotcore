package com.forgestorm.spigotcore.database;

import com.forgestorm.spigotcore.FeatureRequired;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.util.text.Console;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.Configuration;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager extends FeatureRequired {

    private final Map<AbstractDatabaseFeature, DatabaseTemplate> blankDatabaseTemplates = new HashMap<>();
    private final HikariDataSource hikari = new HikariDataSource();

    @Override
    public void onEnable() {
        Configuration config = SpigotCore.PLUGIN.getConfig();
        String configPath = "Database.";

        hikari.setMaximumPoolSize(10);
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", config.getString(configPath + "address"));
        hikari.addDataSourceProperty("port", config.getString(configPath + "port"));
        hikari.addDataSourceProperty("databaseName", config.getString(configPath + "dbname"));
        hikari.addDataSourceProperty("user", config.getString(configPath + "username"));
        hikari.addDataSourceProperty("password", config.getString(configPath + "password"));
        Console.sendMessage("[DatabaseManager] Setup complete");
    }

    @Override
    public void onDisable() {
        hikari.close();
        blankDatabaseTemplates.clear();
        Console.sendMessage("[DatabaseManager] Shut down");
    }

    /**
     * Adds a database template to our collection of database templates.
     *
     * @param databaseTemplate The template we want to add.
     */
    public void addDatabaseTemplate(AbstractDatabaseFeature clazz, DatabaseTemplate databaseTemplate) {
        if (blankDatabaseTemplates.containsKey(clazz)) return;
        blankDatabaseTemplates.put(clazz, databaseTemplate);
    }

    /**
     * When we want to create a player profile, we copy this map of
     * AbstractDatabaseFeature classes and DatabaseTemplates to their profile.
     *
     *
     * @return A copied list of unused DatabaseTemplates.
     */
    Map<AbstractDatabaseFeature, DatabaseTemplate> getCopyOfDatabaseTemplates() {
        return new HashMap<>(blankDatabaseTemplates);
    }

    /**
     * Loads database information for the supplied database template.
     *
     * @param databaseTemplate Contains the data that will be loaded.
     */
    void loadDatabaseTemplateData(DatabaseTemplate databaseTemplate) {
        try {
            databaseTemplate.loadDatabaseData(hikari.getConnection());
            databaseTemplate.setDataLoaded(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves database information for the supplied database template.
     *
     * @param databaseTemplate Contains the data that will be saved.
     */
    void saveDatabaseTemplateData(DatabaseTemplate databaseTemplate) {
        try {
            databaseTemplate.saveDatabaseData(hikari.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
