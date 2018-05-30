package com.forgestorm.spigotcore.features.optional.world;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.LoadsConfig;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple features to control where a player spawns in the world.
 * If the player fall's into the void, we can teleport them back to the main spawn.
 */
public class ServerSpawn implements FeatureOptional, LoadsConfig, Listener, CommandExecutor {

    private boolean spawnOnVoidDamage;
    private CommandOptions commandOptions;
    private Location spawnLocation;

    private Map<Player, Integer> timeLeft;
    private BukkitRunnable spawnTimer;

    private boolean featureEnabled = false;

    @Override
    public void onEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        Bukkit.getWorlds().get(0).setSpawnLocation(
                (int) spawnLocation.getX(),
                (int) spawnLocation.getY(),
                spawnLocation.getBlockZ()
        );

        if (!commandOptions.commandEnabled) return;
        SpigotCore.PLUGIN.getCommand("spawn").setExecutor(this);

        if (!commandOptions.countdownEnabled) return;
        timeLeft = new ConcurrentHashMap<>();
        spawnTimer = new BukkitRunnable() {
            @Override
            public void run() {
                doCountdown();
            }
        };
        spawnTimer.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
        featureEnabled = true;
    }

    @Override
    public void onDisable(boolean manualDisable) {
        featureEnabled = false;
        if (commandOptions.commandEnabled && commandOptions.countdownEnabled) {
            spawnTimer.cancel();
            timeLeft.clear();
        }

        PlayerMoveEvent.getHandlerList().unregister(this);
        PlayerSpawnLocationEvent.getHandlerList().unregister(this);
        EntityDamageEvent.getHandlerList().unregister(this);
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
     * @param sound The sound to play when a player is teleported.
     */
    private void teleport(Player player, CommonSounds sound) {
        player.teleport(spawnLocation);
        player.setFallDistance(0F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 100));
        sound.play(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!featureEnabled) return false;
        if (!(sender instanceof Player)) return false;
        Player player = ((Player) sender).getPlayer();

        if (commandOptions.countdownEnabled) timeLeft.put(player, commandOptions.countdownTime);
        else teleport(player, CommonSounds.ACTION_SUCCESS);
        if (commandOptions.countdownEnabled && commandOptions.cancelOnMove)
            player.sendMessage(ChatColor.GRAY + "Starting countdown. Do not move!");
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

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

    @AllArgsConstructor
    private class CommandOptions {
        private final boolean commandEnabled;
        private final boolean countdownEnabled;
        private final int countdownTime;
        private final boolean cancelOnMove;
    }
}
