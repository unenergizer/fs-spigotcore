package com.forgestorm.spigotcore.util.player;

import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class PlayerUtil {

    /**
     * Gets the players ping.
     *
     * @param player The player to ping.
     * @return Returns the players ping. If null, we return -1.
     */
    public static int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Gets the player supplied in the command.
     *
     * @param player     The player who ran this command.
     * @param playerName The player to find.
     * @return Player object if player is online, otherwise return null.
     */
    public static Player findPlayer(Player player, String playerName) {
        Player otherPlayer = Bukkit.getPlayer(playerName);

        if (otherPlayer == null) {
            player.sendMessage(Text.color("&cWe cannot find a player named &e" + playerName + "&c."));
            player.sendMessage(Text.color("&cCheck name spelling. &e" + playerName + "&c must be on the same server."));
            CommonSounds.ACTION_FAILED.play(player);
            return null;
        }

        return otherPlayer;
    }
}
