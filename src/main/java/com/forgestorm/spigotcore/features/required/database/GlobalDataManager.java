package com.forgestorm.spigotcore.features.required.database;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalDataManager {

    private final Map<Player, MainTableData> playerProfileDataMap = new ConcurrentHashMap<>();

    private class MainTableData {

        private UUID uuid;
        private String name;
    }
}
