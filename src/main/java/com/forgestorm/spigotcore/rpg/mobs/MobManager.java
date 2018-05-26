package com.forgestorm.spigotcore.rpg.mobs;

import com.forgestorm.spigotcore.feature.FeatureOptional;
import com.forgestorm.spigotcore.feature.LoadsConfig;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.util.text.Console;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MobManager implements FeatureOptional, LoadsConfig {

    private final Map<String, MobType> mobTypes = new HashMap<>();
    private final Map<Location, MobSpawner> mobSpawners = new HashMap<>();

    @Override
    public void onEnable() {
        addSpawnersToWorldObjectManager();

        Console.sendMessage("[MobManager] MobTypes Loaded: " + Integer.toString(mobTypes.size()));
        Console.sendMessage("[MobManager] Spawners Loaded: " + Integer.toString(mobSpawners.size()));
    }

    @Override
    public void onDisable() {
        mobSpawners.clear();
        mobTypes.clear();
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

            mobSpawners.put(location, mobSpawner);
        }
    }

    private void addSpawnersToWorldObjectManager() {
        for (Map.Entry<Location, MobSpawner> entry : mobSpawners.entrySet()) {
            SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(entry.getKey(), entry.getValue());
        }
    }
}
