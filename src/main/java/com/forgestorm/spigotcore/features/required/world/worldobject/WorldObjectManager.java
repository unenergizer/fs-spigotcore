package com.forgestorm.spigotcore.features.required.world.worldobject;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.features.required.featuretoggle.FeatureToggleManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * WorldObjectManager is a nifty class that will spawn/remove various types of WorldObjects
 * based on a configurable distance from the player. Example of BaseWorldObjects could be
 * holograms, loot chests, entities, etc.
 * <p>
 * RULES:
 * <ul>
 *     <li>Only spawns {@link BaseWorldObject} in the world when they are needed (and chunks loaded).</li>
 *     <li>BaseWorldObjects should never be added with the intent to have it be saved in the world.</li>
 *     <li>Remove BaseWorldObjects from the map when the server is shutdown.</li>
 * </ul>
 * <p>
 * TODO:
 * <ul>
 *     <li>Implement a respawn timing system for {@link BaseWorldObject}</li>
 * </ul>
 */
public class WorldObjectManager implements FeatureRequired {

    // Quick command to kill entites besides players. Will remove later.
    //TODO: /kill @e[type=!Player]

    private static final int SPAWN_RADIUS_SQUARED = 32 * 32;
    private final Map<Location, BaseWorldObject> worldObjectMap = new ConcurrentHashMap<>();
    private final Queue<BaseWorldObject> baseWorldObjectQueue = new ConcurrentLinkedDeque<>();

    private BukkitRunnable asyncRunnable;
    private BukkitRunnable syncRunnable;

    @Override
    public void onServerStartup() {
        asyncRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                processWorldObjectsAsync();
            }
        };
        asyncRunnable.runTaskTimerAsynchronously(SpigotCore.PLUGIN, FeatureToggleManager.FEATURE_TASK_START_DELAY, 20);

        syncRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                processWorldObjectsSync();
            }
        };
        syncRunnable.runTaskTimer(SpigotCore.PLUGIN, FeatureToggleManager.FEATURE_TASK_START_DELAY, 20);
    }

    @Override
    public void onServerShutdown() {
        asyncRunnable.cancel();
        syncRunnable.cancel();
        baseWorldObjectQueue.clear();

        // Cleanup!
        for (BaseWorldObject baseWorldObject : worldObjectMap.values()) {
            baseWorldObject.setSpawned(false);
            baseWorldObject.removeWorldObject();
        }
        worldObjectMap.clear();
    }

    /**
     * Set of Async processes that will be carried out on all WorldObjects.
     */
    private void processWorldObjectsAsync() {
        for (Map.Entry<Location, BaseWorldObject> entry : worldObjectMap.entrySet()) {
            Location location = entry.getKey();
            BaseWorldObject baseWorldObject = entry.getValue();

            adjustRespawnTime(baseWorldObject);
            shouldSpawn(location, baseWorldObject);
        }
    }

    /**
     * Test to see if this BaseWorldObject has timing components. If it does,
     * lets adjust the time for these objects now.
     *
     * @param baseWorldObject The WorldObject to test and adjust time for.
     */
    private void adjustRespawnTime(BaseWorldObject baseWorldObject) {
        if (baseWorldObject instanceof CooldownWorldObject)
            ((CooldownWorldObject) baseWorldObject).adjustCooldownTime();
    }

    /**
     * Async task that checks to see if the player is near a world object.
     * If a player is near the world object location we will spawn the
     * world object (if it is not already spawned). However, if no players
     * are near the world object being checked, we will despawn it.
     *
     * @param location        The location of the world object in the player world.
     * @param baseWorldObject The BaseWorldObject we will check if it should spawn.
     */
    private void shouldSpawn(Location location, BaseWorldObject baseWorldObject) {
        boolean isPlayerNear = false;

        // Check to see if a player is near a BaseWorldObject location
        for (Player player : location.getWorld().getPlayers()) {

            // See if a player is near this BaseWorldObject.
            if (location.distanceSquared(player.getLocation()) <= SPAWN_RADIUS_SQUARED) {
                isPlayerNear = true;

                // Player is near, but the BaseWorldObject is not spawned. Spawn it now!
                if (!baseWorldObject.isSpawned()) {
                    baseWorldObject.setSpawned(true);
                    baseWorldObjectQueue.add(baseWorldObject);
                }
            }
        }

        // No player near, despawn the BaseWorldObject
        if (!isPlayerNear && baseWorldObject.isSpawned()) {
            baseWorldObject.setSpawned(false);
            baseWorldObjectQueue.add(baseWorldObject);
        }
    }

    /**
     * Sync task that will process any WorldObjects in our que. If an BaseWorldObject exists,
     * we will spawn it or remove it based on it's state.
     */
    private void processWorldObjectsSync() {
        while (!baseWorldObjectQueue.isEmpty()) {
            BaseWorldObject baseWorldObject = baseWorldObjectQueue.remove();

            // Player is near this BaseWorldObject, lets spawn it.
            if (baseWorldObject.isSpawned()) {
                baseWorldObject.spawnWorldObject();
            } else {

                // No players near this BaseWorldObject, lets remove it.
                baseWorldObject.removeWorldObject();
            }
        }
    }

    /**
     * Adds a new BaseWorldObject to be spawned if a player goes near it.
     *
     * @param location        The location of the BaseWorldObject.
     * @param baseWorldObject The BaseWorldObject we will possible be spawning.
     */
    public void addWorldObject(Location location, BaseWorldObject baseWorldObject) {
        worldObjectMap.put(location, baseWorldObject);
    }

    /**
     * Removes a BaseWorldObject. This BaseWorldObject can no longer be spawned in the world.
     *
     * @param location The location the BaseWorldObject (Map key).
     */
    public void removeWorldObject(Location location) {
        BaseWorldObject baseWorldObject = worldObjectMap.get(location);

        // If the BaseWorldObject currently exists in the world, lets remove it.
        if (baseWorldObject.isSpawned()) {
            baseWorldObject.setSpawned(false);
            baseWorldObject.removeWorldObject();
        }

        worldObjectMap.remove(location);
    }

    /**
     * Removes a BaseWorldObject. This BaseWorldObject can no longer be spawned in the world.
     *
     * @param baseWorldObject The BaseWorldObject we wish to remove.
     */
    public void removeWorldObject(BaseWorldObject baseWorldObject) {
        for (Map.Entry<Location, BaseWorldObject> entry : worldObjectMap.entrySet()) {
            if (entry.getValue().equals(baseWorldObject)) {
                removeWorldObject(entry.getKey());
                break;
            }
        }
    }
}
