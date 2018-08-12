package com.forgestorm.spigotcore.features.required.world.loader;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.util.file.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorldManager extends FeatureRequired implements Listener {

    private final Map<String, WorldData> worldUnloadDataList = new HashMap<>();

    private SyncWorldLoader syncWorldLoader;
    private AsyncWorldCopy asyncWorldCopier;

    @Override
    public void initFeatureStart() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        syncWorldLoader = new SyncWorldLoader();
        syncWorldLoader.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
        asyncWorldCopier = new AsyncWorldCopy();
        asyncWorldCopier.runTaskTimerAsynchronously(SpigotCore.PLUGIN, 0, 1);
    }

    @Override
    public void initFeatureClose() {
        WorldUnloadEvent.getHandlerList().unregister(this);
        syncWorldLoader.cancel();
        asyncWorldCopier.cancel();
    }

    public void loadWorld(String worldName, File sourceDirectory, boolean removeSourceDirectory) {
        asyncWorldCopier.worldsToCopy.add(new WorldData(worldName, sourceDirectory, removeSourceDirectory, true));
    }

    public void loadWorld(String worldName, File sourceDirectory) {
        asyncWorldCopier.worldsToCopy.add(new WorldData(worldName, sourceDirectory, false, true));
    }

    /**
     * Unloads a world from Bukkit and saves a copy of it in a backup directory. Optionally
     * removes the world from the WorldContainer folder (same folder as world, world_nether
     * and world_the_end).
     *
     * @param worldName                The world to unload.
     * @param saveWorld                True if world is saved, false otherwise.
     * @param saveDirectory            The directory to backup the world to.
     * @param removeFromWorldContainer Deletes the backedup world from the world container (main server directory)
     */
    public void unloadWorld(String worldName, boolean saveWorld, File saveDirectory, boolean removeFromWorldContainer) {
        worldUnloadDataList.put(worldName, new WorldData(worldName, saveDirectory, removeFromWorldContainer, false));
        Bukkit.unloadWorld(worldName, saveWorld);
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        // World safely unloaded, now send to async copy.
        if (!worldUnloadDataList.containsKey(event.getWorld().getName())) return;
        asyncWorldCopier.worldsToCopy.add(worldUnloadDataList.get(event.getWorld().getName()));
    }

    /**
     * Unloads a world from Bukkit but does not move the world to a backup directory.
     *
     * @param worldName The world to unload.
     * @param saveWorld True if world is saved, false otherwise.
     */
    public void unloadWorld(String worldName, boolean saveWorld) {
        Bukkit.unloadWorld(worldName, saveWorld);
    }

    class SyncWorldLoader extends BukkitRunnable {

        private Queue<WorldData> worldsToLoad = new ConcurrentLinkedQueue<>();

        @Override
        public void run() {
            if (worldsToLoad.isEmpty()) return;
            WorldData worldData = worldsToLoad.remove();
            WorldCreator worldCreator = new WorldCreator(worldData.getWorldName());
            worldCreator.createWorld();
        }
    }

    class AsyncWorldCopy extends BukkitRunnable {

        private Queue<WorldData> worldsToCopy = new ConcurrentLinkedQueue<>();

        @Override
        public void run() {
            if (worldsToCopy.isEmpty()) return;
            WorldData worldData = worldsToCopy.remove();

            // World loading / unloading operations.
            if (worldData.isLoadingWorld()) {

                // Loading
                FileUtil.copyDirectory(worldData.getSourceDirectory(), new File(worldData.getWorldName()));
                syncWorldLoader.worldsToLoad.add(worldData);
            } else {

                // Unloading
                FileUtil.copyDirectory(new File(worldData.getWorldName()), worldData.getSourceDirectory());
            }

            // Remove source directory if enabled
            if (!worldData.isRemoveSourceDirectory()) return;
            FileUtil.removeDirectory(new File(worldData.getWorldName()).toPath());
        }
    }
}
