package com.forgestorm.spigotcore.features.optional.world.loot;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.events.WorldObjectAddEvent;
import com.forgestorm.spigotcore.features.events.WorldObjectSpawnEvent;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.world.worldobject.AsyncWorldObjectTick;
import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import com.forgestorm.spigotcore.features.required.world.worldobject.CooldownWorldObject;
import com.forgestorm.spigotcore.util.display.Hologram;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import com.forgestorm.spigotcore.util.text.Console;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewDragonEggLoot implements FeatureOptional, LoadsConfig, Listener {

    private static final int MAX_EGG_TIMEOUT = 30;

    private final List<String> hologramText = new ArrayList<>();
    private List<BaseWorldObject> dragonEggMap;

    private int currentEggsSpawned = 0;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        hologramText.add("&5&lTP EGG GAME");
        hologramText.add(ChatColor.BOLD + "RIGHT-CLICK");
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        WorldObjectAddEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);

        dragonEggMap.stream()
                .filter(baseWorldObject -> baseWorldObject instanceof DragonEgg)
                .forEach(baseWorldObject -> SpigotCore.PLUGIN.getWorldObjectManager().removeWorldObject(baseWorldObject));

        dragonEggMap.clear();
        dragonEggMap = null;

        // Reset the current egg spawned!
        currentEggsSpawned = 0;
    }

    /**
     * Loads all the possible egg locations from the configuration.
     */
    @Override
    public void loadConfiguration() {
        dragonEggMap = new ArrayList<>();

        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.DRAGON_EGG_LOOT.toString()));

        World world = Bukkit.getWorlds().get(0);
        ConfigurationSection section = config.getConfigurationSection("Locations");

        for (String i : section.getKeys(false)) {
            double x = config.getDouble("Locations." + i + ".x");
            double y = config.getDouble("Locations." + i + ".y");
            double z = config.getDouble("Locations." + i + ".z");

            Location location = new Location(world, x, y, z);
            DragonEgg dragonEgg = new DragonEgg(location, MAX_EGG_TIMEOUT);

            dragonEggMap.add(dragonEgg);
            SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(location, dragonEgg);
        }
    }

    @EventHandler
    public void onWorldObjectSpawn(WorldObjectSpawnEvent event) {
        // Cancel spawning eggs if we have reached max spawns.
        if (event.getBaseWorldObject() instanceof DragonEgg) event.setCancelled(currentEggsSpawned >= 1);
    }

    @EventHandler
    public void onEggInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getClickedBlock().getType().equals(Material.DRAGON_EGG)) return;
        toggleEggLootActive(event.getPlayer(), event.getClickedBlock().getLocation());
    }

    /**
     * Called when a loot egg is clicked.
     *
     * @param player   The player who clicked the egg.
     * @param location The location that the click happened.
     */
    private void toggleEggLootActive(Player player, Location location) {
        // Set the dragon egg to air.
        location.getBlock().setType(Material.AIR);

        // Prepare egg for timeout countdown
        SpigotCore.PLUGIN.getWorldObjectManager().toggleCooldown(location);

        for (double i = 0; i < 2; i++) {
            Firework fw = player.getWorld().spawn(location.subtract(0, -2, 0), Firework.class);
            FireworkMeta fm = fw.getFireworkMeta();
            fm.addEffect(FireworkEffect.builder()
                    .flicker(false)
                    .trail(false)
                    .with(FireworkEffect.Type.BURST)
                    .withColor(Color.PURPLE)
                    .withFade(Color.BLACK)
                    .build());
            fw.setFireworkMeta(fm);
        }

        //Play sound
        Bukkit.getWorlds().get(0).playSound(location, Sound.ENTITY_SHULKER_BULLET_HURT, 1, .7f);

        //Reward Text
        long exp = 100;
        int money = 100;

        //Send player text
        String rewardText = "&a&lEXP: &r&m+" + exp + "&r &e&lMoney: &r&m+" + money;

        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage(CenterChatText.centerChatMessage("&d&lYOU FOUND THE HIDDEN ENDER EGG!!"));
        player.sendMessage("");
        player.sendMessage(CenterChatText.centerChatMessage("&lTHE EGG HAS DISAPPEARED!"));
        player.sendMessage(CenterChatText.centerChatMessage("&7&lCAN YOU FIND IT AGAIN?"));
        player.sendMessage("");
        player.sendMessage(CenterChatText.centerChatMessage(rewardText));
        player.sendMessage(CenterChatText.centerChatMessage("&cREWARDS NOT IMPLEMENTED YET. COMING SOON!"));
        player.sendMessage("");

        //TODO: Give Reward
//            PlayerRewards reward = new PlayerRewards(plugin, player);
//            reward.giveExp(100);
//            reward.giveMoney(100);

        //Play Sound
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, .8f);
    }

    /***
     * This class contains data that represents a dragon egg world object.
     * BaseWorldObject is registerCommand that our WorldSettings class can spawn in our world.
     */
    private class DragonEgg extends CooldownWorldObject implements AsyncWorldObjectTick {

        private Hologram hologram;

        DragonEgg(Location location, int defaultCooldownTime) {
            super(location, defaultCooldownTime);

            //Spawn the hologram.
            double x = location.getX() + .5;
            double y = location.getY() + .3;
            double z = location.getZ() + .5;

            Location hologramLoc = new Location(location.getWorld(), x, y, z);

            hologram = new Hologram(hologramText, hologramLoc);
        }

        @Override
        public void spawnWorldObject() {
            Console.sendMessage(ChatColor.LIGHT_PURPLE + "Spawning dragon egg.");

            location.getBlock().setType(Material.DRAGON_EGG);
            currentEggsSpawned = currentEggsSpawned + 1;

            Bukkit.getWorlds().get(0).playSound(location, Sound.ENTITY_EGG_THROW, 1, .7f);

            hologram.spawnHologram();
        }

        @Override
        public void despawnWorldObject() {
            Console.sendMessage(ChatColor.DARK_PURPLE + "Removing dragon egg.");
            location.getBlock().setType(Material.AIR);

            hologram.despawnHologram();

            currentEggsSpawned = currentEggsSpawned - 1;
        }

        @Override
        public void onAsyncTick() {
            if (!isOnCooldown()) location.getWorld().playEffect(location, Effect.SMOKE, 5);
        }
    }
}
