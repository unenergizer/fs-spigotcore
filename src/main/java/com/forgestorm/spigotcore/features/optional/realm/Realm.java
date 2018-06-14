package com.forgestorm.spigotcore.features.optional.realm;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.features.events.FeatureProfileDataLoadEvent;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import com.forgestorm.spigotcore.util.text.Text;
import com.forgestorm.spigotcore.util.scheduler.ResetTimer;
import com.forgestorm.spigotcore.util.world.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
class Realm implements Listener {

    private final ResetTimer resetTimer = new ResetTimer();
    private final List<Player> allowedBuilders = new ArrayList<>();
    private final List<Player> visitors = new CopyOnWriteArrayList<>();
    private final RealmManager realmManager;
    private final Player realmOwner;
    private final Location outsideDoorLocation;
    private RealmDoorway realmDoorwayOutside;
    private RealmDoorway realmDoorwayInside;
    private Location insideDoorLocation;
    private World realmWorld;
    private RealmAlignment realmAlignment;
    @Setter
    private String title;
    private RealmTier realmTier;

    Realm(RealmManager realmManager, Player realmOwner, Location outsideDoorLocation) {
        this.realmManager = realmManager;
        this.realmOwner = realmOwner;
        this.outsideDoorLocation = outsideDoorLocation;

        realmDoorwayOutside = new RealmDoorway(outsideDoorLocation, realmOwner);
    }

    /**
     * Called when a realm needs to start up.
     */
    void onRealmEnable() {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        realmAlignment = RealmAlignment.HOSTILE;
        resetTimer.runTaskTimerAsynchronously(SpigotCore.PLUGIN, 0, 20);
    }

    /**
     * Called when a realm needs to close down.
     */
    void onRealmDisable() {
        resetTimer.cancel();

        // Remove visitors
        visitors.forEach(this::exitRealm);

        // Unregister listeners
        EntityDamageEvent.getHandlerList().unregister(this);
        WorldLoadEvent.getHandlerList().unregister(this);
        PlayerPortalEvent.getHandlerList().unregister(this);
        FeatureProfileDataLoadEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        BlockPlaceEvent.getHandlerList().unregister(this);
        PlayerInteractEntityEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);

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
        player.teleport(LocationUtil.addToLocation(insideDoorLocation, 0.5, .5, 0.5));
        player.sendMessage("");
        player.sendMessage(CenterChatText.centerChatMessage(Text.color("&7&m-&r &7&m--&r &7&m---&r &7&m----&r &3&lRealm Joined &7&m----&r &7&m---&r &7&m--&r &7&m-&r")));
        player.sendMessage(Text.color("&7Owner&8: &r" + realmOwner.getName()));
        player.sendMessage(Text.color("&7Alignment&8: &r" + realmAlignment.getAlignment()));
        player.sendMessage(Text.color("&7Tier&8: &r" + realmTier.getTier()));
        player.sendMessage(Text.color("&7Build Width&8: &r" + realmTier.getSize() + "&7x&r" + realmTier.getSize()));
        player.sendMessage(Text.color("&7JoinMessage&8: &r" + title));

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
        player.teleport(LocationUtil.addToLocation(outsideDoorLocation, 0.5, .5, 0.5));

        if (player.getName().equals(realmOwner.getName()))
            player.sendMessage(Text.color("&cYou have left your realm."));
        else player.sendMessage(Text.color("&cYou have left &e" + realmOwner.getName() + "'s&c realm."));

        visitors.remove(player);
    }

    public void addBuilder(Player player) {
        if (realmOwner.getName().equals(player.getName())) {
            realmOwner.sendMessage(Text.color("&cYou are the owner of this realm! You can already build."));
            CommonSounds.ACTION_FAILED.play(player);
            return;
        }

        if (!allowedBuilders.contains(player)) {
            realmOwner.sendMessage(Text.color("&aYou added &e" + player.getDisplayName() + "&a to your realm builder list!"));
            player.sendMessage(Text.color("&aYou have been added to &e" + realmOwner.getDisplayName() + "'s &arealm builder list!"));
            allowedBuilders.add(player);
        } else {
            realmOwner.sendMessage(Text.color("&cPlayer &e" + player.getDisplayName() + "&c is already added to your realm builder list!"));
        }
    }

    public void removeBuilder(Player player) {
        if (realmOwner.getName().equals(player.getName())) {
            realmOwner.sendMessage(Text.color("&cYou are the owner of this realm! You can not be removed."));
            CommonSounds.ACTION_FAILED.play(player);
            return;
        }
        if (allowedBuilders.contains(player)) {
            realmOwner.sendMessage(Text.color("&cYou removed &e" + player.getDisplayName() + " &cfrom your realms builder list!"));
            player.sendMessage(Text.color("&cYou have been removed from &e" + realmOwner.getDisplayName() + "'s &crealm builder list."));
            allowedBuilders.remove(player);
        } else {
            realmOwner.sendMessage(Text.color("&cPlayer &e" + player.getDisplayName() + "&c is not a builder on your realm!"));
        }
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
        if (realmWorld == null) return;
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
        realmDoorwayOutside.enable();
        insideDoorLocation = realmManager.getProfileData(realmOwner).getRealmInsideDoorLocation(realmWorld);

        realmDoorwayInside = new RealmDoorway(insideDoorLocation, realmOwner);
        realmDoorwayInside.enable();
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

    /**
     * Make sure only the realm owner and friends can break blocks.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // World specific check
        if (!player.getWorld().getName().equals(realmWorld.getName())) return;

        // Make sure only allowed people can build here
        if (!allowedBuilders.contains(player) && !player.getUniqueId().equals(realmOwner.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(Text.color("&cYou may not build here. Get permission from &e" + realmOwner.getName() + "&c to build."));
            CommonSounds.ACTION_FAILED.play(player);
        } else {
            event.setCancelled(false);
        }
    }

    /**
     * Check to make sure only realm owners and friends can place blocks.
     * Also check to make sure players are building in the right bounds.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        // World specific check
        if (!player.getWorld().getName().equals(realmWorld.getName())) return;

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

    /**
     * Adding and removing builders from realm.
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        Entity clickedEntity = event.getRightClicked();

        if (itemInMainHand == null) return;
        if (!player.getWorld().getName().equals(realmWorld.getName())) return;
        if (player.isSneaking() && itemInMainHand.getType() == Material.COMPASS && clickedEntity instanceof Player) {

            if (!resetTimer.containsPlayer(player)) {
                resetTimer.addPlayer(player, 2);
            } else {
                return;
            }

            if (allowedBuilders.contains(clickedEntity)) {
                player.sendMessage(Text.color("&cYou removed &e" + ((Player) clickedEntity).getDisplayName() + " &cfrom your realm."));
                clickedEntity.sendMessage(Text.color("&cYou have been removed from &e" + player.getDisplayName() + "'s &crealm."));
                allowedBuilders.remove(clickedEntity);
            } else {
                player.sendMessage(Text.color("&aYou added &e" + ((Player) clickedEntity).getDisplayName() + "&a to your realm!"));
                clickedEntity.sendMessage(Text.color("&aYou have been added to &e" + player.getDisplayName() + "'s &arealm!"));
                allowedBuilders.add((Player) clickedEntity);
            }
        }
    }

    /**
     * Change of the inside door location.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (!player.getUniqueId().toString().equals(realmOwner.getUniqueId().toString())) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        // World specific check
        if (!player.getWorld().getName().equals(realmWorld.getName())) return;

        //Check for realm portal move.
        if (!player.isSneaking() || event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() == Material.PORTAL) return;
        if (event.getItem() == null || event.getItem().getType() != Material.COMPASS) return;

        //Remove Old Realm inside Portal
        realmDoorwayInside.disable();

        //Set New Realm inside Portal
        insideDoorLocation = block.getLocation();
        realmDoorwayInside = new RealmDoorway(insideDoorLocation, realmOwner);
        realmDoorwayInside.enable();

        //Save location to player profile.
        RealmProfileData realmProfileData = realmManager.getProfileData(player);
        realmProfileData.setRealmInsideDoorLocation(insideDoorLocation);
    }
}
