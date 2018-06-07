package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

/**
 * Simple class to broadcast custom player join and quit messages.
 */
public class PlayerGreeting implements FeatureOptional, LoadsConfig, Listener {

    private boolean showPersonalLoginMessage = false;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
    }

    @Override
    public void loadConfiguration() {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.PLAYER_GREETING.toString()));
        showPersonalLoginMessage = config.getBoolean("Settings.showPersonalLoginMessage");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player eventPlayer = event.getPlayer();

        event.setJoinMessage(""); // Cancel default message

        // Notify other players
        // If showPersonalLoginMessage is false, then we will show the player this join message.
        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            if (showPersonalLoginMessage && onlinePlayers.getUniqueId().equals(eventPlayer.getUniqueId())) continue;
            onlinePlayers.sendMessage(Greetings.BROADCAST_PLAYER_JOIN.toString().replace("%e", eventPlayer.getDisplayName()));
        }

        // Send personal message
        if (!showPersonalLoginMessage) return;
        eventPlayer.sendMessage("");
        eventPlayer.sendMessage(CenterChatText.centerChatMessage(Greetings.SERVER_TITLE.toString().replace("%s", "1.0")));
        eventPlayer.sendMessage("");
        eventPlayer.sendMessage(CenterChatText.centerChatMessage(Greetings.WEBSITE.toString()));
        eventPlayer.sendMessage("");
        eventPlayer.sendMessage(CenterChatText.centerChatMessage(Greetings.HELPFUL_COMMANDS.toString()));
        eventPlayer.sendMessage("");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(Greetings.BROADCAST_PLAYER_QUIT.toString().replace("%e", event.getPlayer().getDisplayName()));
    }

    private enum Greetings {

        SERVER_TITLE("&e&lForgeStorm: &r&lRPG MINIGAME SERVER %s"),
        WEBSITE("&7&n http://www.ForgeStorm.com/ "),
        HELPFUL_COMMANDS("&c/help &e/mainmenu &a/settings &b/playtime &d/lobby"),
        BROADCAST_PLAYER_JOIN("&a+ &7%e"),
        BROADCAST_PLAYER_QUIT("&c- &7%e");

        private final String message;

        Greetings(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return Text.color(message);
        }
    }
}
