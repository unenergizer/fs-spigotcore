package com.forgestorm.spigotcore.features.optional.chat;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
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
    private Sound sound;
    private float soundVolume;
    private float soundPitch;
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
        sound = Sound.valueOf(config.getString(path + "sound.enum"));
        soundVolume = (float) config.getDouble(path + "sound.volume");
        soundPitch = (float) config.getDouble(path + "sound.pitch");
        timeBetween = config.getInt(path + "timeBetween");
        gameTipList = config.getStringList(path + "gameTipList");
    }

    /**
     * This starts the thread that will loop over and over displaying gameTipList and
     * other useful information to the player.
     */
    private void displayGameTips() {
        if (gameTipList.isEmpty()) return;
        if (Bukkit.getOnlinePlayers().isEmpty()) return;

        String gameTip = gameTipList.get(lastTipIndex);
        sendPlayerTips(Text.color("&e&lTip &e#" + Integer.toString(lastTipIndex + 1) + "&8&l: &f" + gameTip));

        lastTipIndex++;
        if (lastTipIndex >= gameTipList.size()) lastTipIndex = 0;
    }

    /**
     * Sends the tip directly to the player.
     *
     * @param message The message to send to the player.
     */
    private void sendPlayerTips(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("");
            player.sendMessage(message);
            player.sendMessage("");
            player.playSound(player.getEyeLocation(), sound, soundVolume, soundPitch);
        }
    }
}

