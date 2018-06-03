package com.forgestorm.spigotcore;

import co.aikar.commands.PaperCommandManager;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.chat.EzImgMessage;
import com.forgestorm.spigotcore.features.optional.chat.GameTipAnnouncer;
import com.forgestorm.spigotcore.features.optional.chat.SimpleChat;
import com.forgestorm.spigotcore.features.optional.citizen.CitizenManager;
import com.forgestorm.spigotcore.features.optional.lobby.DoubleJump;
import com.forgestorm.spigotcore.features.optional.lobby.LobbyPlayer;
import com.forgestorm.spigotcore.features.optional.player.*;
import com.forgestorm.spigotcore.features.optional.rpg.mobs.MobManager;
import com.forgestorm.spigotcore.features.optional.world.HiddenPaths;
import com.forgestorm.spigotcore.features.optional.world.ServerSpawn;
import com.forgestorm.spigotcore.features.optional.world.WorldHologram;
import com.forgestorm.spigotcore.features.optional.world.WorldSettings;
import com.forgestorm.spigotcore.features.optional.world.lantern.Lantern;
import com.forgestorm.spigotcore.features.optional.world.loot.ChestLoot;
import com.forgestorm.spigotcore.features.optional.world.loot.DragonEggLoot;
import com.forgestorm.spigotcore.features.optional.world.loot.NewChestLoot;
import com.forgestorm.spigotcore.features.required.database.DatabaseConnectionManager;
import com.forgestorm.spigotcore.features.required.database.feature.FeatureDataManager;
import com.forgestorm.spigotcore.features.required.database.global.GlobalDataManager;
import com.forgestorm.spigotcore.features.required.featuretoggle.FeatureToggleManager;
import com.forgestorm.spigotcore.features.required.menu.MenuManager;
import com.forgestorm.spigotcore.features.required.world.regen.BlockRegenerationManager;
import com.forgestorm.spigotcore.features.required.world.worldobject.WorldObjectManager;
import com.forgestorm.spigotcore.util.text.Console;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * SpigotCore is our main plugin class for Bukkit/Spigot. This is the plugin start class.
 * <p>
 * RULES:
 * <ul>
 * <li> All {@link com.forgestorm.spigotcore.features.required.FeatureRequired} are started here.</li>
 * <li> All {@link FeatureOptional} features are added here.</li>
 * <li> {@link org.bukkit.command.CommandExecutor} are never registered here.</li>
 * <li> {@link java.util.EventListener} are never registered here.</li>
 * </ul>
 */
@Getter
public class SpigotCore extends JavaPlugin {

    public static SpigotCore PLUGIN;

    private final MenuManager menuManager = new MenuManager();
    private final DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
    private final GlobalDataManager globalDataManager = new GlobalDataManager();
    private final FeatureDataManager featureDataManager = new FeatureDataManager();
    private final BlockRegenerationManager blockRegenerationManager = new BlockRegenerationManager();
    private final WorldObjectManager worldObjectManager = new WorldObjectManager();
    private final FeatureToggleManager featureToggleManager = new FeatureToggleManager();

    private PaperCommandManager paperCommandManager;
    private TitleManagerAPI titleManager;

    /**
     * Called on plugin start/reload.
     */
    @Override
    public void onEnable() {
        Console.sendMessage(ChatColor.GOLD + "---------[ FS SpigotCore Initializing ]---------");
        PLUGIN = this;

        // Init needed APIs
        paperCommandManager = new PaperCommandManager(this);
        titleManager = (TitleManagerAPI) Bukkit.getServer().getPluginManager().getPlugin("TitleManager");

        // Init required features & maintain startup order
        menuManager.onServerStartup();
        databaseConnectionManager.onServerStartup();
        globalDataManager.onServerStartup();
        featureDataManager.onServerStartup();
        blockRegenerationManager.onServerStartup();
        worldObjectManager.onServerStartup();
        featureToggleManager.onServerStartup();

        // Init optional features last
        initOptionalFeatures();
    }

    /**
     * Called on plugin stop/reload.
     */
    @Override
    public void onDisable() {
        // Maintain shutdown order
        featureToggleManager.onServerShutdown();
        blockRegenerationManager.onServerShutdown();
        worldObjectManager.onServerShutdown();
        featureDataManager.onServerShutdown();
        globalDataManager.onServerShutdown();
        databaseConnectionManager.onServerShutdown();
        menuManager.onServerShutdown();
    }

    /**
     * In brief, FeatureOptional classes are decoupled plugin features
     * that can be toggled on/off at runtime. FeatureRequired classes
     * are required to start when server starts and stop when the server
     * stops. No exceptions.
     * <p>
     * RULES:
     * <ul>
     * <li>FeatureOptional classes cannot rely on each other. Must be 100% decoupled.</li>
     * <li>FeatureOptional classes can safely use RequiredFeature public methods.</li>
     * <li>FeatureRequired classes cannot use FeatureOptional classes or methods.</li>
     * </ul>
     * <p>
     * REFERENCE:
     * {@link FeatureOptional}
     * {@link FeatureToggleManager}
     * {@link com.forgestorm.spigotcore.features.required.FeatureRequired}
     */
    private void initOptionalFeatures() {
        List<FeatureOptional> features = new ArrayList<>();

        features.add(new GameTipAnnouncer());
        features.add(new PlayerCompassMenu());
        features.add(new PlayerScoreboardTeams());
        features.add(new PlayerNewChecker());
        features.add(new PlayerOperator());
        features.add(new PlayerBanKicker());
        features.add(new ServerSpawn());
        features.add(new SimpleChat());
        features.add(new LobbyPlayer());
        features.add(new PlayerGreeting());
        features.add(new PlayerListText());
        features.add(new PlayerBossBar());
        features.add(new CitizenManager());
        features.add(new WorldSettings());
        features.add(new NewChestLoot());
        features.add(new ChestLoot());
        features.add(new DragonEggLoot());
        features.add(new HiddenPaths());
        features.add(new MobManager());
        features.add(new DoubleJump());
        features.add(new Lantern());
        features.add(new WorldHologram());
        features.add(new EzImgMessage());

        // Finally, add all features
        featureToggleManager.addFeatures(features);
    }
}