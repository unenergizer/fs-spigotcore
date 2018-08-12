package com.forgestorm.spigotcore.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatActionMessages {

    // [jmc|hover=runs /list|jmc|run=/list]run /list[/jmc]
    PLAYER_PM("[jmc|hover=&eClick to sends player a Private Message|jmc|suggest=/pm {PLAYER} <yourMessage>]&c<PM>[/jmc]"),
    PLAYER_FRIEND("[jmc|hover=&eClick to add player to your friend list.|jmc|run=/friend {PLAYER}]&e<ADD_FRIEND>[/jmc]"),

    SOCIAL_DISCORD("[jmc|hover=&eClick to join our Discord!|jmc|link=https://discord.gg/NhtvMgR]&d<DISCORD>[/jmc]"),
    SOCIAL_FACEBOOK("[jmc|hover=&eClick to join our Facebook|jmc|link=https://facebook.com/ForgeStormOfficial/]&9<FACEBOOK>[/jmc]"),
    SOCIAL_WEBSITE("[jmc|hover=&eClick to join our Community forums!|jmc|link=http://www.forgestorm.com/]&e<FORUM>[/jmc]"),
    SOCIAL_TWITTER("[jmc|hover=&eClick to join our Twitter!|jmc|link=https://twitter.com/TheForgeStorm]&a<TWITTER>[/jmc]"),
    SOCIAL_YOUTUBE("[jmc|hover=&eClick to join our YouTube!|jmc|link=https://youtube.com/channel/UCOupaY4xuutRjeHzlHH7seA]&c<YOUTUBE>[/jmc]"),

    TELEPORT_LOBBY("[jmc|hover=&eClick to join the Main LobbyBungeeCommand|jmc|run=/lobby]&c<JOIN_LOBBY>[/jmc]");

    private final String message;
}
