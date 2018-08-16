package com.forgestorm.spigotcore.features.optional.world.lantern;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.world.worldobject.AsyncWorldObjectTick;
import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewLantern implements FeatureOptional, LoadsConfig {

    private List<BaseWorldObject> lanternLightList;
    private WorldTime worldTime;
    private TimeOfDay currentTimeOfDay;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        worldTime = new WorldTime();
        worldTime.onEnable();
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        worldTime.onDisable();

        lanternLightList.stream()
                .filter(baseWorldObject -> baseWorldObject instanceof LanternLight)
                .forEach(baseWorldObject -> SpigotCore.PLUGIN.getWorldObjectManager().removeWorldObject(baseWorldObject));
    }

    @Override
    public void loadConfiguration() {
        lanternLightList = new ArrayList<>();

        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.LANTERNS.toString()));

        World world = Bukkit.getWorlds().get(0);
        ConfigurationSection section = config.getConfigurationSection("Locations");

        for (String i : section.getKeys(false)) {
            double x = config.getDouble("Locations." + i + ".x");
            double y = config.getDouble("Locations." + i + ".y");
            double z = config.getDouble("Locations." + i + ".z");

            Material lightOnMaterial = Material.valueOf(config.getString("Locations." + i + ".materialOn"));
            Material lightOffMaterial = Material.valueOf(config.getString("Locations." + i + ".materialOff"));

            Location location = new Location(world, x, y, z);
            LanternLight lanternLight = new LanternLight(location, lightOnMaterial, lightOffMaterial);

            lanternLightList.add(lanternLight);
            SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(location, lanternLight);
        }
    }

    @EventHandler
    public void onTimeOfDayChange(TimeOfDayChangeEvent event) {
        currentTimeOfDay = event.getTimeOfDay();

        if (validLanternSpawnTime()) return;

        // Despawn any active lights
        for (BaseWorldObject lanternLight : lanternLightList) {
            SpigotCore.PLUGIN.getWorldObjectManager().despawnWorldObject(lanternLight.getLocation());
        }
    }

    private boolean validLanternSpawnTime() {
        return currentTimeOfDay == TimeOfDay.DUSK || currentTimeOfDay == TimeOfDay.MIDNIGHT;
    }

    private class LanternLight extends BaseWorldObject implements AsyncWorldObjectTick {

        private final Material lightOnMaterial;
        private final Material lightOffMaterial;

        LanternLight(Location location, Material lightOnMaterial, Material lightOffMaterial) {
            super(location);
            this.lightOnMaterial = lightOnMaterial;
            this.lightOffMaterial = lightOffMaterial;
        }

        private void initLantern() {
            if (validLanternSpawnTime()) {
                location.getBlock().setType(lightOnMaterial);
            } else {
                location.getBlock().setType(lightOffMaterial);
            }
        }

        @Override
        public void spawnWorldObject() {
            initLantern();
        }

        @Override
        public void despawnWorldObject() {
            initLantern();
        }

        @Override
        public void onAsyncTick() {
            if (validLanternSpawnTime()) location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
        }
    }
}
