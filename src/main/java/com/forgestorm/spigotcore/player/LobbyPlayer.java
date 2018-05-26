package com.forgestorm.spigotcore.player;

import com.forgestorm.spigotcore.feature.FeatureOptional;
import com.forgestorm.spigotcore.feature.LoadsConfig;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

/**
 * Provides very basic player setup.
 */
public class LobbyPlayer implements FeatureOptional, LoadsConfig, Listener {

    private boolean allowHunger;
    private boolean fixHealth;
    private int gameMode;
    private boolean clearInventory;

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onDisable() {
        PlayerJoinEvent.getHandlerList().unregister(this);
        FoodLevelChangeEvent.getHandlerList().unregister(this);
    }

    @Override
    public void loadConfiguration() {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.LOBBY_PLAYER.toString()));
        String path = "Settings.";
        allowHunger = config.getBoolean(path + "allowHunger");
        fixHealth = config.getBoolean(path + "fixHealth");
        gameMode = config.getInt(path + "gameMode");
        clearInventory = config.getBoolean(path + "clearInventory");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.getByValue(gameMode));
        if (clearInventory) player.getInventory().clear();

        if (!fixHealth) return;
        player.setHealth(20);
        player.setFoodLevel(20);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (allowHunger) return;
        if (!(event.getEntity() instanceof Player)) return;
        ((Player) event.getEntity()).setFoodLevel(20);
        event.setCancelled(true);
    }
}
