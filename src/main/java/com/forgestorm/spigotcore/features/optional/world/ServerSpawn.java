package com.forgestorm.spigotcore.features.optional.world;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.features.InitCommands;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.events.PlayerTeleportSpawnEvent;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple features to control where a player spawns in the world.
 * If the player fall's into the void, we can teleport them back to the main spawn.
 */
public class ServerSpawn implements FeatureOptional, InitCommands, LoadsConfig {

    private boolean spawnOnVoidDamage;
    private CommandOptions commandOptions;
    private Location spawnLocation;

    private Map<Player, Integer> timeLeft;
    private BukkitRunnable spawnTimer;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getWorlds().get(0).setSpawnLocation(
                (int) spawnLocation.getX(),
                (int) spawnLocation.getY(),
                spawnLocation.getBlockZ()
        );

        if (!commandOptions.countdownEnabled) return;
        timeLeft = new ConcurrentHashMap<>();
        spawnTimer = new BukkitRunnable() {
            @Override
            public void run() {
                doCountdown();
            }
        };
        spawnTimer.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        if (commandOptions.commandEnabled && commandOptions.countdownEnabled) {
            spawnTimer.cancel();
            timeLeft.clear();
        }
    }

    @Override
    public List<FeatureOptionalCommand> registerAllCommands() {
        List<FeatureOptionalCommand> commands = new ArrayList<>();
        commands.add(new SpawnCommand());
        return commands;
    }

    @Override
    public void loadConfiguration() {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.SERVER_SPAWN.toString()));

        spawnOnVoidDamage = config.getBoolean("Settings.spawnOnVoidDamage");

        commandOptions = new CommandOptions(
                config.getBoolean("Settings.command.enabled"),
                config.getBoolean("Settings.command.useCountdown"),
                config.getInt("Settings.command.countdownTime"),
                config.getBoolean("Settings.command.cancelOnMove")
        );

        spawnLocation = new Location(Bukkit.getWorlds().get(0),
                config.getDouble("Settings.spawn.x"),
                config.getDouble("Settings.spawn.y"),
                config.getDouble("Settings.spawn.z"),
                (float) config.getDouble("Settings.spawn.yaw"),
                (float) config.getDouble("Settings.spawn.pitch")
        );

        // Spawn above ground
        spawnLocation.add(0, 1.5, 0);
    }

    private void doCountdown() {
        if (timeLeft.isEmpty()) return;
        for (Map.Entry<Player, Integer> entry : timeLeft.entrySet()) {
            Player player = entry.getKey();
            int time = entry.getValue() - 1;

            if (time <= 0) {
                timeLeft.remove(player);
                teleport(player, CommonSounds.ACTION_SUCCESS);
            } else {
                entry.setValue(time);
                if (time == 1) player.sendMessage(ChatColor.GRAY + "Teleporting in " + time + " second.");
                else player.sendMessage(ChatColor.GRAY + "Teleporting in " + time + " seconds.");
            }
        }
    }

    /**
     * Consistent teleport with added effects.
     *
     * @param player The player to teleport.
     */
    private void teleport(Player player) {
        teleport(player, null);
    }

    /**
     * Consistent teleport with added effects.
     *
     * @param player The player to teleport.
     * @param sound  The sound to play when a player is teleported.
     */
    private void teleport(Player player, CommonSounds sound) {
        player.teleport(spawnLocation);
        player.setFallDistance(0F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 100));
        if (sound != null) sound.play(player);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        //If player enters a Ender portal, teleport them back to spawn pad.
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {

            //Cancel teleportation to the END_GAME
            event.setCancelled(true);

            new BukkitRunnable() {
                public void run() {

                    //Teleport the player.
                    teleport(player, CommonSounds.ACTION_SUCCESS);

                    cancel();
                }
            }.runTaskLater(SpigotCore.PLUGIN, 1L);
        }

//        //If player enters a Ender portal, teleport them back to spawn pad.
//        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
//
//            // Cancel teleportation to the NETHER
//            event.setCancelled(true);
//
//            // Send player to the lobby
//            player.chat("/lobby");
//        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!commandOptions.countdownEnabled) return;
        if (!commandOptions.cancelOnMove) return;
        if (!timeLeft.containsKey(player)) return;

        double moveX = event.getFrom().getX();
        double moveToX = event.getTo().getX();

        double moveY = event.getFrom().getY();
        double moveToY = event.getTo().getY();

        double moveZ = event.getFrom().getZ();
        double moveToZ = event.getTo().getZ();

        // Test to see if the players location has changed.
        // This test does not include pitch and yaw. Head movements allowed.
        if (moveX == moveToX && moveY == moveToY && moveZ == moveToZ) return;

        timeLeft.remove(player);
        player.sendMessage(ChatColor.RED + "Teleport canceled because you moved.");
        CommonSounds.ACTION_FAILED.play(player);
    }

    @EventHandler
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(spawnLocation);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!spawnOnVoidDamage) return;

        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) return;
        if (!Bukkit.getWorlds().get(0).equals(entity.getWorld())) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;

        event.setCancelled(true); // Stop annoying damage noise.

        // Wait one tick and respawn the player.
        // Prevents "Player X moved to quickly" console spam.
        new BukkitRunnable() {
            public void run() {
                teleport((Player) entity, CommonSounds.ACTION_FAILED);
                cancel();
            }
        }.runTaskLater(SpigotCore.PLUGIN, 1L);
    }

    @EventHandler
    public void onPlayerTeleportSpawn(PlayerTeleportSpawnEvent event) {
        teleport(event.getPlayer());
    }

    @AllArgsConstructor
    private class CommandOptions {
        private final boolean commandEnabled;
        private final boolean countdownEnabled;
        private final int countdownTime;
        private final boolean cancelOnMove;
    }

    @CommandAlias("spawn|s")
    private class SpawnCommand extends FeatureOptionalCommand {

        @Override
        public void setupCommand(PaperCommandManager paperCommandManager) {

        }

        @Default
        public void onSpawn(Player player) {
            if (commandOptions.countdownEnabled) {
                timeLeft.put(player, commandOptions.countdownTime);
                if (commandOptions.cancelOnMove)
                    player.sendMessage(ChatColor.GRAY + "Starting countdown. Do not move!");
            } else {
                teleport(player, CommonSounds.ACTION_SUCCESS);
            }
        }

    }
}
