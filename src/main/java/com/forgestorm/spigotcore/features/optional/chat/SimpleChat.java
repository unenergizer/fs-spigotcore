package com.forgestorm.spigotcore.features.optional.chat;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Provides very basic chat formatting.
 */
public class SimpleChat implements FeatureOptional, Listener {

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        if (SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(event.getPlayer()) == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can not do that until your profile has loaded.");
            return;
        }

        PlayerRanks playerRanks = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(event.getPlayer()).getPlayerAccount().getRank();

        for (Player player : Bukkit.getOnlinePlayers())
            player.sendMessage(playerRanks.getUsernamePrefix() + ChatColor.GRAY + event.getPlayer().getName() + ChatColor.DARK_GRAY + ": " + playerRanks.getChatColor() + event.getMessage());
    }
}
