package com.forgestorm.spigotcore.database;

import com.forgestorm.spigotcore.feature.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.feature.FeatureRequired;
import com.forgestorm.spigotcore.SpigotCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Profile manager provides a structured decoupled way to access and save data on a per feature basis.
 */
public class ProfileManager implements FeatureRequired, Listener {

    private final Map<Player, Map<AbstractDatabaseFeature, DatabaseTemplate>> playerData = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onDisable() {
        saveAllProfileData();

        PlayerJoinEvent.getHandlerList().unregister(this);
    }

    private void saveAllProfileData() {
        for (Map.Entry<Player, Map<AbstractDatabaseFeature, DatabaseTemplate>> outerEntry : playerData.entrySet()) {
            for (Map.Entry<AbstractDatabaseFeature, DatabaseTemplate> innerEntry : outerEntry.getValue().entrySet()) {
                if (innerEntry.getValue().isDataLoaded()) saveProfileData(outerEntry.getKey(), innerEntry.getKey());
            }
        }
    }

    /**
     * Saves player data for a specific {@link AbstractDatabaseFeature}
     *
     * @param player  The player to save data for.
     * @param feature The feature to save data for.
     */
    public void saveProfileData(Player player, AbstractDatabaseFeature feature) {
        SpigotCore.PLUGIN.getDatabaseManager().saveDatabaseTemplateData(getDatabaseTemplate(player, feature));
    }

    /**
     * Loads player data for a specific {@link AbstractDatabaseFeature}
     *
     * @param player  The player to load data for.
     * @param feature The feature to load data for.
     */
    public void loadProfileData(Player player, AbstractDatabaseFeature feature) {
        SpigotCore.PLUGIN.getDatabaseManager().loadDatabaseTemplateData(getDatabaseTemplate(player, feature));
    }

    /**
     * Gets profile data specific to this player and a {@link AbstractDatabaseFeature} plugin feature.
     *
     * @param player  The player we want profile data for.
     * @param feature The feature we want to get data for.
     * @return Profile data for player with data specific to {@link AbstractDatabaseFeature}
     */
    public DatabaseTemplate getProfileData(Player player, AbstractDatabaseFeature feature) {
        if (!isProfileDataLoaded(player, feature))
            throw new RuntimeException("Tried to get DatabaseTemplate that has no loaded data.");
        return getDatabaseTemplate(player, feature);
    }

    /**
     * Checks to see if the Profile data we want has already been loaded.
     *
     * @param player  The player who we will get data for.
     * @param feature The feature we want data for.
     * @return True if the data is loaded, false otherwise.
     */
    public boolean isProfileDataLoaded(Player player, AbstractDatabaseFeature feature) {
        return getDatabaseTemplate(player, feature).isDataLoaded();
    }

    /**
     * Gets the DatabaseTemplate data that contains database information for this feature.
     *
     * @param player  The player to get the DatabaseTemplate for.
     * @param feature The feature to get a DatabaseTemplate for.
     * @return A set of data from the database specific to a given player and {@link AbstractDatabaseFeature}.
     */
    private DatabaseTemplate getDatabaseTemplate(Player player, AbstractDatabaseFeature feature) {
        return playerData.get(player).get(feature);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerData.put(event.getPlayer(), SpigotCore.PLUGIN.getDatabaseManager().getCopyOfDatabaseTemplates());
    }
}
