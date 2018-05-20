package com.forgestorm.spigotcore.util.text;

import org.bukkit.Bukkit;

public class Console {

    /**
     * Primarily used to send colored text messages to the console.
     *
     * @param string The text to send.
     */
    public static void sendMessage(String string) {
        Bukkit.getServer().getConsoleSender().sendMessage(Text.color(string));
    }

}
