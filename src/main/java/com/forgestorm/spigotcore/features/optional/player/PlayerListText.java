package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

/**
 * Simple class to change the PlayerList header and footer text.
 */
public class PlayerListText implements FeatureOptional, LoadsConfig, Listener {

    private String header;
    private String footer;

    @Override
    public void onEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        for (Player player : Bukkit.getOnlinePlayers()) setPlayerListText(player);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        PlayerJoinEvent.getHandlerList().unregister(this);

        for (Player player : Bukkit.getOnlinePlayers())
            SpigotCore.PLUGIN.getTitleManager().setHeaderAndFooter(player, "", "");
    }

    @Override
    public void loadConfiguration() {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.PLAYER_LIST_TEXT.toString()));
        header = config.getString("PlayerListText.header");
        footer = config.getString("PlayerListText.footer");
    }

    private void setPlayerListText(Player player) {
        SpigotCore.PLUGIN.getTitleManager().setHeaderAndFooter(player, Text.color(header), Text.color(footer));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        setPlayerListText(event.getPlayer());
    }

}
