package com.forgestorm.spigotcore.constants;

import com.forgestorm.spigotcore.SpigotCore;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FilePaths {

    GAME_TIPS("chat/GameTips.yml"),

    CITIZENS("citizen/Citizens.yml"),
    CITIZENS_MESSAGES("citizen/CitizenMessages.yml"),

    DISCORD_MANAGER("discord/DiscordManager.yml"),

    JAZZY_JUKEBOX("gadget/JazzyJukebox.yml"),

    MOB_TYPES("mobs/MobTypes.yml"),
    MOB_SPAWNERS("mobs/MobSpawners.yml"),

    LOBBY_PLAYER("player/LobbyPlayer.yml"),
    PLAYER_BOSS_BAR("player/BossBarText.yml"),
    PLAYER_GREETING("player/PlayerGreeting.yml"),
    PLAYER_LIST_TEXT("player/PlayerListText.yml"),

    CHAT_ICONS("resources/chaticons/"),
    RPG_GAME_WORLDS("rpg/GameWorlds.yml"),
    RPG_ITEM_DATABASE("rpg/ItemDatabase.yml"),

    PROFESSION_FARMING("skills/Farming.yml"),
    PROFESSION_FISHING("skills/Fishing.yml"),
    PROFESSION_COOKING_AND_SMELTING("skills/CookingAndSmelting.yml"),
    PROFESSION_WOOD_CUTTING("skills/WoodCutting.yml"),
    PROFESSION_MINING("skills/Mining.yml"),

    CHEST_LOOT("world/ChestLootLocations.yml"),
    DRAGON_EGG_LOOT("world/DragonEggLocations.yml"),
    LANTERNS("world/Lanterns.yml"),
    SERVER_SPAWN("world/ServerSpawn.yml"),
    WORLD_HOLOGRAM("world/WorldHolograms.yml"),
    WORLD_SETTINGS("world/WorldSettings.yml");


    private final String filePath;

    @Override
    public String toString() {
        // Include the base file path
        return SpigotCore.PLUGIN.getDataFolder() + "/" + filePath;
    }
}
