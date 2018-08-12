package com.forgestorm.spigotcore.util.text;

import org.bukkit.ChatColor;

public class Text {

    /**
     * Converts special characters in text into Minecraft client color codes.
     * <p>
     * This will give the messages color.
     *
     * @param message The message that needs to have its color codes converted.
     * @return Returns a colored message!
     */
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Takes colored messages and strips them of color code values.
     *
     * @param message The message to remove colors codes from.
     * @return Returns a message without colors.
     */
    public static String removeColor(String message) {
        return ChatColor.stripColor(message);
    }

    /**
     * This will make sure a string is not longer than 14 characters.
     * If it is, we will shorten the string.
     *
     * Note: Tarkan Scoreboard length is 42.
     *
     * @param input The string we want to trim.
     * @param maxWidth The max with allowed for the input.
     * @return The trimmed string.
     */
    public static String trimString(String input, int maxWidth) {
        Console.sendMessage("TarkanLobbyScoreboard - trimString()");

        // Check to see if the input length is greater than the maxWidth of characters.
        if (input.length() > maxWidth) {
            int amountOver = input.length() - maxWidth;
            return input.substring(0, input.length() - amountOver - 2) + "..";
        } else {
            // The input is less than 15 characters so it does not need to be trimmed.
            return input;
        }
    }
}
