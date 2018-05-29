package com.forgestorm.spigotcore.database;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.feature.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.feature.FeatureRequired;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Profile manager provides a structured decoupled way to access and save data on a per feature basis.
 */
public class ProfileManager implements FeatureRequired, Listener {

    private final Map<Player, Map<AbstractDatabaseFeature, ProfileData>> playerProfileDataMap = new ConcurrentHashMap<>();

    @Override
    public void onServerStartup() {
        Bukkit.getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onServerShutdown() {
        ProfileDataLoadEvent.getHandlerList().unregister(this);
    }

    /**
     * Gets profile data specific to this player and a {@link AbstractDatabaseFeature} plugin feature.
     *
     * @param player  The player we want profile data for.
     * @param feature The feature we want to get data for.
     * @return Profile data for player with data specific to {@link AbstractDatabaseFeature}
     */
    public ProfileData getProfileData(Player player, AbstractDatabaseFeature feature) {
        if (!isProfileDataLoaded(player, feature))
            throw new RuntimeException("Tried to get ProfileData that has no loaded data.");
        return playerProfileDataMap.get(player).get(feature);
    }

    public void removeProfileData(Player player, AbstractDatabaseFeature feature) {
        playerProfileDataMap.get(player).remove(feature);
    }

    /**
     * Checks to see if the Profile data we want has already been loaded.
     *
     * @param player  The player who we will get data for.
     * @param feature The feature we want data for.
     * @return True if the data is loaded, false otherwise.
     */
    public boolean isProfileDataLoaded(Player player, AbstractDatabaseFeature feature) {
//        return playerProfileDataMap.get(player).get(feature) != null && playerProfileDataMap.get(player).get(feature).isDataLoaded();
//        && playerProfileDataMap.get(player).containsKey(feature)
        // TODO: Check if the isDataLoaded() var is true?
        return playerProfileDataMap.get(player).get(feature) != null;
    }

    @EventHandler
    public void onProfileDataLoadEvent(ProfileDataLoadEvent event) {
        Player player = event.getPlayer();
        if (!playerProfileDataMap.containsKey(player)) playerProfileDataMap.put(player, new HashMap<>());
        playerProfileDataMap.get(player).put(event.getAbstractDatabaseFeature(), event.getProfileData());
    }
}
