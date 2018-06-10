package com.forgestorm.spigotcore.features;

import com.forgestorm.spigotcore.features.required.featuretoggle.FeatureToggleManager;

/**
 * Implemented when a feature needs to load data from a config file. This can
 * be called at any scheduler by the implementing feature. Also this code is
 * automatically ran by our FeatureToggleManager when a feature is enabled.
 * {@link FeatureToggleManager}
 */
public interface LoadsConfig {
    void loadConfiguration();
}
