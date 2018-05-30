package com.forgestorm.spigotcore.features;

/**
 * Implemented when a feature needs to save data to a config file. This can
 * be called at any time by the implementing feature. Also this code is
 * automatically ran by our FeatureToggleManager when a feature is disabled.
 * {@link com.forgestorm.spigotcore.features.required.FeatureToggleManager}
 */
public interface SavesConfig {
    void saveConfiguration();
}
