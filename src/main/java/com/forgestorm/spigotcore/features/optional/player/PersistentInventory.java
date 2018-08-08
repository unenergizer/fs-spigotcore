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
import java.util.ArrayList;
import java.util.List;

public class PersistentInventory extends AbstractDatabaseFeature<PersistentInventoryData> implements Listener {

    private List<Player> inventoriesLoading = new ArrayList<>();

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
        syncRunnable.runTaskTimer(SpigotCore.PLUGIN, FeatureToggleManager.FEATURE_TASK_START_DELAY, 1);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        syncRunnable.cancel();

        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
    }

    private void processInventoriesSync() {
        for (Player player : inventoriesLoading) {
            if (isProfileDataLoaded(player)) {
                String persistentInventoryData = getProfileData(player).getInventoryBase64();

                player.getInventory().clear();

                if (persistentInventoryData != null) return;
                if (!persistentInventoryData.equals("")) return;
                try {
                    //System.out.println("LOAD InvData: " + profileData.getSerializedInventory());
                    ItemStack[] inventory = InventoryStringDeSerializer.stacksFromBase64(persistentInventoryData);
                    player.getInventory().setContents(inventory);

                    getProfileData(player).setInventoryBase64(""); // Clearing data (watching for wipes/dupes)
                } catch (IllegalArgumentException | IOException e) {
                    e.printStackTrace();
                }

                inventoriesLoading.remove(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!isProfileDataLoaded(player)) {
            asyncDatastoreLoad(player);
            inventoriesLoading.add(player);
            player.sendMessage(Text.color("&aLoading your inventory data..."));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (isProfileDataLoaded(player)) {
            asyncDatastoreSave(player);
            player.sendMessage(Text.color("&Saving your inventory data..."));

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
