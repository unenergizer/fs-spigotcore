package com.forgestorm.spigotcore.constants;

public enum FilePaths {

    CITIZENS("citizen/Citizens.yml"),
    CITIZENS_MESSAGES("citizen/CitizenMessages.yml"),

    MOB_TYPES("mobs/MobTypes.yml"),
    MOB_SPAWNERS("mobs/MobSpawners.yml"),

    LOBBY_PLAYER("player/LobbyPlayer.yml"),
    PLAYER_GREETING("player/PlayerGreeting.yml"),

    CHEST_LOOT("world/ChestLootLocations.yml"),
    DRAGON_EGG_LOOT("world/DragonEggLocations.yml"),
    LANTERNS("world/Lanterns.yml"),
    SERVER_SPAWN("world/ServerSpawn.yml"),
    WORLD_SETTINGS("world/WorldSettings.yml");

    private final String filePath;

    FilePaths(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        // Include the base file path
        return "plugins/FS-SpigotCore/" + filePath;
    }
}
