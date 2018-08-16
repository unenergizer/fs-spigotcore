package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.file.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class DeletePlayerWorldData implements FeatureOptional {

    // TODO : Remove player data from "Game Worlds"

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        for (World world : Bukkit.getWorlds()) {
            FileUtil.removeDirectory(new File(world.getName()
                    + File.separator
                    + "playerdata").toPath());
        }
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        System.out.print(" --- Removing player data from worlds! ---");
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    File file = new File(world.getName()
                            + File.separator
                            + "playerdata"
                            + File.separator
                            + event.getPlayer().getUniqueId().toString()
                            + ".dat");
                    file.delete();
                }
            }
        }.runTaskLater(SpigotCore.PLUGIN, 20 * 3);
    }
}
