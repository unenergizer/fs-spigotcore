package com.forgestorm.spigotcore.features.optional.realm;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.events.FeatureProfileDataLoadEvent;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.util.world.LocationUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

@Getter
class Realm implements Listener {

    private final RealmDoorway realmDoorwayOutside = new RealmDoorway();
    private final RealmDoorway realmDoorwayInside = new RealmDoorway();
    private final RealmManager realmManager;
    private final Player realmOwner;
    private final Location outsideDoorLocation;
    private Location insideDoorLocation;
    private World realmWorld;

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
    }

    /**
     * Called when a realm needs to close down.
     */
    void onRealmDisable() {
        EntityDamageEvent.getHandlerList().unregister(this);
        WorldLoadEvent.getHandlerList().unregister(this);
        PlayerPortalEvent.getHandlerList().unregister(this);
        FeatureProfileDataLoadEvent.getHandlerList().unregister(this);
        realmDoorwayOutside.disable();
        realmDoorwayInside.disable();

        File file = new File(".." + File.separator + "player_realms" + File.separator + realmWorld.getName());

        // Save the realm world
        SpigotCore.PLUGIN.getWorldManager().unloadWorld(realmWorld.getName(), true, file, true);
    }

    /**
     * Called when any player (including and not including the realm owner) enters this realm world.
     *
     * @param player The player entering the realm.
     */
    private void enterRealm(Player player) {
        player.teleport(LocationUtil.addToLocation(insideDoorLocation, 0.0, 1.5, 0.0));
    }

    /**
     * Called when any player (including and not including the realm owner) exits this realm world.
     *
     * @param player The player exiting the realm.
     */
    private void exitRealm(Player player) {
        player.teleport(LocationUtil.addToLocation(outsideDoorLocation, 0.0, 1.5, 0.0));
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

        Console.sendMessage("World Loaded");

        realmWorld = event.getWorld();
        realmDoorwayOutside.enable(realmOwner, outsideDoorLocation);
        insideDoorLocation = ((RealmData) realmManager.getProfileData(realmOwner)).getRealmInsideDoorLocation(realmWorld);
        realmDoorwayInside.enable(realmOwner, insideDoorLocation);
    }

    /**
     * Load the realm world after we get data from the database.
     */
    @EventHandler
    public void onFeatureProfileLoad(FeatureProfileDataLoadEvent event) {
        Console.sendMessage("FeatureProfileDataLoadEvent");
        if (!(event.getFeature() instanceof RealmManager)) return;
        if (!event.getPlayer().getUniqueId().equals(realmOwner.getUniqueId())) return;

        RealmData realmData = (RealmData) event.getProfileData();

        // Load world
        if (!realmData.isHasRealm()) {
            SpigotCore.PLUGIN.getWorldManager().loadWorld(realmOwner.getUniqueId().toString(),
                    new File("plugins" + File.separator + "ForgeStorm-SpigotCore" + File.separator + "realm" + File.separator + "defaultRealmWorld"));
            realmData.setHasRealm(true);
        } else {
            SpigotCore.PLUGIN.getWorldManager().loadWorld(realmOwner.getUniqueId().toString(),
                    new File(".." + File.separator + "player_realms" + File.separator + realmOwner.getUniqueId()));
        }
    }
}
