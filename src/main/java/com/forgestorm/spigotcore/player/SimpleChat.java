package com.forgestorm.spigotcore.player;

import com.forgestorm.spigotcore.FeatureOptional;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.UserGroup;
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
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onDisable() {
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        ChatColor messageColor = ChatColor.GRAY;
        String tag = "";

        if (event.getPlayer().isOp()) {
            tag = tag + UserGroup.ADMINISTRATOR.getUserGroupPrefix();
            messageColor = ChatColor.YELLOW;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(tag + ChatColor.GRAY + event.getPlayer().getName() + ChatColor.DARK_GRAY + ": " + messageColor + event.getMessage());
        }
    }
}
