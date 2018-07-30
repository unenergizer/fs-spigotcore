package com.forgestorm.spigotcore.features.optional.world.gameworld;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
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
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        hologramLocation = new Location(Bukkit.getWorlds().get(0), 7.5, 79, 22.5);

        mapSelect();
    }

    private void mapSelect() {
        int rand = RandomChance.randomInt(1, gameWorldDataList.size() - 1);

        currentGameWorld = null;
        currentGameWorld = gameWorldDataList.get(rand);

        SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(hologramLocation, new GameWorldHologram(hologramLocation, currentGameWorld.displayName));
        SpigotCore.PLUGIN.getWorldManager().loadWorld(currentGameWorld.folderName,
                new File(".." + File.separator + "game_worlds" + File.separator + currentGameWorld.folderName));
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

            String insidePath = pathStart + "." + worldName;

            String displayName = config.getString(insidePath + ".displayName");
            String author = config.getString(insidePath + ".author");
            String url = config.getString(insidePath + ".url");

            GameWorldData gameWorldData = new GameWorldData(worldName, displayName, author, url);

            // Loop through all topic keys and get messages.
//            for (String topicKey : topicKeySet) {
//                final String path = pathStart + "." + worldName + "." + topicKey;
//
//                npcTopics.addMessageTopic(topicKey, config.getStringList(path));
//            }

            gameWorldDataList.add(gameWorldData);

            for (GameWorldData gameWorldData1 : gameWorldDataList) {
                System.out.println(gameWorldData1.folderName);
                System.out.println(gameWorldData1.displayName);
                System.out.println(gameWorldData1.author);
                System.out.println(gameWorldData1.url);
            }
            System.out.println("Size: " + gameWorldDataList.size());
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

        // Delete world
        FileUtil.removeDirectory(new File(SpigotCore.PLUGIN.getDataFolder() + File.pathSeparator + currentGameWorld.folderName).toPath());
    }

    private void enterGameWorld(Player player) {
        player.sendMessage("Taking you to " + currentGameWorld.displayName + " Map");
        player.teleport(new Location(Bukkit.getWorld(currentGameWorld.folderName), -176.5, 93, 33.5));
    }

    private void exitGameWorld(Player player) {
        // TODO: Exit back to the main lobby world
    }

    class GameWorldHologram extends BaseWorldObject implements SyncWorldObjectTick {

        private Hologram hologram;
        private int tick;

        GameWorldHologram(Location hologramLocation, String worldName) {
            super(hologramLocation);

            hologram = new Hologram("Loaded: " + worldName, hologramLocation);
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
            if (tick > 100) tick = 0;
            hologram.changeText(Integer.toString(tick));
            tick++;
        }
    }

    @Getter
    @AllArgsConstructor
    class GameWorldData {

        private String folderName;
        private String displayName;
        private String author;
        private String url;
        //private Map<String, String> nodeMap;
    }
}
