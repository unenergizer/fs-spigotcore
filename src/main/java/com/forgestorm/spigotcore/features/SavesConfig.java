package com.forgestorm.spigotcore.features;

import com.forgestorm.spigotcore.features.required.featuretoggle.FeatureToggleManager;

/**
 * Implemented when a feature needs to save data to a config file. This can
 * be called at any time by the implementing feature. Also this code is
 * automatically ran by our FeatureToggleManager when a feature is disabled.
 * {@link FeatureToggleManager}
 */
public interface SavesConfig {
    void saveConfiguration();
}
