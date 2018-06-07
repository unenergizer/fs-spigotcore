package com.forgestorm.spigotcore.features.optional.realm;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.features.events.FeatureProfileDataLoadEvent;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import com.forgestorm.spigotcore.util.text.Text;
import com.forgestorm.spigotcore.util.world.LocationUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
class Realm implements Listener {

    private final List<Player> allowedBuilders = new ArrayList<>();
    private final List<Player> visitors = new CopyOnWriteArrayList<>();
    private final RealmDoorway realmDoorwayOutside = new RealmDoorway();
    private final RealmDoorway realmDoorwayInside = new RealmDoorway();
    private final RealmManager realmManager;
    private final Player realmOwner;
    private final Location outsideDoorLocation;
    private Location insideDoorLocation;
    private World realmWorld;
    private RealmAlignment realmAlignment;
    private String title;
    private RealmTier realmTier;

    Realm(RealmManager realmManager, Player realmOwner, Location outsideDoorLocation) {
        this.realmManager = realmManager;
        this.realmOwner = realmOwner;
        this.outsideDoorLocation = outsideDoorLocation;
    }

    /**
     * Called when a realm needs to start up.
     */
    void onRealmEnable() {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        realmAlignment = RealmAlignment.HOSTILE;
    }

    /**
     * Called when a realm needs to close down.
     */
    void onRealmDisable() {

        // Remove visitors
        visitors.forEach(this::exitRealm);

        // Unregister listeners
        EntityDamageEvent.getHandlerList().unregister(this);
        WorldLoadEvent.getHandlerList().unregister(this);
        PlayerPortalEvent.getHandlerList().unregister(this);
        FeatureProfileDataLoadEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        BlockPlaceEvent.getHandlerList().unregister(this);

        // Close doorways
        realmDoorwayOutside.disable();
        realmDoorwayInside.disable();

        // Save the realm world
        File file = new File(".." + File.separator + "player_realms" + File.separator + realmWorld.getName());
        SpigotCore.PLUGIN.getWorldManager().unloadWorld(realmWorld.getName(), true, file, true);
    }

    /**
     * Called when any player (including and not including the realm owner) enters this realm world.
     *
     * @param player The player entering the realm.
     */
    private void enterRealm(Player player) {
        player.teleport(LocationUtil.addToLocation(insideDoorLocation, 0.0, 1.5, 0.0));
        player.sendMessage("");
        player.sendMessage(CenterChatText.centerChatMessage(Text.color("&7&m-&r &7&m--&r &7&m---&r &7&m----&r &3&lRealm Joined &7&m----&r &7&m---&r &7&m--&r &7&m-&r")));
        player.sendMessage(Text.color("&eOwner&8: &r" + realmOwner.getName()));
        player.sendMessage(Text.color("&eAlignment&8: &r" + realmAlignment.getAlignment()));
        player.sendMessage(Text.color("&eTier&8: &r" + realmTier.getTier()));
        player.sendMessage(Text.color("&eBuild Width&8: &r" + realmTier.getSize() + "&7x&r" + realmTier.getSize()));
        player.sendMessage(Text.color("&eJoinMessage&8: &r" + title));

        // Removes double jump, if feature is active
        // Enabled again automatically by DoubleJump feature when player rejoins the main world
        player.setAllowFlight(false);
        player.setFlying(false);

        visitors.add(player);
    }

    /**
     * Called when any player (including and not including the realm owner) exits this realm world.
     *
     * @param player The player exiting the realm.
     */
    private void exitRealm(Player player) {
        player.teleport(LocationUtil.addToLocation(outsideDoorLocation, 0.0, 1.5, 0.0));
        player.sendMessage(Text.color("&cYou have left &e" + realmOwner.getName() + "'s&c realm."));

        visitors.remove(player);
    }

    /**
     * Check to see if the player has stepped into a realm portal.
     * Additionally we check see if the player is entering/exiting this realm via the realm portal.
     */
    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        if (event.getCause() != PlayerPortalEvent.TeleportCause.NETHER_PORTAL) return;
        if (!LocationUtil.isWorldLoaded(realmOwner.getUniqueId().toString())) return;

        event.getPlayer().setPortalCooldown(20 * 3); // cooldown in ticks

        // Check doorway enter/exit
        if (LocationUtil.doLocationsMatch(event.getPlayer().getLocation(), outsideDoorLocation)) {
            event.setCancelled(true);
            enterRealm(event.getPlayer());
        } else if (LocationUtil.doLocationsMatch(event.getPlayer().getLocation(), insideDoorLocation)) {
            event.setCancelled(true);
            exitRealm(event.getPlayer());
        }
    }

    /**
     * Check to see if the player has left the realm by jumping into the void.
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) return;
        if (!realmWorld.equals(entity.getWorld())) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;

        event.setCancelled(true); // Stop annoying damage noise.

        // Wait one tick and respawn the player.
        // Prevents "Player X moved to quickly" console spam.
        new BukkitRunnable() {
            public void run() {
                exitRealm((Player) entity);
                cancel();
            }
        }.runTaskLater(SpigotCore.PLUGIN, 1L);
    }

    /**
     * After the world is loaded, setup some additional variables.
     */
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!event.getWorld().getName().equals(realmOwner.getUniqueId().toString())) return;

        realmWorld = event.getWorld();
        realmDoorwayOutside.enable(realmOwner, outsideDoorLocation);
        insideDoorLocation = ((RealmProfileData) realmManager.getProfileData(realmOwner)).getRealmInsideDoorLocation(realmWorld);
        realmDoorwayInside.enable(realmOwner, insideDoorLocation);
    }

    /**
     * Load the realm world after we get data from the database.
     */
    @EventHandler
    public void onFeatureProfileLoad(FeatureProfileDataLoadEvent event) {
        if (!(event.getFeature() instanceof RealmManager)) return;
        if (!event.getPlayer().getUniqueId().equals(realmOwner.getUniqueId())) return;

        RealmProfileData realmProfileData = (RealmProfileData) event.getProfileData();

        // Set vars
        title = realmProfileData.getRealmTitle();
        realmTier = RealmTier.valueOfTier(realmProfileData.getRealmTier());

        // Load world from backup or create a new one
        if (!realmProfileData.isHasRealm()) {
            SpigotCore.PLUGIN.getWorldManager().loadWorld(realmOwner.getUniqueId().toString(),
                    new File(SpigotCore.PLUGIN.getDataFolder() + File.separator + "realm" + File.separator + "defaultRealmWorld"));
            realmProfileData.setHasRealm(true);
        } else {
            SpigotCore.PLUGIN.getWorldManager().loadWorld(realmOwner.getUniqueId().toString(),
                    new File(".." + File.separator + "player_realms" + File.separator + realmOwner.getUniqueId()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Make sure only allowed people can build here
        if (!allowedBuilders.contains(player) && !player.getUniqueId().equals(realmOwner.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(Text.color("&cYou may not build here. Get permission from &e" + realmOwner.getName() + "&c to build."));
            CommonSounds.ACTION_FAILED.play(player);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        // Make sure only allowed people can build here
        if (!allowedBuilders.contains(player) && !player.getUniqueId().equals(realmOwner.getUniqueId())) {
            player.sendMessage(Text.color("&cYou may not build here. Get permission from &e" + realmOwner.getName() + "&c to build."));
            CommonSounds.ACTION_FAILED.play(player);
            return;
        }

        // Check to make sure we are building within the realm tier dimensions
        int x = event.getBlock().getLocation().getBlockX();
        int z = event.getBlock().getLocation().getBlockZ();

        if (x <= realmTier.getSize() && z <= realmTier.getSize() && x >= 0 && z >= 0) {
            event.setCancelled(false);
        } else {
            player.sendMessage(Text.color("&cYou may not build here."));
            if (player.getUniqueId().equals(realmOwner.getUniqueId()))
                player.sendMessage(Text.color("&cUpgrade your realm tier to build further out."));
            CommonSounds.ACTION_FAILED.play(player);
        }
    }
}
