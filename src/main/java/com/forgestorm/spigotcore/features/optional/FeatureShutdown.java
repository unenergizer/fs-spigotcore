package com.forgestorm.spigotcore.features.optional;

/**
 * Defines what a features needs to do when the server is shutting down. This code
 * is automatically ran by our FeatureToggleManager when the server shuts down.
 * {@link com.forgestorm.spigotcore.features.required.FeatureToggleManager}
 */
public interface FeatureShutdown {
    void onServerShutdown();
}
