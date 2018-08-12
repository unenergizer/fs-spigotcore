package com.forgestorm.spigotcore.features.optional.world.gameworld;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.constants.WorldDirectories;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import com.forgestorm.spigotcore.features.required.world.worldobject.SyncWorldObjectTick;
import com.forgestorm.spigotcore.util.display.Hologram;
import com.forgestorm.spigotcore.util.file.FileUtil;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.text.Text;
import com.forgestorm.spigotcore.util.world.LocationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameWorldManager implements FeatureOptional, LoadsConfig, Listener {

    private final List<GameWorldData> gameWorldDataList = new ArrayList<>();
    private Location hologramLocation;
    private GameWorldData currentGameWorld;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        hologramLocation = new Location(Bukkit.getWorlds().get(0), 7.5, 79, 22.5);

        mapSelect();
    }

    private void mapSelect() {
        int rand = RandomChance.randomInt(0, gameWorldDataList.size() - 1);

        currentGameWorld = null;
        currentGameWorld = gameWorldDataList.get(rand);

        SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(hologramLocation, new GameWorldHologram(hologramLocation, currentGameWorld.displayName));
        SpigotCore.PLUGIN.getWorldManager().loadWorld(currentGameWorld.folderName,
                new File(WorldDirectories.RPG.getWorldDirectory() + File.separator + currentGameWorld.folderName));
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        PlayerPortalEvent.getHandlerList().unregister(this);

        unloadWorld(currentGameWorld.folderName);
    }

    @Override
    public void loadConfiguration() {
        final String pathStart = "Worlds";

        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.RPG_GAME_WORLDS.toString()));
        Set<String> worldKeySet = config.getConfigurationSection(pathStart).getKeys(false);

        // Loops through all game worlds
        for (String worldName : worldKeySet) {

            //Set<String> topicKeySet = config.getConfigurationSection(pathStart + "." + worldName).getKeys(false);

            String insidePath = pathStart + "." + worldName + ".";

            String displayName = config.getString(insidePath + "displayName");
            String author = config.getString(insidePath + "author");
            String url = config.getString(insidePath + "url");

            insidePath = insidePath + ".spawn.";

            double x = config.getDouble(insidePath + "x");
            double y = config.getDouble(insidePath + "y");
            double z = config.getDouble(insidePath + "z");
            float yaw = config.getLong(insidePath + "yaw");
            float pitch = config.getLong(insidePath + "pitch");

            GameWorldData gameWorldData = new GameWorldData(worldName, displayName, author, url, x, y, z, yaw, pitch);

            // Loop through all topic keys and get messages.
//            for (String topicKey : topicKeySet) {
//                final String path = pathStart + "." + worldName + "." + topicKey;
//
//                npcTopics.addMessageTopic(topicKey, config.getStringList(path));
//            }

            gameWorldDataList.add(gameWorldData);
        }
    }

    @EventHandler
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        if (event.getCause() != PlayerPortalEvent.TeleportCause.NETHER_PORTAL) return;
        if (!LocationUtil.isWorldLoaded(currentGameWorld.folderName)) return;

        event.getPlayer().setPortalCooldown(20 * 3); // cooldown in ticks

        // Check doorway enter/exit
        enterGameWorld(event.getPlayer());
    }

    private void unloadWorld(String worldName) {
        SpigotCore.PLUGIN.getWorldManager().unloadWorld(worldName, false);
        FileUtil.removeDirectory(new File(currentGameWorld.folderName).toPath());
    }

    private void enterGameWorld(Player player) {
//        player.sendMessage("");
//        player.sendMessage(CenterChatText.centerChatMessage(Text.color("&7- -- ---[ &aWorld Change &7]--- -- -")));
//        player.sendMessage("");
//        player.sendMessage(Text.color("&eWorld&7:&r " + ChatColor.RESET + currentGameWorld.displayName));
//        player.sendMessage(Text.color("&eAuthor&7:&r " + ChatColor.RESET + currentGameWorld.getAuthor()));
//        player.sendMessage(Text.color("&eurl&7:&r " + ChatColor.RESET + currentGameWorld.getUrl()));
//        player.sendMessage("");
//        player.sendMessage(Text.color("&cType &7/spawn &cto exit"));
        SpigotCore.PLUGIN.getTeleportManager().teleportPlayer(player, new Location(Bukkit.getWorld(currentGameWorld.folderName), currentGameWorld.x, currentGameWorld.y, currentGameWorld.z, currentGameWorld.yaw, currentGameWorld.pitch));
    }

    private void exitGameWorld(Player player) {
        player.chat("/spawn");
    }

    class GameWorldHologram extends BaseWorldObject implements SyncWorldObjectTick {

        private Hologram hologram;
        private int tick;

        GameWorldHologram(Location hologramLocation, String worldName) {
            super(hologramLocation);
            hologram = new Hologram(hologramLocation,
                    Text.color("&a&lWorld Loaded&7&l:&r&l " + worldName),
                    "0",
                    Text.color("&d&lDifficulty&7&l:&r&l " + Text.color("Easy")));
        }


        @Override
        public void spawnWorldObject() {
            hologram.spawnHologram();
        }

        @Override
        public void despawnWorldObject() {
            hologram.despawnHologram();
        }

        @Override
        public void onSyncTick() {
            // This is just an example of changing the hologram info (animating based on tick)
            hologram.changeText(Text.color("&e&lTicks Online&7&l:&r&l " + Integer.toString(tick)), 1);
            tick++;
        }
    }

    @Getter
    @AllArgsConstructor
    private class GameWorldData {

        private String folderName;
        private String displayName;
        private String author;
        private String url;
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;
        //private Map<String, String> nodeMap;
    }

    @AllArgsConstructor
    enum PortalState {
        UNLOADING(false),
        LOADING(false),
        READY(true);

        boolean allowPortalUse;
    }
}
