package com.forgestorm.spigotcore.util.text;

import org.bukkit.ChatColor;

public class ProgressBarString {

    public static String buildBar(double percent) {

        int maxBars = 20;
        String low = ChatColor.RED + "" + ChatColor.BOLD;
        String mid = ChatColor.YELLOW + "" + ChatColor.BOLD;
        String high = ChatColor.GREEN + "" + ChatColor.BOLD;
        String empty = ChatColor.GRAY + "" + ChatColor.BOLD;

        int coloredBars = 0;
        String barColor = "";
        String fullBar = "";
        String emptyBar = empty + "";

        //Make positive bars.
        for (int i = 0; i < percent; i += 5) {
            fullBar = fullBar.concat("|");
            coloredBars++;
        }

        //Make remaining bars.
        for (int i = 0; i < maxBars - coloredBars; i++) {
            emptyBar = emptyBar.concat("|");
        }

        //Decided bar color.
        if (percent <= 33) {
            barColor = low;
        } else if (percent > 33 && percent <= 66) {
            barColor = mid;
        } else if (percent > 66 && percent <= 100) {
            barColor = high;
        }

        return barColor + fullBar + emptyBar + ChatColor.RESET;
    }
}
