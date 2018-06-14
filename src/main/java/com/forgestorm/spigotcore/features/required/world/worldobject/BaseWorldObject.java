package com.forgestorm.spigotcore.features.required.world.worldobject;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@RequiredArgsConstructor
public abstract class BaseWorldObject {

    protected final Location location;

    /**
     * Denotes if this world object has been spawned inside a playable world or not.
     */
    @Setter
    private boolean isSpawned = false;

    /**
     * Action to perform when a object is to spawn in a world.
     */
    public abstract void spawnWorldObject();

    /**
     * Actions to perform when a object is to despawn in a world.
     */
    public abstract void despawnWorldObject();
}
