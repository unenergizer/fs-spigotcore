package com.forgestorm.spigotcore.world.worldobject;


import lombok.Getter;
import lombok.Setter;

public abstract class BaseWorldObject {

    /**
     * Denotes if this world object has been spawned inside a playable world or not.
     */
    @Getter
    @Setter
    private boolean isSpawned = false;

    /**
     * Action to perform when a object is to spawn in a world.
     */
    public abstract void spawnWorldObject();

    /**
     * Actions to perform when a object is to spawn in a world.
     */
    public abstract void removeWorldObject();
}
