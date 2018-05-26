package com.forgestorm.spigotcore.feature;

/**
 * Provides a safe way to enable and disable features.
 *
 * Classes that implement FeatureOptional will only be initialized
 * if they are added in SpigotCore.java class with the
 * addFeature() method. These classes should not be directly
 * access anywhere in the plugin.  Lets keep as much code
 * decoupled as possible!
 */
public interface FeatureOptional {

    void onEnable();
    void onDisable();
}
