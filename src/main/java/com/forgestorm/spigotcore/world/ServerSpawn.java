package com.forgestorm.spigotcore.world;

import com.forgestorm.spigotcore.FeatureOptional;
import com.forgestorm.spigotcore.LoadsConfig;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.constants.FilePaths;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * Simple feature to control where a player spawns in the world.
 * If the player fall's into the void, we can teleport them back to the main spawn.
 */
public class ServerSpawn implements FeatureOptional, LoadsConfig, Listener {

    private Location spawnLocation;
    private boolean spawnOnVoidDamage;

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onDisable() {
        PlayerJoinEvent.getHandlerList().unregister(this);
        EntityDamageEvent.getHandlerList().unregister(this);
    }

    @Override
    public void loadConfiguration() {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.SERVER_SPAWN.toString()));
        spawnLocation = new Location(Bukkit.getWorlds().get(0),
                config.getDouble("Settings.spawn.x"),
                config.getDouble("Settings.spawn.y"),
                config.getDouble("Settings.spawn.z"),
                (float) config.getDouble("Settings.spawn.yaw"),
                (float) config.getDouble("Settings.spawn.pitch"));

        // Spawn above ground
        spawnLocation.add(0, 1.5, 0);

        spawnOnVoidDamage = config.getBoolean("Settings.spawnOnVoidDamage");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(spawnLocation);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!spawnOnVoidDamage) return;

        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) return;
        if (!Bukkit.getWorlds().get(0).equals(entity.getWorld())) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID) return;

        event.setCancelled(true); // Stop annoying damage noise.
        Player player = (Player) entity;

        // Wait one tick and respawn the player.
        new BukkitRunnable() {
            public void run() {

                player.teleport(spawnLocation);
                player.setFallDistance(0F);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 4 * 20, 100));
                CommonSounds.ACTION_FAILED.play(player);

                cancel();
            }
        }.runTaskLater(SpigotCore.PLUGIN, 1L);
    }
}
