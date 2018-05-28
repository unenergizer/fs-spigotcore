package com.forgestorm.spigotcore.feature;

/**
 * Defines how an optional feature will be permanently shutdown.
 * This is only called when the server is shutting down.
 * Once called, it is no longer safe to enable said feature until
 * the server restarts.
 */
public interface FeatureShutdown {
    void onServerShutdown();
}
