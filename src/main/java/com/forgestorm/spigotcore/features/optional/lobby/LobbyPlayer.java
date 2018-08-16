package com.forgestorm.spigotcore.features.optional.lobby;

import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

/**
 * Provides very basic player setup.
 */
public class LobbyPlayer implements FeatureOptional, LoadsConfig {

    private boolean allowHunger;
    private boolean fixHealth;
    private int gameMode;
    private boolean clearInventory;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.hasMetadata("NPC")) continue;
            setupPlayer(player);
        }
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
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

    /**
     * Sets some very basic lobby attributes.
     *
     * @param player The player to setup.
     */
    private void setupPlayer(Player player) {
        player.setGameMode(GameMode.getByValue(gameMode));
        if (clearInventory) player.getInventory().clear();

        player.getInventory().setHeldItemSlot(0);

        if (!fixHealth) return;
        player.setHealth(20);
        player.setFoodLevel(20);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        setupPlayer(event.getPlayer());
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (allowHunger) return;
        if (!(event.getEntity() instanceof Player)) return;
        ((Player) event.getEntity()).setFoodLevel(20);
        event.setCancelled(true);
    }
}
