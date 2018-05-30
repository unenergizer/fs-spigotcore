package com.forgestorm.spigotcore.features.required.database;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * INFO:
 * FeatureDataManager provides a strict decoupled way to access and save data on a per features basis.
 * <p>
 * RULES:
 * <ul>
 *      <li>All {@link com.forgestorm.spigotcore.features.optional.FeatureOptional} must maintain complete decoupling requirement.</li>
 *      <li>No {@link com.forgestorm.spigotcore.features.optional.FeatureOptional} can access others ProfileData.</li>
 * </ul>
 * <p>
 * TODO:
 * <ul>
 *      <li>Save and remove data from playerProfileDataMap, when player quits or changes servers.</li>
 *      <li>Fix the isProfileData loaded method.</li>
 * </ul>
 */
public class FeatureDataManager implements FeatureRequired, Listener {

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
     * Gets profile data specific to this player and a {@link AbstractDatabaseFeature} plugin features.
     *
     * @param player  The player we want profile data for.
     * @param feature The features we want to get data for.
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
     * @param feature The features we want data for.
     * @return True if the data is loaded, false otherwise.
     */
    public boolean isProfileDataLoaded(Player player, AbstractDatabaseFeature feature) {
        if (!playerProfileDataMap.containsKey(player)) return false;
        if (!playerProfileDataMap.get(player).containsKey(feature)) return false;
        if (playerProfileDataMap.get(player).get(feature) == null) return false;
        return playerProfileDataMap.get(player).get(feature).isDataLoaded();
    }

    @EventHandler
    public void onProfileDataLoadEvent(ProfileDataLoadEvent event) {
        Player player = event.getPlayer();
        if (!playerProfileDataMap.containsKey(player)) playerProfileDataMap.put(player, new HashMap<>());
        playerProfileDataMap.get(player).put(event.getAbstractDatabaseFeature(), event.getProfileData());
    }
}
