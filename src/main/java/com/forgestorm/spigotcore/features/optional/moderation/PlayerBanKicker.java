package com.forgestorm.spigotcore.features.optional.moderation;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerBanKicker implements FeatureOptional, Listener {

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
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
                event.getPlayer().kickPlayer(Text.color("&c" + event.getPlayer().getName() + " &8- &7Permanently Banned\n\n"
                        + "&cReason&8: &7<ban_reason>\n\n"
                        + "&8Submit ban appeals here:\n"
                        + "&eForum&8: &cwww.forgestorm.com\n"
                        + "&eDiscord&8: &chttps://discord.gg/MMuDKFD"));
            }
        }.runTaskLater(SpigotCore.PLUGIN, 20);
    }
}
