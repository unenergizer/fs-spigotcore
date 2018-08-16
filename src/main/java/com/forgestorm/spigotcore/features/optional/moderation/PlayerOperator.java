package com.forgestorm.spigotcore.features.optional.moderation;

import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerOperator implements FeatureOptional, Listener {

    @Override
    public void onFeatureEnable(boolean manualEnable) {
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
    }

    @EventHandler
    public void onGlobalProfileDataLoad(GlobalProfileDataLoadEvent event) {
        event.getPlayer().setOp(event.getGlobalPlayerData().getPlayerAccount().isAdmin());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().setOp(false);
    }
}
