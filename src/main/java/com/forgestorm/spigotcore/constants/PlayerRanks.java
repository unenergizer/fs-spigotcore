package com.forgestorm.spigotcore.constants;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum PlayerRanks {

    NEW_PLAYER("newPlayer", "&7[&aNew&7]", ChatColor.GRAY),
    FREE_PLAYER("free", "", ChatColor.GRAY),
    VIP("paid1", "&aVIP", ChatColor.WHITE),
    VIP_PLUS("paid2", "&aVIP+", ChatColor.WHITE),
    MVP("paid3", "&bMVP", ChatColor.WHITE),
    MVP_PLUS("paid4", "&bMVP+", ChatColor.WHITE),
    MODERATOR("mod", "&9&lMOD", ChatColor.YELLOW),
    ADMINISTRATOR("admin", "&c&lADMIN", ChatColor.YELLOW),
    NPC("npc", "&7[&9NPC&7]", ChatColor.WHITE),
    MINIGAME_TEAM("mgTeam", "&lTEAM&7&l:&r", ChatColor.WHITE),
    MINIGAME_KIT("mgKit", "&lKIT&7&l:&r", ChatColor.WHITE),
    MINIGAME_SPECTATOR("mgSpectator", "&7[Spectator]", ChatColor.DARK_GRAY);

    @Getter
	private final String scoreboardTeamName;
	private final String usernamePrefix;
    @Getter
	private final ChatColor chatColor;

    PlayerRanks(String scoreboardTeamName, String usernamePrefix, ChatColor messageColor) {
        this.scoreboardTeamName = scoreboardTeamName;
        this.usernamePrefix = ChatColor.translateAlternateColorCodes('&', usernamePrefix);
        this.chatColor = messageColor;
    }

    public String getUsernamePrefix() {
        if (this == FREE_PLAYER) return "";
        return usernamePrefix + ChatColor.RESET + " ";
    }
}
