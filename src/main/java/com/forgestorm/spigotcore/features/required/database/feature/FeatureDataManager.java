package com.forgestorm.spigotcore.features.required.database.feature;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.events.FeatureProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.features.required.database.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.features.required.database.ProfileData;
import com.forgestorm.spigotcore.features.required.database.global.SqlSearchData;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.util.text.Text;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * INFO:
 * FeatureDataManager provides a strict decoupled way to access and save data on a per features basis.
 * <p>
 * RULES:
 * <ul>
 * <li>All {@link com.forgestorm.spigotcore.features.optional.FeatureOptional} must maintain complete decoupling requirement.</li>
 * <li>No {@link com.forgestorm.spigotcore.features.optional.FeatureOptional} can access others ProfileData.</li>
 * </ul>
 * <p>
 * TODO:
 * <ul>
 * <li>Save and remove data from playerProfileDataMap, when player quits or changes servers.</li>
 * <li>Fix the isProfileData loaded method.</li>
 * </ul>
 */
public class FeatureDataManager extends FeatureRequired implements Listener {

    private final Map<Player, Map<AbstractDatabaseFeature, ProfileData>> playerProfileDataMap = new ConcurrentHashMap<>();

    @Override
    public void initFeatureStart() {
//        Bukkit.getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void initFeatureClose() {
//        GlobalProfileDataLoadEvent.getHandlerList().unregister(this);
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
            throw new RuntimeException("[FeatureDataManager:getProfileData]Tried to get ProfileData that has no loaded data.");
        return playerProfileDataMap.get(player).get(feature);
    }

    private void addProfileData(Player player, AbstractDatabaseFeature feature, ProfileData profileData) {
        if (!playerProfileDataMap.containsKey(player)) playerProfileDataMap.put(player, new HashMap<>());
        playerProfileDataMap.get(player).put(feature, profileData);
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

    /**
     * Async loads data from MySQL.
     *
     * @param player  The player to get data for.
     * @param feature The features to get data for.
     */
    public void asyncDatastoreLoad(Player player, AbstractDatabaseFeature feature) {
        new AsyncLoad(player, feature).runTaskAsynchronously(SpigotCore.PLUGIN);
        player.sendMessage(Text.color("&7[&9Database&7] &aLoading your data from &e" + feature.getClass().getSimpleName() + "&a."));
        Console.sendMessage("&7[&9Database&7] &aLoading data from &e" + feature.getClass().getSimpleName() + "&a for &e" + player.getName() + "&a.");
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
        player.sendMessage(Text.color("&7[&9Database&7] &aSaving your data from &e" + feature.getClass().getSimpleName() + "&a."));
        Console.sendMessage("&7[&9Database&7] &aSaving data from &e" + feature.getClass().getSimpleName() + "&a for &e" + player.getName() + "&a.");
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

            ProfileData[] profileData = new ProfileData[1];
            try (Connection connection = SpigotCore.PLUGIN.getDatabaseConnectionManager().getHikariDataSource().getConnection()) {
                SqlSearchData sqlSearchData = feature.searchForData(player, connection);

                PreparedStatement searchStatement = connection.prepareStatement("SELECT * FROM " + sqlSearchData.getTableName() + " WHERE " + sqlSearchData.getColumnName() + "=?");
                searchStatement.setObject(1, sqlSearchData.getSetData());
                ResultSet resultSet = searchStatement.executeQuery();

                if (resultSet.next()) {
                    profileData[0] = feature.databaseLoad(player, connection, resultSet);
                } else {
                    profileData[0] = feature.firstTimeSave(player, connection);
                }
            } catch (SQLException exe) {
                exe.printStackTrace();
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(new FeatureProfileDataLoadEvent(player, feature, profileData[0]));
                    addProfileData(player, feature, profileData[0]);
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
        private AbstractDatabaseFeature<ProfileData> feature;
        private ProfileData profileData;

        @Override
        public void run() {
            try (Connection connection = SpigotCore.PLUGIN.getDatabaseConnectionManager().getHikariDataSource().getConnection()) {

                feature.databaseSave(player, profileData, connection);

            } catch (SQLException exe) {
                exe.printStackTrace();
            }
        }
    }
}
