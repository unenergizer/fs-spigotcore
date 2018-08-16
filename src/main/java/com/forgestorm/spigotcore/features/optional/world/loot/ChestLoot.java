package com.forgestorm.spigotcore.features.optional.world.loot;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.events.WorldObjectSpawnEvent;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.world.worldobject.AsyncWorldObjectTick;
import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import com.forgestorm.spigotcore.features.required.world.worldobject.CooldownWorldObject;
import com.forgestorm.spigotcore.util.text.Console;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChestLoot implements FeatureOptional, LoadsConfig {

    private static final int MAX_CHEST_PER_PLAYER_ONLINE = 2;
    private static final int MAX_CHEST_TIMEOUT = 30;
    private static final Material CHEST_TYPE = Material.TRAPPED_CHEST;

    private List<BaseWorldObject> chestMap;

    private int currentChestsSpawned = 0;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        chestMap.stream()
                .filter(baseWorldObject -> baseWorldObject instanceof Chest)
                .forEach(baseWorldObject -> SpigotCore.PLUGIN.getWorldObjectManager().removeWorldObject(baseWorldObject));

        chestMap.clear();
        chestMap = null;

        // Reset the current chest spawned!
        currentChestsSpawned = 0;
    }

    /**
     * Loads all the possible chest locations from the configuration.
     */
    @Override
    public void loadConfiguration() {
        chestMap = new ArrayList<>();

        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.CHEST_LOOT.toString()));

        World world = Bukkit.getWorlds().get(0);
        ConfigurationSection section = config.getConfigurationSection("Locations");

        for (String i : section.getKeys(false)) {
            double x = config.getDouble("Locations." + i + ".x");
            double y = config.getDouble("Locations." + i + ".y");
            double z = config.getDouble("Locations." + i + ".z");

            Location location = new Location(world, x, y, z);
            Chest chest = new Chest(location);

            chestMap.add(chest);
            SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(location, chest);
        }

        //System.out.println("[ChestLoot] Loaded " + chestList.size() + " loot chests locations.");
    }

    @EventHandler
    public void onWorldObjectSpawn(WorldObjectSpawnEvent event) {
        // Cancel spawning chests if we have reached max spawns.
        if (event.getBaseWorldObject() instanceof Chest)
            event.setCancelled(currentChestsSpawned >= MAX_CHEST_PER_PLAYER_ONLINE);
    }

    /**
     * Generates chest loot for the opened chest.
     *
     * @return Items to be given by said chest.
     */
    private List<ItemStack> generateChestLoot() {
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Material.TNT));
        items.add(new ItemStack(Material.COBBLESTONE));
        return items;
    }

    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getClickedBlock().getType().equals(CHEST_TYPE)) return;
        toggleChestLootActive(event.getPlayer(), event.getClickedBlock().getLocation(), false);
    }

    @EventHandler
    public void onChestBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != CHEST_TYPE) return;

        event.setCancelled(true);
        toggleChestLootActive(event.getPlayer(), event.getBlock().getLocation(), true);
    }

    /**
     * Called when a loot chest is clicked.
     *
     * @param player     The player who clicked the chest.
     * @param location   The location that the click happened.
     * @param blockBreak True if the chest was broken, false if it was opened.
     */
    private void toggleChestLootActive(Player player, Location location, boolean blockBreak) {

        // Set the chest to air.
        location.getBlock().setType(Material.AIR);
        player.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 0.5F, 1.2F);

        // Prepare chest for timeout countdown
        SpigotCore.PLUGIN.getWorldObjectManager().toggleCooldown(location);

        // Give the player chest loot
        List<ItemStack> items = generateChestLoot();

        if (blockBreak) {
            // DragonEgg was broken, lets spawn items in the open world
            for (ItemStack itemStack : items) {
                location.getWorld().dropItem(location, itemStack);
            }
        } else {
            // DragonEgg was opened, lets show the player an inventory of items
            int slot = 0;
            Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Loot DragonEgg");
            for (ItemStack itemStack : items) {
                inventory.setItem(slot++, itemStack);
            }
            player.openInventory(inventory);
        }
    }

    /***
     * This class contains data that represents a chest world object.
     * BaseWorldObject is registerCommand that our WorldSettings class can spawn in our world.
     */
    private class Chest extends CooldownWorldObject implements AsyncWorldObjectTick {

        Chest(Location location) {
            super(location, ChestLoot.MAX_CHEST_TIMEOUT);
        }

        @Override
        public void spawnWorldObject() {
            Console.sendMessage(ChatColor.LIGHT_PURPLE + "Spawning loot chest.");

            location.getBlock().setType(CHEST_TYPE);
            currentChestsSpawned = currentChestsSpawned + 1;
        }

        @Override
        public void despawnWorldObject() {
            Console.sendMessage(ChatColor.DARK_PURPLE + "Removing loot chest.");
            location.getBlock().setType(Material.AIR);
            currentChestsSpawned = currentChestsSpawned - 1;
        }

        @Override
        public void onAsyncTick() {
            if (!isOnCooldown()) location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
        }
    }
}
