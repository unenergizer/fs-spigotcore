package com.forgestorm.spigotcore.features.required.database;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.database.global.SqlSearchData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A type of {@link FeatureOptional} that needs database support. Note that to continue with our
 * goal of achieving completely decoupled optional features, data loaded should never be access
 * by any other feature. A feature that registers data here is the only class that should access
 * this data later.
 */
@Getter
@AllArgsConstructor
public abstract class AbstractDatabaseFeature<T> implements FeatureOptional {

    public abstract ProfileData databaseLoad(Player player, Connection connection, ResultSet resultSet) throws SQLException;

    public abstract void databaseSave(Player player, T profileData, Connection connection) throws SQLException;

    public abstract ProfileData firstTimeSave(Player player, Connection connection) throws SQLException;

    public abstract SqlSearchData searchForData(Player player, Connection connection);

    /**
     * Gets ProfileData for the player from the FeatureDataManager for this specific features.
     *
     * @param player The player we want to grab the template for.
     * @return A ProfileData that contains saved information.
     */
    public T getProfileData(Player player) {
        //noinspection unchecked
        return (T) SpigotCore.PLUGIN.getFeatureDataManager().getProfileData(player, this);
    }

    /**
     * Checks to see if the players ProfileData was loaded for this features.
     *
     * @param player The player to check for.
     * @return True if the profile data was loaded.
     */
    protected boolean isProfileDataLoaded(Player player) {
        return SpigotCore.PLUGIN.getFeatureDataManager().isProfileDataLoaded(player, this);
    }

    /**
     * Async load data to MySQL using the DatabaseConnectionManager.
     *
     * @param player The player to load data for.
     */
    protected void asyncDatastoreLoad(Player player) {
        SpigotCore.PLUGIN.getFeatureDataManager().asyncDatastoreLoad(player, this);
    }

    /**
     * Async save data to MySQL using the DatabaseConnectionManager.
     *
     * @param player The player to save data for.
     */
    protected void asyncDatastoreSave(Player player) {
        SpigotCore.PLUGIN.getFeatureDataManager().asyncDatastoreSave(player, this, (ProfileData) getProfileData(player));
    }
}
