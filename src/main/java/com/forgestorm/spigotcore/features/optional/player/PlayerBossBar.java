package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.ForgeStormCommand;
import com.forgestorm.spigotcore.features.InitCommands;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.display.BossBarUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple boss bar to show players additional information.
 * Shows a static message at the top of the players game.
 */
public class PlayerBossBar implements FeatureOptional, LoadsConfig, InitCommands, Listener {

    private String barText;
    private BossBarUtil bossBarUtil;

    @Override
    public List<ForgeStormCommand> registerAllCommands() {
        // TODO: REMOVE TEST COMMAND!! <---------------------------------------------------------------------------
        List<ForgeStormCommand> commands = new ArrayList<>();
        commands.add(new TestCommand());
        return commands;
    }

    @Override
    public void onEnable(boolean manualEnable) {
        bossBarUtil = new BossBarUtil(barText);
        bossBarUtil.setBossBarProgress(1);

        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        // For server reloads
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBarUtil.showBossBar(player);
        }
    }

    @Override
    public void onDisable(boolean manualDisable) {
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerKickEvent.getHandlerList().unregister(this);

        bossBarUtil.removeAllBossBar();
    }

    @Override
    public void loadConfiguration() {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.PLAYER_BOSS_BAR.toString()));
        barText = config.getString("BossBarText");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        bossBarUtil.showBossBar(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        bossBarUtil.removeBossBar(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        bossBarUtil.removeBossBar(event.getPlayer());
    }

}
