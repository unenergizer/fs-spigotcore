package com.forgestorm.spigotcore.world.loot;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.feature.FeatureOptional;
import com.forgestorm.spigotcore.util.display.Hologram;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import com.forgestorm.spigotcore.world.worldobject.BaseWorldObject;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DragonEggLoot extends BukkitRunnable implements FeatureOptional, Listener {

    private static final int MAX_COUNTDOWN = 5;
    private final File file = new File(FilePaths.DRAGON_EGG_LOOT.toString());
    private final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    private final List<Location> locations = new ArrayList<>();
    private final List<String> hologramText = new ArrayList<>();

    private int countdown = 0;
    private DragonEgg dragonEgg;
    private boolean isSpawned = false;

    @Override
    public void onEnable(boolean manualEnable) {
        loadLocations();

        hologramText.add("&5&lTP EGG GAME");
        hologramText.add(ChatColor.BOLD + "RIGHT-CLICK");

        // Register listeners
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        //Spawn the egg after server startup.
        this.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        // Remove the dragon egg from chunk manager
        SpigotCore.PLUGIN.getWorldObjectManager().removeWorldObject(dragonEgg.location);

        // Unregister Listeners
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

    @Override
    public void run() {
        if (countdown == 0 && !isSpawned) {
            isSpawned = true;
            initDragonEgg();
        } else {
            if (dragonEgg != null) {
                countdown = countdown - 1;
            }
        }
    }

    private void toggleEggClick(Player player, Location clickLocation) {

        //Make sure the player clicks the official dragon egg.
        //This will stop them from clicking an egg on a "build."
        if (clickLocation.equals(dragonEgg.location)) {
            //TODO: Play Animation
            for (double i = 0; i < 2; i++) {
                Firework fw = player.getWorld().spawn(clickLocation.subtract(0, -2, 0), Firework.class);
                FireworkMeta fm = fw.getFireworkMeta();
                fm.addEffect(FireworkEffect.builder()
                        .flicker(false)
                        .trail(false)
                        .with(Type.BURST)
                        .withColor(Color.PURPLE)
                        .withFade(Color.BLACK)
                        .build());
                fw.setFireworkMeta(fm);
            }

            //Play sound
            Bukkit.getWorlds().get(0).playSound(dragonEgg.location, Sound.ENTITY_SHULKER_BULLET_HURT, 1, .7f);

            // Remove the dragon egg from chunk manager
            SpigotCore.PLUGIN.getWorldObjectManager().removeWorldObject(dragonEgg.location);

            //Reward Text
            long exp = 100;
            int money = 100;

            //Send player text
            String rewardText = ChatColor.GREEN + "" + ChatColor.BOLD + "EXP: +" + exp
                    + ChatColor.YELLOW + "" + ChatColor.BOLD + " Money: +" + money;

            player.sendMessage("");
            player.sendMessage("");
            player.sendMessage("");
            player.sendMessage("");
            player.sendMessage(CenterChatText.centerChatMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "YOU FOUND THE HIDDEN ENDER EGG!!"));
            player.sendMessage("");
            player.sendMessage(CenterChatText.centerChatMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "THE EGG HAS DISAPPEARED!"));
            player.sendMessage(CenterChatText.centerChatMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "CAN YOU FIND IT AGAIN?"));
            player.sendMessage("");
            player.sendMessage(CenterChatText.centerChatMessage(rewardText));
            player.sendMessage("");
            player.sendMessage("");

            //TODO: Give Reward
//            PlayerRewards reward = new PlayerRewards(plugin, player);
//            reward.giveExp(100);
//            reward.giveMoney(100);

            //Play Sound
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, .8f);

            // Begin the countdown to spawn another egg
            countdown = MAX_COUNTDOWN;
            isSpawned = false;
        }
    }

    public void teleportToEgg(Player player) {
        player.teleport(dragonEgg.location);
        player.sendMessage(ChatColor.YELLOW + "Teleported you to egg location: " + dragonEgg.id);
    }

    private void initDragonEgg() {

        // Construct a new dragon egg
        int index = RandomChance.randomInt(0, locations.size() - 1);
        Location location = locations.get(index);
        dragonEgg = new DragonEgg(location, index);

        // Add the world object to the chunk manager
        SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(location, dragonEgg);
    }

    public void addLocation(Player player) {
        Location location = player.getLocation();
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
        int size = locations.size();

        config.set("Locations." + size + ".x", x);
        config.set("Locations." + size + ".y", y);
        config.set("Locations." + size + ".z", z);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        locations.add(new Location(location.getWorld(), x, y, z));
        player.sendMessage(ChatColor.YELLOW + "Set Dragon Egg at X: " + x + "  Y: " + y + "  Z: " + z + " Total Locations: " + size);
    }

    private void loadLocations() {
        World world = Bukkit.getWorlds().get(0);
        ConfigurationSection section = config.getConfigurationSection("Locations");
        Iterator<String> it = section.getKeys(false).iterator();
        String i;

        while (it.hasNext()) {
            i = it.next();

            double x = config.getDouble("Locations." + i + ".x");
            double y = config.getDouble("Locations." + i + ".y");
            double z = config.getDouble("Locations." + i + ".z");

            locations.add(new Location(world, x, y, z));
        }

        System.out.println("[DragonEggLoot] Loaded " + locations.size() + " egg locations.");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block.getType().equals(Material.DRAGON_EGG)
                || event.getAction().equals(Action.LEFT_CLICK_BLOCK) && block.getType().equals(Material.DRAGON_EGG)) {

            //Cancel the default egg respawn.
            event.setCancelled(true);

            //Toggle the egg click.
            toggleEggClick(player, event.getClickedBlock().getLocation());
        }
    }

    /**
     * Represents a DragonEggLoot object that can be spawned in the world.
     */
    class DragonEgg extends BaseWorldObject {

        /**
         * This is both the spawn location of the egg and counts as the entry from the "locations"
         * list.
         */
        private final Location location;

        /**
         * Mainly used to teleport an admin to this location. This ID is the exact one found in
         * the file configuration.
         */
        private final int id;

        /**
         * This hologram places text over the egg, telling the player what it is.
         */
        private Hologram hologram;

        /**
         * Their may sometimes be instances where the egg might spawn in mid air.  In order
         * to keep the egg from falling, we can place a barrier block underneath it.  However,
         * if we do so, we need to remove the barrier block if the egg is clicked or despawned.
         */
        private boolean barrierSpawned = false;

        DragonEgg(Location location, int id) {
            this.location = location;
            this.id = id;
        }

        @Override
        public void spawnWorldObject() {
            // Spawn barrier under the egg (if needed)
            Location underEgg = new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ());

            if (underEgg.getBlock().getType() == Material.AIR) {
                underEgg.getBlock().setType(Material.BARRIER);
                barrierSpawned = true;
            }

            // Spawn egg
            Block block = Bukkit.getWorlds().get(0).getBlockAt(location);
            block.setType(Material.DRAGON_EGG);

            //Play sound
            Bukkit.getWorlds().get(0).playSound(location, Sound.ENTITY_EGG_THROW, 1, .7f);

            //Spawn the hologram.
            double x = location.getX() + .5;
            double y = location.getY() + .3;
            double z = location.getZ() + .5;

            Location hologramLoc = new Location(location.getWorld(), x, y, z);

            hologram = new Hologram(hologramText, hologramLoc);
            hologram.spawnHologram();
        }

        @Override
        public void removeWorldObject() {
            //Despawn the block
            location.getBlock().setType(Material.AIR);

            // Remove barrier if it was spawned
            if (barrierSpawned) {
                new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ()).getBlock().setType(Material.AIR);
                barrierSpawned = false;
            }

            //Remove the hologram.
            hologram.despawnHologram();
        }
    }
}
