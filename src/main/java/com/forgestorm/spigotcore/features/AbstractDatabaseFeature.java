package com.forgestorm.spigotcore.features;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.database.ProfileData;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * A type of {@link FeatureOptional} that needs database support. Note that to continue with our
 * goal of achieving completely decoupled optional features, data loaded should never be access
 * by any other feature. A feature that registers data here is the only class that should access
 * this data later.
 */
@Getter
@AllArgsConstructor
public abstract class AbstractDatabaseFeature implements FeatureOptional {

    public abstract ProfileData databaseLoad(Player player, HikariDataSource hikariDataSource);

    public abstract void databaseSave(Player player, ProfileData profileData, HikariDataSource hikariDataSource);

    /**
     * Gets ProfileData for the player from the FeatureDataManager for this specific features.
     *
     * @param player The player we want to grab the template for.
     * @return A ProfileData that contains saved information.
     */
    protected ProfileData getProfileData(Player player) {
        return SpigotCore.PLUGIN.getFeatureDataManager().getProfileData(player, this);
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
     * Async load data to MySQL using the DatabaseManager.
     *
     * @param player The player to load data for.
     */
    protected void asyncDatastoreLoad(Player player) {
        SpigotCore.PLUGIN.getDatabaseManager().asyncDatastoreLoad(player, this);
    }

    /**
     * Async save data to MySQL using the DatabaseManager.
     *
     * @param player The player to save data for.
     */
    protected void asyncDatastoreSave(Player player) {
        SpigotCore.PLUGIN.getDatabaseManager().asyncDatastoreSave(player, this, getProfileData(player));
    }
}