package com.forgestorm.spigotcore.database;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;

/**
 * DatabaseTemplate is the base class for profile data classes.
 * These classes hold per player data specific to a
 * {@link com.forgestorm.spigotcore.feature.AbstractDatabaseFeature}
 */
public abstract class DatabaseTemplate {

    @Getter
    @Setter
    private boolean isDataLoaded = false;

    public abstract void loadDatabaseData(Connection connection);

    public abstract void saveDatabaseData(Connection connection);
}
