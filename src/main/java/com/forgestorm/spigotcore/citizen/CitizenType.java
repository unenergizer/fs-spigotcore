package com.forgestorm.spigotcore.citizen;

import org.bukkit.ChatColor;

public enum CitizenType {

    AUCTIONEER("Auctioneer"),
    BANKER("Banker"),
    BARTENDER("Bartender"),
    DIRTY_HOBO("Dirty Old Hobo"),
    LOBBY("Back To Lobby"),
    MERCHANT("Item Merchant"),
    MERCHANT_BOAT("Boat Merchant"),
    MERCHANT_REALM("Realm Merchant"),
    MERCHANT_FARMING("Farming Merchant"),
    MERCHANT_FISHING("Fishing Merchant"),
    MERCHANT_MINING("Mining Merchant"),
    MERCHANT_WOOD_CUTTING("Wood Cutting Merchant"),
    NONE(""),
    PLAY_MINIGAMES("Play Minigames!"),
    PROFESSION_COOKING("Cooking Trainer"),
    PROFESSION_FARMING("Farming Trainer"),
    PROFESSION_FISHING("Fishing Trainer"),
    PROFESSION_MINING("Mining Trainer"),
    PROFESSION_SMELTING("Smelting Trainer"),
    PROFESSION_WOOD_CUTTING("Wood Cutting Trainer"),
    SOCIAL_MEDIA("Social Media"),
    DISCORD("Discord Server"),
    TUTORIAL("Server Tutorial"),
    TUTORIAL_START("Tutorial Start"),
    TUTORIAL_EXIT("Tutorial Exit"),
    TUTORIAL_SKIP("Skip Tutorial"),
    TUTORIAL_PROFESSION_FARMING("Farming Information"),
    TUTORIAL_PROFESSION_FISHING("Fishing Information"),
    TUTORIAL_PROFESSION_MINING("Mining Information"),
    TUTORIAL_PROFESSION_COOKING_SMELTING("Cooking and Smelting Information"),
    TUTORIAL_PROFESSION_WOOD_CUTTING("Wood Cutting Information"),
    TUTORIAL_PLAYER_REALM("Player Realms Information"),
    TUTORIAL_CRAFTING("Crafting Information"),
    VOTE("Daily Rewards");

    private final String title;

    CitizenType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return ChatColor.YELLOW + "" + ChatColor.BOLD + title;
    }

    public String getTitle(ChatColor color) {
        return color + "" + ChatColor.BOLD + title;
    }
}
