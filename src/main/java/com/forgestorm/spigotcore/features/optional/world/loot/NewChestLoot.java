package com.forgestorm.spigotcore.features.optional.world.loot;

import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.features.required.world.worldobject.CooldownWorldObject;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewChestLoot implements FeatureOptional, Listener {

    private static final int MAX_CHEST_PER_PLAYER_ONLINE = 2;
    private static final int MAX_CHEST_TIMEOUT = 30;
    private static final Material CHEST_TYPE = Material.TRAPPED_CHEST;

    private int currentChestsSpawned = 0;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        loadLocations();
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {

    }

    /**
     * Generates chest loot for the opened chest.
     *
     * @return Items to be given by said chest.
     */
    private List<ItemStack> generateChestLoot() {
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Material.DIRT));
        items.add(new ItemStack(Material.COBBLESTONE));
        return items;
    }

    /**
     * Loads all the possible chest locations from the configuration.
     */
    private void loadLocations() {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.CHEST_LOOT.toString()));

        World world = Bukkit.getWorlds().get(0);
        ConfigurationSection section = config.getConfigurationSection("Locations");
        Iterator<String> it = section.getKeys(false).iterator();

        while (it.hasNext()) {
            String i = it.next();

            double x = config.getDouble("Locations." + i + ".x");
            double y = config.getDouble("Locations." + i + ".y");
            double z = config.getDouble("Locations." + i + ".z");

            new NewChestLoot.Chest(new Location(world, x, y, z));
        }

        //System.out.println("[ChestLoot] Loaded " + chestList.size() + " loot chests locations.");
    }

    /***
     * This class contains data that represents a chest world object.
     * BaseWorldObject is registerCommand that our WorldSettings class can spawn in our world.
     */
    private class Chest extends CooldownWorldObject {

        /**
         * The location of this chest.
         */
        private final Location location;

        private Chest(Location location) {
            super(MAX_CHEST_TIMEOUT);
            this.location = location;
        }

        @Override
        public void spawnWorldObject() {
            Console.sendMessage(ChatColor.LIGHT_PURPLE + "Spawning loot chest.");
            location.getBlock().setType(CHEST_TYPE);
            currentChestsSpawned = currentChestsSpawned + 1;

            Console.sendMessage("Number of chests: " + currentChestsSpawned);
        }

        @Override
        public void despawnWorldObject() {
            Console.sendMessage(ChatColor.DARK_PURPLE + "Removing loot chest.");
            location.getBlock().setType(Material.AIR);
            currentChestsSpawned = currentChestsSpawned - 1;

            Console.sendMessage("Number of chests: " + currentChestsSpawned);
        }
    }
}
