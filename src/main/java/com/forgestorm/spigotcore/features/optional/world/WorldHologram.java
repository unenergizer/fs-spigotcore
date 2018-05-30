package com.forgestorm.spigotcore.features.optional.world;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureShutdown;
import com.forgestorm.spigotcore.util.display.Hologram;
import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldHologram implements FeatureOptional, FeatureShutdown, LoadsConfig {

    private final List<HologramWorldObject> hologramWorldObjectList = new ArrayList<>();

    @Override
    public void onEnable(boolean manualEnable) {
        hologramWorldObjectList.forEach(worldObject -> SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(worldObject.getLocation(), worldObject));
    }

    @Override
    public void onDisable(boolean manualDisable) {
        hologramWorldObjectList.forEach(HologramWorldObject::removeWorldObject);
    }

    @Override
    public void onServerShutdown() {
        hologramWorldObjectList.forEach(HologramWorldObject::remove);
        hologramWorldObjectList.clear();
    }

    @Override
    public void loadConfiguration() {
        final String prefix = "WorldHolograms";
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.WORLD_HOLOGRAM.toString()));

        for (String entry : config.getConfigurationSection(prefix).getKeys(false)) {

            String worldName = config.getString(prefix + "." + entry + ".worldName");
            double x = config.getDouble(prefix + "." + entry + ".x");
            double y = config.getDouble(prefix + "." + entry + ".y");
            double z = config.getDouble(prefix + "." + entry + ".z");
            List<String> text = config.getStringList(prefix + "." + entry + ".text");
            boolean centerOnBlock = config.getBoolean(prefix + "." + entry + ".centerOnBlock");

            Location location = new Location(Bukkit.getWorld(worldName), x, y, z);

            if (centerOnBlock) {
                location = location.add(.5, .65, .5);
            } else {
                location = location.add(0, .65, 0);
            }

            hologramWorldObjectList.add(new HologramWorldObject(new Hologram(text, location)));
        }
    }

    @AllArgsConstructor
    class HologramWorldObject extends BaseWorldObject {

        private final Hologram hologram;

        public Location getLocation() {
            return hologram.getLocation();
        }

        public void remove() {
            removeWorldObject();
            hologram.remove();
        }

        @Override
        public void spawnWorldObject() {
            hologram.spawnHologram();
        }

        @Override
        public void removeWorldObject() {
            hologram.despawnHologram();
        }
    }
}
