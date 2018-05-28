package com.forgestorm.spigotcore.player;

import com.forgestorm.spigotcore.feature.FeatureOptional;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.feature.FeatureShutdown;
import com.forgestorm.spigotcore.util.text.Text;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Simple class to change the PlayerList header and footer text.
 */
public class PlayerListText implements FeatureOptional, FeatureShutdown, Listener {

    private static final String PLAYER_LIST_HEADER = "\n&e&lForgeStorm: &r&lRPG MINIGAME SERVER\n";
    private static final String PLAYER_LIST_FOOTER = "\n&7 www.ForgeStorm.com\n&c /help &e/mainmenu &a/settings &b/playtime &d/lobby ";

    private TitleManagerAPI titleManager = (TitleManagerAPI) Bukkit.getServer().getPluginManager().getPlugin("TitleManager");

    @Override
    public void onEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        for (Player player : Bukkit.getOnlinePlayers()) setPlayerListText(player);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        for (Player player : Bukkit.getOnlinePlayers()) titleManager.setHeaderAndFooter(player, "", "");
        PlayerJoinEvent.getHandlerList().unregister(this);
    }

    @Override
    public void onServerShutdown() {
        titleManager = null;
    }

    private void setPlayerListText(Player player) {
        titleManager.setHeaderAndFooter(
                player,
                Text.color(PLAYER_LIST_HEADER),
                Text.color(PLAYER_LIST_FOOTER));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        setPlayerListText(event.getPlayer());
    }
}
