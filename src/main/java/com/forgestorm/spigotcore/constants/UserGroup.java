package com.forgestorm.spigotcore.constants;

import org.bukkit.ChatColor;

public enum UserGroup {

    USER_GROUP_NEW("newPlayer", "&7[&aNew&7]", ChatColor.GRAY),
    USER_GROUP_0("free", "", ChatColor.GRAY),//Free user
    USER_GROUP_1("paid1", "&aVIP", ChatColor.WHITE),
    USER_GROUP_2("paid2", "&aVIP+", ChatColor.WHITE),
    USER_GROUP_3("paid3", "&bMVP", ChatColor.WHITE),
    USER_GROUP_4("paid4", "&bMVP+", ChatColor.WHITE),
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
