package com.forgestorm.spigotcore.features.optional.world.lantern;

import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.util.text.Console;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Lantern implements FeatureOptional, LoadsConfig, Listener {

    private final static String FILE_PATH = FilePaths.LANTERNS.toString();
    private final World world = Bukkit.getWorlds().get(0);
    private final Map<Location, LanternInfo> lanterns = new HashMap<>();
    private final File file = new File(FILE_PATH);
    private final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    private TimeOfDay timeOfDay;
    private final static String prefix = "Lanterns";
    private final WorldTimer worldTimer = new WorldTimer();

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        worldTimer.onEnable();
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        worldTimer.onDisable();

        if (timeOfDay == TimeOfDay.DUSK) putOutLanterns();

        TimeOfDayEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onTimeChange(TimeOfDayEvent event) {
        TimeOfDay times = event.getTimeOfDay();

        // Light all lantern
        if (times == TimeOfDay.DUSK) {
            timeOfDay = TimeOfDay.DUSK;
            lightLanterns();
        }

        // Turn out all lanterns
        if (times == TimeOfDay.DAWN) {
            timeOfDay = TimeOfDay.DAWN;
            putOutLanterns();
        }
    }

    /**
     * This will light all the lanterns in the world.
     */
    private void lightLanterns() {
        for (Map.Entry<Location, LanternInfo> entry : lanterns.entrySet()) {
            world.getBlockAt(entry.getKey()).setType(entry.getValue().getLanternOnMaterial());
        }
    }

    /**
     * This will put out all the lanterns in the world.
     */
    private void putOutLanterns() {
        for (Map.Entry<Location, LanternInfo> entry : lanterns.entrySet()) {
            world.getBlockAt(entry.getKey()).setType(entry.getValue().getLanternOffMaterial());
        }
    }

    public void addLantern(Location location, String onMaterial, String offMaterial) {
        String entry = Integer.toString(lanterns.size());
        config.set(prefix + "." + entry + ".x", location.getX());
        config.set(prefix + "." + entry + ".y", location.getY());
        config.set(prefix + "." + entry + ".z", location.getZ());
        config.set(prefix + "." + entry + ".materialOn", onMaterial.toUpperCase());
        config.set(prefix + "." + entry + ".materialOff", offMaterial.toUpperCase());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        Console.sendMessage("[Lantern] " + x + ", " + y + ", " + z + ", " + onMaterial + ", " + offMaterial);

        Material on = Material.getMaterial(onMaterial.toUpperCase());
        Material off = Material.getMaterial(offMaterial.toUpperCase());

        // Add location to the map
        lanterns.put(location, new LanternInfo(x, y, z, on, off));

        // Set confirmation block
        world.getBlockAt(location).setType(on);
    }

    /**
     * This will load all lanterns from the configuration file.
     */
    @Override
    public void loadConfiguration() {
        int totalLanterns = 0;
        for (String entry : config.getConfigurationSection(prefix).getKeys(false)) {

            double x = config.getDouble(prefix + "." + entry + ".x");
            double y = config.getDouble(prefix + "." + entry + ".y");
            double z = config.getDouble(prefix + "." + entry + ".z");

            String onType = config.getString(prefix + "." + entry + ".materialOn");
            String offType = config.getString(prefix + "." + entry + ".materialOff");

            //System.out.println("[LANTERNS]" + x + ", " + y + ", " + z + ", " + onType + ", " + offType);

            Material onMaterial = Material.getMaterial(onType.toUpperCase());
            Material offMaterial = Material.getMaterial(offType.toUpperCase());

            // Create variables for the lanterns Map.
            Location location = new Location(world, x, y, z);
            LanternInfo lanternInfo = new LanternInfo(x, y, z, onMaterial, offMaterial);

            // Save for later
            lanterns.put(location, lanternInfo);
            totalLanterns++;
        }
        Console.sendMessage("[Lantern] Loaded " + totalLanterns + " lanterns.");
    }

    /**
     * This is the LanternInfo class that holds data pertaining to lanterns.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    private class LanternInfo {
        private final double x;
        private final double y;
        private final double z;
        private final Material lanternOnMaterial, lanternOffMaterial;
    }
}

