package com.forgestorm.spigotcore.database;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;

public abstract class DatabaseTemplate {

    @Getter
    @Setter
    private boolean isDataLoaded = false;

    public abstract void loadDatabaseData(Connection connection);

    public abstract void saveDatabaseData(Connection connection);
}
