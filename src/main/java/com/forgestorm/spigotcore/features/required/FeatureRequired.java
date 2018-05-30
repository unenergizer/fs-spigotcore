package com.forgestorm.spigotcore.features.required;

/**
 * Implemented only by classes that the server always needs.
 * These features tend to be publicly accessible from within
 * {@link com.forgestorm.spigotcore.SpigotCore}
 */
public interface FeatureRequired {
    void onServerStartup();
    void onServerShutdown();
}
