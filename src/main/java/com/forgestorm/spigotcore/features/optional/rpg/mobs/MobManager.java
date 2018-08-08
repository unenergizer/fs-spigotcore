package com.forgestorm.spigotcore.features.optional.rpg.mobs;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.ShutdownTask;
import com.forgestorm.spigotcore.util.text.Console;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MobManager implements FeatureOptional, ShutdownTask, LoadsConfig, Listener {

    private final Map<String, MobType> mobTypes = new HashMap<>();
    private final Map<Location, MobSpawner> mobSpawners = new HashMap<>();

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        addSpawnersToWorldObjectManager();

        Console.sendMessage("[MobManager] MobTypes Loaded: " + Integer.toString(mobTypes.size()));
        Console.sendMessage("[MobManager] Spawners Loaded: " + Integer.toString(mobSpawners.size()));
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        EntityDamageEvent.getHandlerList().unregister(this);

        removeSpawnersFromWorldObjectManager();
    }

    @Override
    public void onServerShutdown() {
        mobSpawners.clear();
        mobTypes.clear();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
//        if (event.getEntity() instanceof LivingEntity) {
//            NBTEntity nbti = new NBTEntity(event.getEntity());
//
//            System.out.println("[MobManager] " + event.getEventName() + " --------------------------------- START");
//            System.out.println(nbti);
//            System.out.println("[MobManager] " + event.getEventName() + " ----------------------------------- END");
//        }
    }

    @Override
    public void loadConfiguration() {
        loadMobTypes();
        loadMobSpawners();
    }

    private void loadMobTypes() {
        File file = new File(FilePaths.MOB_TYPES.toString());
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        Set<String> keys = config.getConfigurationSection("mobs").getKeys(false);

        for (String mob : keys) {
            String section = "mobs." + mob + ".";

            mobTypes.put(mob,
                    new MobType(
                            config.getString(section + "name"),
                            config.getInt(section + "health"),
                            EntityType.valueOf(config.getString(section + "type")),
                            config.getInt(section + "respawnTime")
                    ));
        }
    }

    private void loadMobSpawners() {
        File file = new File(FilePaths.MOB_SPAWNERS.toString());
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        Set<String> keys = config.getConfigurationSection("spawners").getKeys(false);

        for (String key : keys) {
            String section = "spawners." + key + ".";

            Location location = new Location(Bukkit.getWorld(config.getString(section + "world")),
                    config.getDouble(section + "x"),
                    config.getDouble(section + "y"),
                    config.getDouble(section + "z"));
            MobSpawner mobSpawner = new MobSpawner(location);

            // Grab Mobs
            for (String mobType : config.getStringList(section + ".mobs")) {
                mobSpawner.addMob(mobTypes.get(mobType));
            }

            mobSpawners.put(location.add(0, 1, 0), mobSpawner);
        }
    }

    private void addSpawnersToWorldObjectManager() {
        for (Map.Entry<Location, MobSpawner> entry : mobSpawners.entrySet()) {
            SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(entry.getKey(), entry.getValue());
        }
    }

    private void removeSpawnersFromWorldObjectManager() {
        for (Location location : mobSpawners.keySet()) {
            SpigotCore.PLUGIN.getWorldObjectManager().removeWorldObject(location);
        }
    }
}
