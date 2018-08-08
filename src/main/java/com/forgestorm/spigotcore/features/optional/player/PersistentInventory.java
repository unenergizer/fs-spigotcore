package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.database.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.features.required.database.ProfileData;
import com.forgestorm.spigotcore.features.required.database.global.SqlSearchData;
import com.forgestorm.spigotcore.features.required.featuretoggle.FeatureToggleManager;
import com.forgestorm.spigotcore.util.item.InventoryStringDeSerializer;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PersistentInventory extends AbstractDatabaseFeature<PersistentInventoryData> implements Listener {

    private List<Player> playerInventorySyncList = new CopyOnWriteArrayList<>();

    private BukkitRunnable syncRunnable;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        syncRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                processInventoriesSync();
            }
        };
        syncRunnable.runTaskTimer(SpigotCore.PLUGIN, FeatureToggleManager.FEATURE_TASK_START_DELAY, 5);

        Bukkit.getOnlinePlayers().forEach(this::loadInventory); // Load player if feature enabled manually
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        syncRunnable.cancel();

        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);

        Bukkit.getOnlinePlayers().forEach(this::saveInventory); // Save player if feature disabled manually
    }

    /**
     * Compile and apply Base64 inventory for players from the database.
     */
    private void processInventoriesSync() {
        for (Iterator<Player> iterator = playerInventorySyncList.iterator(); iterator.hasNext(); ) {
            Player player = iterator.next();

            if (isProfileDataLoaded(player)) {
                String persistentInventoryData = getProfileData(player).getInventoryBase64();

                player.getInventory().clear();

                try {
                    //System.out.println("LOAD InvData: " + profileData.getSerializedInventory());
                    ItemStack[] inventory = InventoryStringDeSerializer.stacksFromBase64(persistentInventoryData);
                    player.getInventory().setContents(inventory);

                    player.sendMessage(Text.color("&aLoading complete!"));

                    getProfileData(player).setInventoryBase64(""); // Clearing data (watching for wipes/dupes)
                } catch (IllegalArgumentException | IOException e) {
                    e.printStackTrace();
                }

                playerInventorySyncList.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadInventory(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveInventory(event.getPlayer());
    }

    private void loadInventory(Player player) {
        if (!isProfileDataLoaded(player)) {
            asyncDatastoreLoad(player);
            playerInventorySyncList.add(player);
//            player.sendMessage(Text.color("&aLoading your inventory data..."));
        }
    }

    private void saveInventory(Player player) {
        if (isProfileDataLoaded(player)) {
            asyncDatastoreSave(player);
//            player.sendMessage(Text.color("&aSaving your inventory data..."));

            PersistentInventoryData persistentInventoryData = getProfileData(player);
            persistentInventoryData.setInventoryBase64(InventoryStringDeSerializer.toBase64(player.getInventory().getContents()));
        }
    }

    @Override
    public ProfileData databaseLoad(Player player, Connection connection, ResultSet resultSet) throws SQLException {
        PersistentInventoryData profileData = new PersistentInventoryData();

        profileData.setInventoryBase64(resultSet.getString("inventory_base_64"));
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public void databaseSave(Player player, PersistentInventoryData profileData, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE fs_player_inventory SET inventory_base_64=? WHERE uuid=?");

        preparedStatement.setString(1, profileData.getInventoryBase64());
        preparedStatement.setString(2, player.getUniqueId().toString());

        preparedStatement.execute();
    }

    @Override
    public ProfileData firstTimeSave(Player player, Connection connection) throws SQLException {
        final String inventory_base_64 = "";

        PreparedStatement newPlayerStatement = connection.prepareStatement("INSERT INTO fs_player_inventory " +
                "(uuid, inventory_base_64) " +
                "VALUES(?, ?)");

        newPlayerStatement.setString(1, player.getUniqueId().toString());
        newPlayerStatement.setString(2, inventory_base_64);

        newPlayerStatement.execute();

        PersistentInventoryData profileData = new PersistentInventoryData();
        profileData.setInventoryBase64(inventory_base_64);
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public SqlSearchData searchForData(Player player, Connection connection) {
        return new SqlSearchData("fs_player_inventory", "uuid", player.getUniqueId().toString());
    }
}
