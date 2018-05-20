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

    //    AQUA("AQUA", "&b", ChatColor.AQUA),
//    BLACK("BLACK", "&0", ChatColor.BLACK),
//    BLUE("BLUE", "&9", ChatColor.BLUE),
//    DARK_AQUA("DARK_AQUA", "&3", ChatColor.DARK_AQUA),
//    DARK_BLUE("DARK_BLUE", "&1", ChatColor.DARK_BLUE),
//    DARK_GRAY("DARK_GRAY", "&8", ChatColor.DARK_GRAY),
//    DARK_GREEN("DARK_GREEN", "&2", ChatColor.DARK_GREEN),
//    DARK_PURPLE("DARK_PURPLE", "&5", ChatColor.DARK_PURPLE),
//    DARK_RED("DARK_RED", "&4", ChatColor.DARK_RED),
//    GOLD("GOLD", "&6", ChatColor.GOLD),
//    GRAY("GRAY", "&7", ChatColor.GRAY),
//    GREEN("GREEN", "&a", ChatColor.GREEN),
//    LIGHT_PURPLE("LIGHT_PURPLE", "&d", ChatColor.LIGHT_PURPLE),
//    RED("RED", "&c", ChatColor.RED),
//    WHITE("WHITE", "&f", ChatColor.WHITE),
//    YELLOW("YELLOW", "&e", ChatColor.YELLOW);

	private final String teamName;
	private final String prefix;
    private final ChatColor usernameColor;

    //Constructor
    UserGroup(String teamName, String prefix, ChatColor usernameColor) {
        this.teamName = teamName;
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        this.usernameColor = usernameColor;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getUserGroupPrefix() {
        return prefix + ChatColor.RESET + " ";
    }

}
