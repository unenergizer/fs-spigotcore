package com.forgestorm.spigotcore.features.optional.realm;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.database.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.features.required.database.ProfileData;
import com.forgestorm.spigotcore.features.required.database.global.SqlSearchData;
import com.forgestorm.spigotcore.util.text.Text;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RealmManager extends AbstractDatabaseFeature implements FeatureOptional, Listener {


    private final Map<Player, Realm> openRealmsMap = new ConcurrentHashMap<>();
    private RealmCooldownTimer realmCooldownTimer;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        realmCooldownTimer = new RealmCooldownTimer();
        realmCooldownTimer.runTaskTimerAsynchronously(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        PlayerInteractEvent.getHandlerList().unregister(this);
        BlockDamageEvent.getHandlerList().unregister(this);
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
        BlockPhysicsEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);

        for (Map.Entry<Player, Realm> entry : openRealmsMap.entrySet()) {
            closeRealm(entry.getKey());
        }

        realmCooldownTimer.cancel();
        realmCooldownTimer = null;
    }

    /**
     * Opens a player's realm.
     *
     * @param player            The player who we will open a realm for.
     * @param realmOpenLocation The location where the realm is being opened.
     */
    private void openRealm(Player player, Location realmOpenLocation) {
        // Make sure the user doesn't already have a realm open.
        if (openRealmsMap.containsKey(player)) {
            player.sendMessage(RealmMessages.REALM_PORTAL_DUPLICATE.getMessage());
            CommonSounds.ACTION_FAILED.play(player);
            return;
        }

        // Check to see if the player is on a realm cooldown
        if (realmCooldownTimer.isOnCooldown(player)) {
            player.sendMessage(ChatColor.RED + "You can not open your realm yet. Please wait " + realmCooldownTimer.getTimeLeft(player) + " seconds.");
            CommonSounds.ACTION_FAILED.play(player);
            return;
        }

        // Make sure the player is opening their realm in a lobby
        if (!player.getWorld().equals(Bukkit.getWorlds().get(0))) {
            player.sendMessage(ChatColor.RED + "You must be in a lobby world to open your realm.");
            CommonSounds.ACTION_FAILED.play(player);
            return;
        }

        // Make sure the player isn't setting the realm close to another realm.
        if (getNearbyPortals(realmOpenLocation, 2)) {
            player.sendMessage(RealmMessages.REALM_PORTAL_PLACE_TOO_CLOSE.getMessage());
            CommonSounds.ACTION_FAILED.play(player);
            return;
        }

        asyncDatastoreLoad(player);
        Realm realm = new Realm(this, player, realmOpenLocation);
        realm.onRealmEnable();
        openRealmsMap.put(player, realm);

        player.sendMessage(ChatColor.GREEN + "Loading your realm.");
    }

    /**
     * Closes a players realm.
     *
     * @param player The player we will close a realm for.
     */
    private void closeRealm(Player player) {
        if (!openRealmsMap.containsKey(player)) return;
        realmCooldownTimer.addRealmCooldown(player);

        Realm realm = openRealmsMap.get(player);
        realm.onRealmDisable();
        asyncDatastoreSave(player);
        openRealmsMap.remove(player);

        player.sendMessage(ChatColor.GREEN + "Closing and saving your realm.");
    }

    @Override
    public ProfileData databaseLoad(Player player, Connection connection, ResultSet resultSet) throws SQLException {

        RealmProfileData realmProfileData = new RealmProfileData();

        realmProfileData.setHasRealm(resultSet.getBoolean("has_realm"));
        realmProfileData.setRealmTitle(resultSet.getString("title"));
        realmProfileData.setRealmTier(resultSet.getInt("tier"));
        realmProfileData.setRealmInsideDoorLocation(resultSet.getString("inside_door_location"));
        realmProfileData.setDataLoaded(true);

        return realmProfileData;
    }

    @Override
    public void databaseSave(Player player, ProfileData profileData, Connection connection) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE fs_feature_realm SET has_realm=?, title=?, tier=?, inside_door_location=? WHERE owner_uuid=?");

        RealmProfileData realmProfileData = (RealmProfileData) profileData;

        preparedStatement.setBoolean(1, realmProfileData.isHasRealm());
        preparedStatement.setString(2, realmProfileData.getRealmTitle());
        preparedStatement.setInt(3, realmProfileData.getRealmTier());
        preparedStatement.setString(4, realmProfileData.getRealmInsideDoorLocation());
        preparedStatement.setString(5, player.getUniqueId().toString());

        preparedStatement.execute();
    }

    @Override
    public ProfileData firstTimeSave(Player player, Connection connection) throws SQLException {

        boolean newHasRealm = false;
        String newRealmTitle = "Welcome to my realm!";
        int newRealmTier = 0;
        String newRealmInsideDoorLocation = "11/18/7";

        PreparedStatement newPlayerStatement = connection.prepareStatement("INSERT INTO fs_feature_realm " +
                "(owner_uuid, has_realm, title, tier, inside_door_location) " +
                "VALUES(?, ?, ?, ?, ?)");

        newPlayerStatement.setString(1, player.getUniqueId().toString());
        newPlayerStatement.setBoolean(2, newHasRealm);
        newPlayerStatement.setString(3, newRealmTitle);
        newPlayerStatement.setInt(4, newRealmTier);
        newPlayerStatement.setString(5, newRealmInsideDoorLocation);

        newPlayerStatement.execute();

        RealmProfileData realmProfileData = new RealmProfileData();
        realmProfileData.setHasRealm(newHasRealm);
        realmProfileData.setRealmTitle(newRealmTitle);
        realmProfileData.setRealmTier(newRealmTier);
        realmProfileData.setRealmInsideDoorLocation(newRealmInsideDoorLocation);
        realmProfileData.setDataLoaded(true);

        return realmProfileData;
    }

    @Override
    public SqlSearchData searchForData(Player player, Connection connection) {
        return new SqlSearchData("fs_feature_realm", "owner_uuid", player.getUniqueId().toString());
    }

    /**
     * This is called when a player breaks their realm portal form the main world.
     * This will then begin the unloading process for the realm.
     *
     * @param player   The player who attempted to close down the realm.
     * @param location The location of the attempted block break.
     */
    private void removePlayerRealmAtLocation(Player player, Location location) {
        if (!openRealmsMap.containsKey(player)) return;
        Realm realm = openRealmsMap.get(player);

        int x = location.getBlockX();
        int z = location.getBlockZ();
        int realmX = realm.getOutsideDoorLocation().getBlockX();
        int realmZ = realm.getOutsideDoorLocation().getBlockZ();

        // Check to make sure the player is only closing their realm.
        // TODO: Include Y axis check.
        if (x != realmX) return;
        if (z != realmZ) return;

        closeRealm(player);
    }

    /**
     * This is a conditional check to make sure their isn't a realm nearby the one that is
     * attempting to be placed. We prevent portals from being next to each other for various
     * technical reasons. For instance, we don't want portal blocks to combine and look like
     * one big portal.
     *
     * @param location The location of the attempted portal placement.
     * @param radius   How far this location must be from other portal locations.
     * @return True if we can set a portal at the given location, false if another portal is too close.
     */
    private static boolean getNearbyPortals(Location location, int radius) {
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    if (location.getWorld().getBlockAt(x, y, z).getType().equals(Material.PORTAL)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // Test for opening player realm
        if (!player.getWorld().equals(Bukkit.getWorlds().get(0))) return;
        if (!player.isSneaking()) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.COMPASS) return;

        openRealm(player, block.getLocation());
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();

        // Check to see if were closing a player realm
        if (!player.getWorld().equals(Bukkit.getWorlds().get(0))) return; // Only close realm from lobby world
        if (event.getBlock().getType() != Material.PORTAL) return;
        event.setCancelled(true);
        removePlayerRealmAtLocation(player, event.getBlock().getLocation());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        // Check to see if were closing a player realm
        if (!event.getEntity().getWorld().equals(Bukkit.getWorlds().get(0))) return;
        if (!(event.getEntity() instanceof ArmorStand)) return;
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        removePlayerRealmAtLocation(player, event.getEntity().getLocation());
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        //Prevent the nether portals from breaking.
        if (event.getBlock().getType().equals(Material.PORTAL)) {
            event.setCancelled(true);
        }
        //Prevent the nether portals from breaking.
        if (event.getChangedType().equals(Material.PORTAL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (openRealmsMap.containsKey(event.getPlayer())) closeRealm(event.getPlayer());
    }

    @AllArgsConstructor
    public enum RealmMessages {
        BAR_REALM("&8&l&m--------------&r&l &l &l &f&lRealm Commands&l &l &l &8&l&m--------------"),
        REALM_PORTAL_DUPLICATE("&cYou already have a realm opened! Close it to open your realm at another location."),
        REALM_PORTAL_OPENED("&d&l* Realm Portal OPENED *"),
        REALM_PORTAL_TITLE("&7Type &3/realm help &7for a list of commands."),
        REALM_PORTAL_PLACE_DENY_BLOCK("&cYou &ncannot&c open a realm portal here."),
        REALM_PORTAL_PLACE_TOO_CLOSE("&cYou &ncannot&c place a realm portal so close to another one.");

        private final String message;

        public String getMessage() {
            return Text.color(message);
        }
    }
}
