package com.forgestorm.spigotcore.feature;

/**
 * Provides a safe way to enable and disable features.
 * <p>
 * Classes that implement FeatureOptional will only be initialized
 * if they are added in SpigotCore.java class with the
 * addFeature() method. These classes should not be directly
 * access anywhere in the plugin.  Lets keep as much code
 * decoupled as possible!
 */
public interface FeatureOptional {

    /**
     * Called when we want to enable a feature.
     *
     * @param manualEnable True, if startup was called manually (e.g. via command).
     */
    void onEnable(boolean manualEnable);

    /**
     * Called when we want to disable a feature.
     *
     * @param manualDisable True, if startup was called manually (e.g. via command).
     */
    void onDisable(boolean manualDisable);
}
