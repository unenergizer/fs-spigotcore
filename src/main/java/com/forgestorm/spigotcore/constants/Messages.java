package com.forgestorm.spigotcore.constants;

import com.forgestorm.spigotcore.util.text.Text;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Messages {

    PROFILE_LOOKUP("&8&l&m-----------------&r&l &l &l &9&lProfile&l &l &l &8&l&m-----------------"),

    ITEM_PICKUP_GEMS("         &a&l+%s Gems"),

    SCOREBOARD_TITLE("- &e&lFORGESTORM &r-"),
    SCOREBOARD_BLANK_LINE_1("&r"),
    SCOREBOARD_GEMS("&a&lGEMS&7&l:&r "),
    SCOREBOARD_LEVEL("&a&lLVL&7&l:&r "),
    SCOREBOARD_XP("&a&lXP&7&l:&r "),
    SCOREBOARD_BLANK_LINE_2("&r&r"),
    SCOREBOARD_SERVER("&b&lSERVER&7&l:&r "),

    ECONOMY_PURCHASE_SUCCESS("&aYour purchase was successful!!"),
    ECONOMY_PURCHASE_FAILED("&cYou do not have enough money to make this purchase."),

    PROFESSION_ACTION_FAILED("&cYour profession action was unsuccessful."),
    PROFESSION_NOT_LEARNED("&cYou have not learned this profession."),
    PROFESSION_LEVEL_NOT_HIGH_ENOUGH("&cYou don't have the required level to use this tool."),
    PROFESSION_WRONG_TOOL("&cYou're using the wrong tool for this."),

    PLAYER_WELCOME_1("&e&lForgeStorm: &r&lRPG MINIGAME SERVER %s"),
    PLAYER_WELCOME_2("&7&nhttp://www.ForgeStorm.com/"),
    PLAYER_WELCOME_3("&c/help &e/mainmenu &a/settings &b/playtime &d/lobby"),

    BAR_REALM("&8&l&m--------------&r&l &l &l &f&lRealm Commands&l &l &l &8&l&m--------------"),
    REALM_PORTAL_DUPLICATE("&cYou already have a realm opened! Close it to open your realm at another location."),
    REALM_PORTAL_OPENED("&d&l* Realm Portal OPENED *"),
    REALM_PORTAL_TITLE("&7Type &3/realm help &7for a list of commands."),
    REALM_PORTAL_PLACE_DENY_BLOCK("&cYou &ncannot&c open a realm portal here."),
    REALM_PORTAL_PLACE_TOO_CLOSE("&cYou &ncannot&c place a realm portal so close to another one."),

    BUNGEECORD_CONNECT_SERVER("&cConnecting you to server \"&e%s&c\"..."),

    BLOCK_PLACE_TNT_SUCCESS("&a&lTNT was set, RUN!"),
    BLOCK_PLACE_TNT_FAIL("&c&lYou can not set TNT here!"),

    COMMAND_ADMIN_PLAYER_NOT_ONLINE("&cThat player is not online."),
    COMMAND_ADMIN_DEMOTE_ADMIN_SUCCESS("&aYou demoted the player from admin."),
    COMMAND_ADMIN_DEMOTE_MODERATOR_SUCCESS("&aYou demoted the player from moderator."),
    COMMAND_ADMIN_DEMOTE_USERGROUP_SUCCESS("&aYou demoted the player's usergroup."),
    COMMAND_ADMIN_DEMOTE_USERGROUP_FAILURE("&cUnable to demote the player's usergroup."),
    COMMAND_ADMIN_PROMOTE_ADMIN_SUCCESS("&aYou promoted the player to admin."),
    COMMAND_ADMIN_PROMOTE_MODERATOR_SUCCESS("&aYou promoted the player to moderator."),
    COMMAND_ADMIN_PROMOTE_USERGROUP_SUCCESS("&aYou promoted the player's usergroup."),
    COMMAND_ADMIN_SET_USERGROUP_SUCCESS("&aYou have set the player's usergroup to &e%s&a."),
    COMMAND_ADMIN_SET_CURRENCY_SUCCESS("&aPlayer's currency has been changed to %s&a."),
    COMMAND_ADMIN_SET_PREMIUMCURRENCY_SUCCESS("&aPlayer's premium currency has been changed to %s&a."),
    COMMAND_ADMIN_SET_EXPERIENCE_SUCCESS("&aPlayer's experience has been changed to %s&a."),
    COMMAND_ADMIN_SET_LEVEL_SUCCESS("&aPlayer's level has been changed to %s&a."),
    COMMAND_ADMIN_ADD_CURRENCY_SUCCESS("&aPlayer's currency has been increased. &e%c &c-> &e%s&a."),
    COMMAND_ADMIN_ADD_PREMIUMCURRENCY_SUCCESS("&aPlayer's premium currency has been increased. &e%c &c-> &e%s&a."),
    COMMAND_ADMIN_ADD_EXPERIENCE_SUCCESS("&aPlayer's experience has been increased. &e%c &c-> &e%s&a."),
    COMMAND_ADMIN_ADD_LEVEL_SUCCESS("&aPlayer's level has been increased. &e%c &c-> &e%s&a."),
    COMMAND_ADMIN_REMOVE_CURRENCY_SUCCESS("&aPlayer's currency has been decreased. &e%c &c-> &e%s&a."),
    COMMAND_ADMIN_REMOVE_PREMIUMCURRENCY_SUCCESS("&aPlayer's premium currency has been decreased. &e%c &c-> &e%s&a."),
    COMMAND_ADMIN_REMOVE_EXPERIENCE_SUCCESS("&aPlayer's experience has been decreased. &e%c &c-> &e%s&a."),
    COMMAND_ADMIN_REMOVE_LEVEL_SUCCESS("&aPlayer's level has been decreased. &e%c &c-> &e%s&a."),

    BAR_DISCORD("&8&l&m-----------------&r&l &l &l &9&lDiscord&l &l &l &8&l&m-----------------"),
    DISCORD_INFO_1("&7Want to voice chat with staff and our members?"),
    DISCORD_INFO_2("&7If so, join our Discord server! "),
    DISCORD_INFO_3("&bLink&8: &ehttps://discord.gg/NhtvMgR"),

    BAR_LEVEL_UP("&8&l&m----------------&r&l &l &l &b&lLevel Up!&l &l &l &8&l&m-----------------"),
    LEVEL_UP_01("&e\u2748 \u2748 \u2748 Congratulations!! \u2748 \u2748 \u2748 "),

    BAR_SOCIAL_MEDIA("&8&l&m---------------&r&l &l &l &9&lSocial Media&l &l &l &8&l&m---------------"),
    FS_SOCIAL_WEB("&c• http://www.forgestorm.com/"),
    FS_SOCIAL_FACEBOOK("&e• https://facebook.com/ForgeStormOfficial/"),
    FS_SOCIAL_TWITTER("&a• https://twitter.com/TheForgeStorm"),
    FS_SOCIAL_YOUTUBE("&b• https://youtube.com/channel/UCOupaY4xuutRjeHzlHH7seA"),

    BAR_BOTTOM("&8&l&m---------------------------------------------"),

    BAR_TUTORIAL("&8&l&m-----------------&r&l &l &l &9&lTutorial&l &l &l &8&l&m-----------------"),

    DISPLAY_TAB_HEADER("\n&8&m-------------------------------\n&r%s&7, thanks for playing on\n \n&r&6&l\u2605  ForgeStorm  \u2605&r\n&8&m-------------------------------&r\n "),
    DISPLAY_TAB_FOOTER("\n&8&m-------------------------------\n&bNews&7, &aForum&7, &eDiscord&7, &dShop &6@\n&r\n&r&9http://www.ForgeStorm.com\n&8&m-------------------------------"),

    GAME_BAR_ROLL("&8&l&m--------------------&r&8&l<[ &6&lRoll &8&l]>&8&l&m------------------"),
    ROLL("&7     &l%player%&8&l: &7Rolled a &n%s&r&7 out of &n%f&r&8."),
    ROLL_ERROR("&c  &l! &a&lPlease specify the maximum size of your dice roll."),
    ROLL_EXAMPLE("&7  &lExample&8&l: &r/roll 100"),
    ROLL_UNHEARD("&cNo one heard your message!"),

    NO_PERMISSION("&cYou do not have permission to do this.");

    private final String message;

    @Override
    public String toString() {
        return Text.color(message);
    }
}
