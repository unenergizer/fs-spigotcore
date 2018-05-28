package com.forgestorm.spigotcore.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.UserGroup;
import com.forgestorm.spigotcore.feature.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.util.text.Console;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Provides very basic chat formatting.
 */
public class UsergroupChat extends AbstractDatabaseFeature implements Listener {

    public UsergroupChat() {
        super(new UsergroupData());
    }

    @Override
    public void onEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!isProfileDataLoaded(player)) loadProfileData(player);

        UsergroupData databaseTemplate = (UsergroupData) getProfileData(event.getPlayer());
        databaseTemplate.setSomeMessage(event.getPlayer().getDisplayName());

        Console.sendMessage(Boolean.toString(databaseTemplate.isSomeBool()));

        saveProfileData(event.getPlayer());

        event.setCancelled(true);
        ChatColor messageColor = ChatColor.GRAY;
        String tag = "";

        if (event.getPlayer().isOp()) {
            tag = tag + UserGroup.ADMINISTRATOR.getUserGroupPrefix();
            messageColor = ChatColor.YELLOW;
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.sendMessage(tag + ChatColor.GRAY + player.getName() + ChatColor.DARK_GRAY + ": " + messageColor + event.getMessage());
        }
    }
}
