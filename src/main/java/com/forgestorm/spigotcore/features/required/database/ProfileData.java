package com.forgestorm.spigotcore.features.required.database;

import lombok.Getter;
import lombok.Setter;

/**
 * ProfileData is the base class for profile data classes.
 */
public abstract class ProfileData {

    @Getter
    @Setter
    private boolean isDataLoaded;

}
