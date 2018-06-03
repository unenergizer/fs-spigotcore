package com.forgestorm.spigotcore.features.optional;

import com.forgestorm.spigotcore.features.required.featuretoggle.FeatureToggleManager;

/**
 * Defines what a features needs to do when the server is shutting down. This code
 * is automatically ran by our FeatureToggleManager when the server shuts down.
 * {@link FeatureToggleManager}
 */
public interface ShutdownTask {
    void onServerShutdown();
}
