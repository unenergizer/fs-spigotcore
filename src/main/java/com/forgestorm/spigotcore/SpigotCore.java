package com.forgestorm.spigotcore;

import co.aikar.commands.PaperCommandManager;
import com.forgestorm.spigotcore.bungeecord.BungeeCord;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.chat.EzImgMessage;
import com.forgestorm.spigotcore.features.optional.chat.GameTipAnnouncer;
import com.forgestorm.spigotcore.features.optional.chat.SimpleChat;
import com.forgestorm.spigotcore.features.optional.citizen.CitizenManager;
import com.forgestorm.spigotcore.features.optional.commands.PlayMidi;
import com.forgestorm.spigotcore.features.optional.commands.Roll;
import com.forgestorm.spigotcore.features.optional.discord.DiscordMessageRelay;
import com.forgestorm.spigotcore.features.optional.gadget.jukebox.JazzyJukebox;
import com.forgestorm.spigotcore.features.optional.lobby.DoubleJump;
import com.forgestorm.spigotcore.features.optional.lobby.LobbyPlayer;
import com.forgestorm.spigotcore.features.optional.minigame.MinigameFramework;
import com.forgestorm.spigotcore.features.optional.moderation.PlayerBanKicker;
import com.forgestorm.spigotcore.features.optional.moderation.PlayerOperator;
import com.forgestorm.spigotcore.features.optional.player.*;
import com.forgestorm.spigotcore.features.optional.realm.RealmManager;
import com.forgestorm.spigotcore.features.optional.rpg.ItemDatabase;
import com.forgestorm.spigotcore.features.optional.rpg.armor.ArmorManager;
import com.forgestorm.spigotcore.features.optional.rpg.mobs.MobManager;
import com.forgestorm.spigotcore.features.optional.skill.blockbreak.Farming;
import com.forgestorm.spigotcore.features.optional.skill.blockbreak.Mining;
import com.forgestorm.spigotcore.features.optional.skill.blockbreak.WoodCutting;
import com.forgestorm.spigotcore.features.optional.skill.fishing.FishingSkill;
import com.forgestorm.spigotcore.features.optional.world.*;
import com.forgestorm.spigotcore.features.optional.world.gameworld.GameWorldManager;
import com.forgestorm.spigotcore.features.optional.world.lantern.NewLantern;
import com.forgestorm.spigotcore.features.optional.world.loot.ChestLoot;
import com.forgestorm.spigotcore.features.optional.world.loot.DragonEggLoot;
import com.forgestorm.spigotcore.features.required.core.ScoreboardManager;
import com.forgestorm.spigotcore.features.required.database.DatabaseConnectionManager;
import com.forgestorm.spigotcore.features.required.database.feature.FeatureDataManager;
import com.forgestorm.spigotcore.features.required.database.global.GlobalDataManager;
import com.forgestorm.spigotcore.features.required.discord.DiscordManager;
import com.forgestorm.spigotcore.features.required.featuretoggle.FeatureToggleManager;
import com.forgestorm.spigotcore.features.required.menu.MenuManager;
import com.forgestorm.spigotcore.features.required.player.AccountManager;
import com.forgestorm.spigotcore.features.required.player.EconomyManager;
import com.forgestorm.spigotcore.features.required.world.TeleportManager;
import com.forgestorm.spigotcore.features.required.world.loader.WorldManager;
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

    private final DiscordManager discordManager = new DiscordManager();
    private final BungeeCord bungeeCord = new BungeeCord();
    private final ScoreboardManager scoreboardManager = new ScoreboardManager();
    private final AccountManager accountManager = new AccountManager();
    private final EconomyManager economyManager = new EconomyManager();
    private final WorldManager worldManager = new WorldManager();
    private final MenuManager menuManager = new MenuManager();
    private final DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManager();
    private final GlobalDataManager globalDataManager = new GlobalDataManager();
    private final FeatureDataManager featureDataManager = new FeatureDataManager();
    private final BlockRegenerationManager blockRegenerationManager = new BlockRegenerationManager();
    private final WorldObjectManager worldObjectManager = new WorldObjectManager();
    private final FeatureToggleManager featureToggleManager = new FeatureToggleManager();
    private final TeleportManager teleportManager = new TeleportManager();

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
        paperCommandManager.enableUnstableAPI("help");
        titleManager = (TitleManagerAPI) Bukkit.getServer().getPluginManager().getPlugin("TitleManager");

        // Init required features & maintain startup order
        discordManager.startup();
        bungeeCord.startup();
        scoreboardManager.startup();
        accountManager.startup();
        economyManager.startup();
        worldManager.startup();
        menuManager.startup();
        databaseConnectionManager.startup();
        globalDataManager.startup();
        featureDataManager.startup();
        blockRegenerationManager.startup();
        worldObjectManager.startup();
        featureToggleManager.startup();
        teleportManager.startup();

        // Init optional features last
        initOptionalFeatures();
    }

    /**
     * Called on plugin stop/reload.
     */
    @Override
    public void onDisable() {
        // Maintain shutdown order
        teleportManager.shutdown();
        featureToggleManager.shutdown();
        blockRegenerationManager.shutdown();
        worldObjectManager.shutdown();
        featureDataManager.shutdown();
        globalDataManager.shutdown();
        databaseConnectionManager.shutdown();
        menuManager.shutdown();
        worldManager.shutdown();
        economyManager.shutdown();
        accountManager.shutdown();
        scoreboardManager.shutdown();
        bungeeCord.shutdown();
        discordManager.shutdown();
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

        features.add(new DiscordMessageRelay());
        features.add(new PlayerBreakables());
        features.add(new JazzyJukebox());
        features.add(new MinigameFramework());
        features.add(new PlayMidi());
        features.add(new DeletePlayerWorldData());
        features.add(new PersistentInventory());
        features.add(new FishingSkill());
        features.add(new WoodCutting());
        features.add(new Farming());
        features.add(new Mining());
        features.add(new ItemDatabase());
        features.add(new GameWorldManager());
        features.add(new ArmorManager());
        features.add(new DragonEggLoot());
        features.add(new BasicBuildProtection());
        features.add(new Roll());
        features.add(new HelpBookTest());
        features.add(new AnvilTest());
        features.add(new RealmManager());
        features.add(new GameTipAnnouncer());
        features.add(new PlayerCompassMenu());
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
        features.add(new ChestLoot());
        features.add(new HiddenPaths());
        features.add(new MobManager());
        features.add(new DoubleJump());
        features.add(new NewLantern());
        features.add(new WorldHologram());
        features.add(new EzImgMessage());

        // Finally, add all features
        featureToggleManager.addFeatures(features);
    }
}