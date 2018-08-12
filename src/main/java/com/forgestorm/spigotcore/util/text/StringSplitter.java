package com.forgestorm.spigotcore.util.text;

import org.bukkit.ChatColor;

import java.util.ArrayList;

public class StringSplitter {

    public static ArrayList<String> split(String string, int maxLength) {
        String[] split = string.split(" ");
        string = "";
        ArrayList<String> newString = new ArrayList<>();
        for (String aSplit : split) {
            string += (string.length() == 0 ? "" : " ") + aSplit;
            if (ChatColor.stripColor(string).length() > maxLength) {
                newString
                        .add((newString.size() > 0 ? ChatColor.getLastColors(newString.get(newString.size() - 1)) : "") + string);
                string = "";
            }
        }
        if (string.length() > 0)
            newString.add((newString.size() > 0 ? ChatColor.getLastColors(newString.get(newString.size() - 1)) : "") + string);
        return newString;
    }
}
