package com.forgestorm.spigotcore.world;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.feature.FeatureOptional;
import com.forgestorm.spigotcore.feature.LoadsConfig;
import com.forgestorm.spigotcore.util.text.Console;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Skull;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * Small class to set some custom world settings on server start. To prevent
 * recreating the wheel, we set some "world game rules" instead of writing
 * custom bukkit listeners to produce the exact same effect.
 */
public class WorldSettings implements FeatureOptional, LoadsConfig, Listener {

    private int timeOfDay;
    private boolean pvp;
    private boolean pve;
    private boolean pvm;
    private boolean clearEntitiesOnServerStart;
    private boolean autoSaveWorld;
    private boolean preventNewChunks;
    private boolean allowPortal;
    private boolean doDaylightCycle;
    private boolean doWeatherCycle;

    @Override
    public void onEnable(boolean manualEnable) {
        Bukkit.getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        initWorld(manualEnable);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        BlockIgniteEvent.getHandlerList().unregister(this);
        ChunkLoadEvent.getHandlerList().unregister(this);
        EntityChangeBlockEvent.getHandlerList().unregister(this);
        EntityCombustEvent.getHandlerList().unregister(this);
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
        EntityDamageEvent.getHandlerList().unregister(this);
        PlayerTeleportEvent.getHandlerList().unregister(this);
    }

    @Override
    public void loadConfiguration() {
        String path = "Settings.";
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.WORLD_SETTINGS.toString()));

        // Custom settings first
        timeOfDay = config.getInt(path + "timeOfDay");
        pvp = config.getBoolean(path + "pvp"); // Player vs Player
        pve = config.getBoolean(path + "pve"); // Player vs Environment
        pvm = config.getBoolean(path + "pvm"); // Player vs Monster
        clearEntitiesOnServerStart = config.getBoolean(path + "clearEntitiesOnServerStart");
        autoSaveWorld = config.getBoolean(path + "autoSaveWorld");
        preventNewChunks = config.getBoolean(path + "preventNewChunks");
        allowPortal = config.getBoolean(path + "allowPortal");

        // Set GameRules last. Config path updated here.
        path = path + "GameRules."; // update path!
        doDaylightCycle = config.getBoolean(path + "doDaylightCycle");
        doWeatherCycle = config.getBoolean(path + "doWeatherCycle");
    }

    /**
     * Init the world and configure it based off loaded settings.
     */
    private void initWorld(boolean manualEnable) {
        // Lets do some initial world cleanup
        Bukkit.setSpawnRadius(0);

        // Mandatory world settings
        World world = Bukkit.getWorlds().get(0);
        world.setSpawnFlags(false, false);
        world.setGameRuleValue("doMobSpawning", "false");

        // Optional world settings
        world.setTime(timeOfDay);
        world.setAutoSave(autoSaveWorld);
        world.setGameRuleValue("doDaylightCycle", Boolean.toString(doDaylightCycle));
        world.setGameRuleValue("doWeatherCycle", Boolean.toString(doWeatherCycle));

        // If we have manually enabled this feature, do not clear entities!
        // This would remove any entities spawned by other features.
        if (manualEnable) Console.sendMessage("&c[WorldSettings] Manually enabled. Not clearing entities!");
        if (clearEntitiesOnServerStart && !manualEnable) clearEntitiesOnServerStart();
    }

    /**
     * Removes any entities that exist on the server after the world is done loading up.
     * We also prevent removing any Player objects encase this is ran on a server reload.
     * Removing the player object creates really strange and undesirable bugs.
     */
    private void clearEntitiesOnServerStart() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    int count = 0;
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof ItemFrame) continue;
                        if (entity instanceof Painting) continue;
                        if (entity instanceof Skull) continue;
                        if (entity instanceof Player) continue;
                        entity.remove();
                        count++;
                    }
                    Console.sendMessage("[WorldSettings] Cleared " + count + " entities in " + world.getName());
                }
            }
        }.runTaskLater(SpigotCore.PLUGIN, 0);
    }

    /**
     * Stop fire from being ignited, unless its a player.
     *
     * @param event Block ignite event.
     */
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() == null) event.setCancelled(true);
    }

    /**
     * Tests to see if new chunks are being created. If they are not
     * allowed, we remove them. If they are allowed we ignore it.
     *
     * @param event The ChunkLoadEvent provided by Bukkit.
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk() && preventNewChunks) event.getChunk().unload(false);
    }

    /**
     * Prevent entities from changing blockRegen. For instance, this would
     * prevent Endermen from moving/removing blockRegen.
     *
     * @param event Entity change block event.
     */
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        event.setCancelled(true);
    }

    /**
     * Prevent living entities from catching on fire.
     *
     * @param event Entity combust event.
     */
    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if ((event.getEntity() instanceof Player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if ((event.getDamager() instanceof Player)) {
            event.setCancelled(!pvp);
        } else {
            event.setCancelled(!pvm);
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        event.setCancelled(!pve);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        switch (event.getCause()) {

            case ENDER_PEARL:
                break;
            case COMMAND:
                break;
            case PLUGIN:
                break;
            case NETHER_PORTAL:
            case END_PORTAL:
            case END_GATEWAY:
                event.setCancelled(!allowPortal);
                break;
            case SPECTATE:
                break;
            case CHORUS_FRUIT:
                break;
            case UNKNOWN:
                break;
        }
    }
}
