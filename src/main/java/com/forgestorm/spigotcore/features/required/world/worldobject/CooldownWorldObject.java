package com.forgestorm.spigotcore.features.required.world.worldobject;

import lombok.Getter;
import lombok.Setter;

public abstract class CooldownWorldObject extends BaseWorldObject {

    @Getter
    private final int DEFAULT_COOLDOWN_TIME;

    @Setter
    @Getter
    private int timeLeft;

    public CooldownWorldObject(int defaultCooldownTime) {
        this.DEFAULT_COOLDOWN_TIME = defaultCooldownTime;
    }

    void adjustCooldownTime() {
        timeLeft = timeLeft - 1;
    }

    void resetCooldownTime() {
        timeLeft = DEFAULT_COOLDOWN_TIME;
    }

}
