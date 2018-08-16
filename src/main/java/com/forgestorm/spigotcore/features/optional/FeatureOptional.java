package com.forgestorm.spigotcore.features.optional;

import org.bukkit.event.Listener;

/**
 * Provides a safe way to enable and disable features.
 * <p>
 * Classes that implement FeatureOptional will only be initialized
 * if they are added in SpigotCore.java class with the
 * addFeature() method. These classes should not be directly
 * access anywhere in the plugin.  Lets keep as much code
 * decoupled as possible!
 */
public interface FeatureOptional extends Listener {

    /**
     * Called when we want to enable a features.
     *
     * @param manualEnable True, if startup was called manually (e.g. via command).
     */
    void onFeatureEnable(boolean manualEnable);

    /**
     * Called when we want to disable a features.
     *
     * @param manualDisable True, if startup was called manually (e.g. via command).
     */
    void onFeatureDisable(boolean manualDisable);
}
