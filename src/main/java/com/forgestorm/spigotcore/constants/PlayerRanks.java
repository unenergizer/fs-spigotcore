package com.forgestorm.spigotcore.constants;

import com.forgestorm.spigotcore.util.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum PlayerRanks {

    NEW_PLAYER("newPlayer", "&7[&aNew&7]", ChatColor.GRAY),
    FREE_PLAYER("free", "", ChatColor.GRAY),
    VIP("vip", "&aVIP", ChatColor.WHITE),
    VIP_PLUS("vip_plus", "&aVIP+", ChatColor.WHITE),
    MVP("mvp", "&bMVP", ChatColor.WHITE),
    MVP_PLUS("mvp_plus", "&bMVP+", ChatColor.WHITE),
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

    public String getUsernamePrefix() {
        if (this == FREE_PLAYER) return "";
        return Text.color(usernamePrefix + "&r ");
    }
}
