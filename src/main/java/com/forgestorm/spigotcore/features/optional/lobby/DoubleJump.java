package com.forgestorm.spigotcore.features.optional.lobby;

import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class DoubleJump implements FeatureOptional {

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getOnlinePlayers().forEach(this::setupPlayer);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        Bukkit.getOnlinePlayers().forEach(this::removePlayer);
    }

    private void setupPlayer(Player player) {
        player.setAllowFlight(true);
        player.setFlying(false);
    }

    private void removePlayer(Player player) {
        player.setAllowFlight(false);
        player.setFlying(false);
    }

//    /**
//     * Test to see if the player is in a main lobby world.
//     * The main lobby world is:
//     * -Bukkit.getWorlds().get(0);
//     * -Bukkit.getWorlds().get("World");
//     *
//     * @param player The player we will test.
//     * @return True if they are in the main world, false otherwise.
//     */
//    private boolean inOtherWorld(Player player) {
////        return !player.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName());
//        return false; // Enables double jump on all worlds.
//    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        setupPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

//        if (inOtherWorld(player)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) return;

        // Start double jump
        player.setAllowFlight(true);

    }

    @EventHandler
    public void onPlayerFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

//        if (inOtherWorld(player)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;

        // Stop double jump
        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        // Launch the player
        player.setVelocity(player.getLocation().getDirection().multiply(2.0D).setY(0.9D));
        player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, .7f);

    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    /**
     * Added listener to make sure people who joined our main world, has the correct setup to
     * be able to double jump. This is useful if players have left a
     * {@link com.forgestorm.spigotcore.features.optional.realm.Realm} and flying and other vars
     * have been turned off. Using a listener here allows both features to play nicely wither or not
     * one or another is enabled/disabled.
     */
    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        if (event.getCause() != PlayerPortalEvent.TeleportCause.NETHER_PORTAL) return;
        // if (!Bukkit.getWorlds().get(0).getName().equals(event.getPlayer().getWorld().getName())) return;
        setupPlayer(event.getPlayer());
    }
}