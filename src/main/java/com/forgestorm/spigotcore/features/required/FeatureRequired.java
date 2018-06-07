package com.forgestorm.spigotcore.features.required;

import lombok.Getter;

/**
 * Implemented only by classes that the server always needs.
 * These features tend to be publicly accessible from within
 * {@link com.forgestorm.spigotcore.SpigotCore}
 */
public abstract class FeatureRequired {

    @Getter
    protected boolean started;

    protected abstract void initFeatureStart();
    protected abstract void initFeatureClose();

    public void startup() {
        initFeatureStart();
        started = true;
    }

    public void shutdown() {
        if (!started) return;
        initFeatureClose();
    }
}
