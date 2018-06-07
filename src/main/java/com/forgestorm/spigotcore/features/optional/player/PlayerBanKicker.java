package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerBanKicker implements FeatureOptional, Listener {

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        GlobalProfileDataLoadEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onGlobalProfileDataLoad(GlobalProfileDataLoadEvent event) {
        if (!event.getGlobalPlayerData().getPlayerAccount().isBanned()) return;

        new BukkitRunnable() {

            @Override
            public void run() {
                event.getPlayer().kickPlayer(ChatColor.RED + "You are banned! Visit forums to submit an appeal.");
            }
        }.runTaskLater(SpigotCore.PLUGIN, 20);
    }
}
