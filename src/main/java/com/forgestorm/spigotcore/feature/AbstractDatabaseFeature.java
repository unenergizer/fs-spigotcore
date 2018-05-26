package com.forgestorm.spigotcore.feature;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.database.DatabaseManager;
import com.forgestorm.spigotcore.database.DatabaseTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * A type of {@link FeatureOptional} that needs
 * database support.
 */
@Getter
@AllArgsConstructor
public abstract class AbstractDatabaseFeature implements FeatureOptional {

    /**
     * Do NOT save data here!
     * This is a blank DatabaseTemplate.
     * This is solely for purpose of adding the template to the
     * {@link DatabaseManager} for use later!
     */
    private final DatabaseTemplate blankDatabaseTemplate;

    /**
     * Saves profile data for the player for a specific {@link AbstractDatabaseFeature}.
     *
     * @param player The player to save the data for.
     */
    protected void saveProfileData(Player player) {
        SpigotCore.PLUGIN.getProfileManager().saveProfileData(player, this);
    }

    /**
     * Loads profile data for the player for a specific {@link AbstractDatabaseFeature}.
     *
     * @param player The player to load the data for.
     */
    protected void loadProfileData(Player player) {
        SpigotCore.PLUGIN.getProfileManager().loadProfileData(player, this);
    }

    /**
     * Wrapper method to quickly get the databaseTemplate for this class.
     *
     * @param player The player we want to grab the template for.
     * @return A DatabaseTemplate that contains saved information.
     */
    protected DatabaseTemplate getProfileData(Player player) {
        return SpigotCore.PLUGIN.getProfileManager().getProfileData(player, this);
    }

    /**
     * Wrapper method to see if a profile is loaded
     *
     * @param player The player profile we want to test
     * @return True if the profile is loaded, false otherwise.
     */
    protected boolean isProfileDataLoaded(Player player) {
        return SpigotCore.PLUGIN.getProfileManager().isProfileDataLoaded(player, this);
    }
}
