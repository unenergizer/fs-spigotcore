package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.events.ProfileDataLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerOperator implements FeatureOptional, Listener {

    @Override
    public void onEnable(boolean manualEnable) {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        ProfileDataLoadEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerKickEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onGlobalProfileDataLoad(ProfileDataLoadEvent event) {
        event.getPlayer().setOp(event.getGlobalPlayerData().getPlayerAccount().isAdmin());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().setOp(false);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.getPlayer().setOp(false);
    }
}
