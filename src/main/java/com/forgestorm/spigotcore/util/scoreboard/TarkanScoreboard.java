package com.forgestorm.spigotcore.util.scoreboard;

public class TarkanScoreboard {

//    private final UserManager plugin;
//
//    private TitleManagerAPI titleManager = (TitleManagerAPI) Bukkit.getServer().getPluginManager().getPlugin("TitleManager");
//
//    public TarkanScoreboard(UserManager plugin) {
//        this.plugin = plugin;
//        api = plugin.getTitleManagerAPI();
//    }
//
//    public void giveScoreboard(Player player) {
//        api.giveScoreboard(player);
//        updateScoreboard(player);
//    }
//
//    public boolean hasScoreboard(Player player) {
//        return api.hasScoreboard(player);
//    }
//
//    public void removeScoreboard(Player player) {
//        api.removeScoreboard(player);
//    }
//
//    public void onServerShutdown() {
//        for (Player players : Bukkit.getOnlinePlayers()) {
//            if (hasScoreboard(players)) {
//                removeScoreboard(players);
//            }
//        }
//    }
//
//    public void updateScoreboard(Player player) {
//        PlayerProfileData profileData = plugin.getProfileManager().getPlayerProfileData(player);
//
//        //Set Title
//        api.setScoreboardTitle(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "FORGESTORM");
//
//        //Set Contents
//        api.setScoreboardValue(player, 1, ChatColor.GREEN + " ");
//        api.setScoreboardValue(player, 2, ChatColor.LIGHT_PURPLE + "XP: " + ChatColor.RESET + profileData.getExpPercent() + "%");
//        api.setScoreboardValue(player, 3, ChatColor.AQUA + "Level: " + ChatColor.RESET + profileData.getPlayerLevel());
//        api.setScoreboardValue(player, 4, ChatColor.GREEN + "  ");
//        api.setScoreboardValue(player, 5, ChatColor.GREEN + "Gems: " + ChatColor.RESET + profileData.getCurrency());
//        api.setScoreboardValue(player, 6, ChatColor.GREEN + "eCash: " + ChatColor.RESET + profileData.getPremiumCurrency());
//    }
}
