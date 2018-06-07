package com.forgestorm.spigotcore.features.optional.world.loot;

import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChestLoot extends BukkitRunnable implements FeatureOptional, Listener {

    private static final int MAX_CHEST_PER_PLAYER_ONLINE = 2;
    private static final int MAX_CHEST_TIMEOUT = 30;
    private static final Material CHEST_TYPE = Material.TRAPPED_CHEST;
    private final List<Chest> chestList = new ArrayList<>();

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        loadLocations();

        this.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        this.cancel(); // Cancel BukkitRunnable
        // Lets remove all the loot chests
        int chestsRemoved = 0;

        for (Chest chest : chestList) {
            Block block = chest.location.getBlock();
            if (!chest.location.getChunk().isLoaded()) return;
            if (block.getType().equals(CHEST_TYPE)) chest.location.getBlock().setType(Material.AIR);
            chestsRemoved++;
        }

        if (chestsRemoved > 0)
            System.out.println("[ChestLoot] Did chunk managers job. Removed " + chestsRemoved + " chests.");

        BlockBreakEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);

        this.cancel();
        chestList.clear();
    }

    /**
     * Called when a loot chest is clicked.
     *
     * @param player     The player who clicked the chest.
     * @param location   The location that the click happened.
     * @param blockBreak True if the chest was broken, false if it was opened.
     */
    private void toggleChestLootActive(Player player, Location location, boolean blockBreak) {
        boolean validLocation = false;
        Chest validChest = null;

        for (Chest chest : chestList) {
            if (location.equals(chest.location)) {
                validLocation = true;
                validChest = chest;
            }
        }

        // Make sure we have a valid chest!
        if (!validLocation) return;

        // Set the chest to air.
        location.getBlock().setType(Material.AIR);
        player.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 0.5F, 1.2F);


        // In some rare cases, a chest might not have been removed. So before we give items, lets
        // make sure this "isReady = true."
        if (!validChest.isReady) {
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "This loot chest didn't contain any items.");
            return;
        }

        // Prepare chest for timeout countdown
        validChest.isReady = false; // Chest is no longer spawned
        validChest.isTimedOut = true; // Countdown for possible respawn should happen

        // Remove the world object from the chunk manager
        SpigotCore.PLUGIN.getWorldObjectManager().removeWorldObject(location);

        // Give the player chest loot
        List<ItemStack> items = generateChestLoot();

        if (blockBreak) {
            // Chest was broken, lets spawn items in the open world
            for (ItemStack itemStack : items) {
                location.getWorld().dropItem(location, itemStack);
            }
        } else {
            // Chest was opened, lets show the player an inventory of items
            int slot = 0;
            Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Loot Chest");
            for (ItemStack itemStack : items) {
                inventory.setItem(slot++, itemStack);
            }
            player.openInventory(inventory);
        }
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

    //TODO: Add chest location via command
//    public void addChestLocation(Player player, Location location) {
//        if (allChestLocations.contains(location)) return;
//        allChestLocations.add(location);
//
//        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
//        int size = allChestLocations.size();
//
//        config.set("Locations." + size + ".x", x);
//        config.set("Locations." + size + ".y", y);
//        config.set("Locations." + size + ".z", z);
//        try {
//            config.save(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        player.sendComponentMessage(ChatColor.YELLOW + "ChestLoot added at X: " + x + "  Y: " + y + "  Z: " + z +
//                " Total Locations: " + size);
//    }

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

            chestList.add(new Chest(new Location(world, x, y, z)));
        }

        System.out.println("[ChestLoot] Loaded " + chestList.size() + " loot chests locations.");
    }

    /**
     * A loot chest is ready to be spawned.
     */
    private void initNewLootChest() {
        int maxLocations = chestList.size() - 1;
        int randomChest = RandomChance.randomInt(0, maxLocations);
        Chest chest = chestList.get(randomChest);

        // The this chest has already spawned, find a different chest.
        if (chest.isReady) return;

        // Make sure this chest isn't timed out.
        if (chest.isTimedOut) return;

        // Lets make sure a block isn't already in this spawn location.
        Material material = chest.location.getBlock().getType();
        if (material != Material.AIR && material != CHEST_TYPE) {
            Console.sendMessage("[ChestLoot] Skipped chest spawn. " + material.toString() + " was in the way!");
            return;
        }

        // Now, mark chest as spawned
        chest.isReady = true;

        // Add the chest to the WorldSettings
        SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(chest.location, chest);
    }

    @Override
    public void run() {

        int chestsNeeded = MAX_CHEST_PER_PLAYER_ONLINE * Bukkit.getOnlinePlayers().size();
        int spawnedChests = 0;

        for (Chest chest : chestList) {

            if (chest.isSpawned()) spawnedChests++;

            // Adjust time left for locked out chest locations
            if (chest.isTimedOut) {
                chest.timeLeft = chest.timeLeft - 1;
                if (chest.timeLeft <= 0) {
                    chest.timeLeft = MAX_CHEST_TIMEOUT;
                    chest.isTimedOut = false;
                    //System.out.println("[ChestLoot] One loot chest is ready for respawn.");
                }
            }

            // Show chest particle effects.
            if (chest.showParticles) chest.location.getWorld().playEffect(chest.location, Effect.MOBSPAWNER_FLAMES, 1);
        }

        // If not enough loot chests exist in the world, lets spawn one.
        if (chestsNeeded > spawnedChests) {
            initNewLootChest();
        }

        //System.out.println("[ChestLoot] Chests needed: " + chestsNeeded + ", Spawned chests: " + spawnedChests);
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

    private int spawnedChests = 0;

    /***
     * This class contains data that represents a chest world object.
     * BaseWorldObject is registerCommand that our WorldSettings class can spawn in our world.
     */
    private class Chest extends BaseWorldObject {

        /**
         * The location of this chest.
         */
        private final Location location;

        /**
         * If the chest is being timed out, this will be the counter until next possible respawn.
         */
        private int timeLeft = MAX_CHEST_TIMEOUT;

        /**
         * If the chest is timed out, it can not respawn. Instead a countdown will happen that
         * involves the timeLeft var.
         */
        private boolean isTimedOut = false;

        /**
         * If the chest is ready to be looted in the open world, we mark it as spawned.
         * Otherwise, this should remain false to allow the chest to be spawned at some point.
         */
        private boolean isReady = false;

        /**
         * Added showParticles so we know when to spawn particle effects around chests.
         */
        private boolean showParticles = false;

        private Chest(Location location) {
            this.location = location;
        }

        @Override
        public void spawnWorldObject() {
            Console.sendMessage(ChatColor.LIGHT_PURPLE + "Spawning loot chest.");
            location.getBlock().setType(CHEST_TYPE);
            showParticles = true;
        }

        @Override
        public void despawnWorldObject() {
            Console.sendMessage(ChatColor.DARK_PURPLE + "Removing loot chest.");
            location.getBlock().setType(Material.AIR);
            showParticles = false;
        }
    }
}
