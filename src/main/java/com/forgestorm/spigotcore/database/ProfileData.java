package com.forgestorm.spigotcore.database;

import lombok.Getter;
import lombok.Setter;

/**
 * ProfileData is the base class for profile data classes.
 * These classes hold per player data specific to a
 * {@link com.forgestorm.spigotcore.feature.AbstractDatabaseFeature}
 */
public abstract class ProfileData {

    @Getter
    @Setter
    private boolean isDataLoaded;

}
