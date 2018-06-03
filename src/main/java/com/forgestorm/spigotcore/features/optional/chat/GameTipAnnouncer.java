package com.forgestorm.spigotcore.features.optional.chat;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.List;

public class GameTipAnnouncer implements FeatureOptional, LoadsConfig {

    private List<String> gameTipList;
    private int timeBetween;
    private int lastTipIndex = 0;
    private BukkitTask tipAnnouncer;

    @Override
    public void onEnable(boolean manualEnable) {
        tipAnnouncer = new BukkitRunnable() {
            @Override
            public void run() {
                displayGameTips();
            }
        }.runTaskTimerAsynchronously(SpigotCore.PLUGIN, 20 * 5, timeBetween * 20);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        tipAnnouncer.cancel();
    }

    @Override
    public void loadConfiguration() {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.GAME_TIPS.toString()));
        String path = "GameTips.";
        timeBetween = config.getInt(path + "timeBetween");
        gameTipList = config.getStringList(path + "gameTipList");
    }

    /**
     * This starts the thread that will loop over and over displaying gameTipList and
     * other useful information to the player.
     */
    private void displayGameTips() {
        if (gameTipList.isEmpty()) return;
        String gameTip = gameTipList.get(lastTipIndex);

        sendPlayerTips(new StringBuilder()
                .append(ChatColor.YELLOW)
                .append(ChatColor.BOLD)
                .append("Tip")
                .append(ChatColor.YELLOW)
                .append(" #")
                .append(Integer.toString(lastTipIndex + 1))
                .append(ChatColor.DARK_GRAY)
                .append(ChatColor.BOLD)
                .append(": ")
                .append(ChatColor.WHITE)
                .append(gameTip)
                .append(ChatColor.DARK_GRAY)
                .append(".")
                .toString());

        lastTipIndex++;
        if (lastTipIndex >= gameTipList.size()) lastTipIndex = 0;
    }

    /**
     * Sends the tip directly to the player.
     *
     * @param message The message to send to the player.
     */
    private void sendPlayerTips(String message) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.sendMessage(message);
            players.playSound(players.getEyeLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, .5F, .2f);
            players.playSound(players.getEyeLocation(), Sound.BLOCK_CLOTH_BREAK, .5F, 1f);
        }
    }
}

