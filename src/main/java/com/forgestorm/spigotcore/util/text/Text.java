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
}
