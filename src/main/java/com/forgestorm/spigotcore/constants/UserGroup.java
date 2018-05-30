package com.forgestorm.spigotcore.constants;

import org.bukkit.ChatColor;

public enum UserGroup {

    NEW_PLAYER("newPlayer", "&7[&aNew&7]", ChatColor.GRAY),
    FREE_PLAYER("free", "", ChatColor.GRAY),//Free user
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

	private final String teamName;
	private final String prefix;
    private final ChatColor messageColor;

    //Constructor
    UserGroup(String teamName, String prefix, ChatColor messageColor) {
        this.teamName = teamName;
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        this.messageColor = messageColor;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getUserGroupPrefix() {
        return prefix + ChatColor.RESET + " ";
    }

    public ChatColor getMessageColor() {
        return messageColor;
    }
}
