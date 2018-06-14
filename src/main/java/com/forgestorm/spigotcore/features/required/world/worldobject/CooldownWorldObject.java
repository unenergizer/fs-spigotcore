package com.forgestorm.spigotcore.features.required.world.worldobject;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public abstract class CooldownWorldObject extends BaseWorldObject {

    @Getter
    private final int DEFAULT_COOLDOWN_TIME;

    @Setter
    @Getter
    private int timeLeft = 0;

    public CooldownWorldObject(Location location, int defaultCooldownTime) {
        super(location);
        this.DEFAULT_COOLDOWN_TIME = defaultCooldownTime;
    }

    void adjustCooldownTime() {
        if (timeLeft != 0) timeLeft = timeLeft - 1;
    }

    /**
     * @return True if this object is on cooldown, false otherwise.
     */
    boolean isOnCooldown() {
        return timeLeft != 0;
    }
}
