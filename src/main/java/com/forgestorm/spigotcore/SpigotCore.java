package com.forgestorm.spigotcore;

import com.forgestorm.spigotcore.citizen.CitizenManager;
import com.forgestorm.spigotcore.database.DatabaseManager;
import com.forgestorm.spigotcore.database.ProfileManager;
import com.forgestorm.spigotcore.feature.*;
import com.forgestorm.spigotcore.player.*;
import com.forgestorm.spigotcore.rpg.mobs.MobManager;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.world.ServerSpawn;
import com.forgestorm.spigotcore.world.WorldSettings;
import com.forgestorm.spigotcore.world.blockregen.BlockRegenerationManager;
import com.forgestorm.spigotcore.world.blockregen.HiddenPaths;
import com.forgestorm.spigotcore.world.lantern.Lantern;
import com.forgestorm.spigotcore.world.loot.ChestLoot;
import com.forgestorm.spigotcore.world.loot.DragonEggLoot;
import com.forgestorm.spigotcore.world.loot.NewChestLoot;
import com.forgestorm.spigotcore.world.worldobject.WorldObjectManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SpigotCore extends JavaPlugin {

    /**
     * Instance of the main plugin class accessible to the entire plugin.
     */
    public static SpigotCore PLUGIN;

    /**
     * Contains a list of all SpigotCore features that were turned on
     * in the fileConfiguration class. If a feature was marked as
     * disabled (or false) then it is not loaded or placed in this map.
     * <p>
     * Note: Our goal is to never have any features directly talk to
     * each other and to never access this map except to load and
     * unload our core features.
     * </p>
     */
    private final Map<Class, FeatureOptional> features = new HashMap<>();

    /**
     * A delay created to make sure all tasks start X amount of time
     * after the server is fully loaded up. This is done mainly to give
     * the WorldSettings feature (if enabled) enough time to clear all
     * world entities without effecting ones we may spawn in.
     * <p>
     * Note: 20 ticks is 1 second.
     * Example: 20 ticks * 5 = 5 seconds.
     */
    public static final int FEATURE_TASK_START_DELAY = 20 * 5;

    private DatabaseManager databaseManager;
    private ProfileManager profileManager;
    private BlockRegenerationManager blockRegenerationManager;
    private WorldObjectManager worldObjectManager;

    @Override
    public void onEnable() {
        Console.sendMessage(ChatColor.GOLD + "---------[ SpigotCore Initializing ]---------");
        PLUGIN = this;

        // First, start these required features
        databaseManager = new DatabaseManager();
        databaseManager.onEnable();
        profileManager = new ProfileManager();
        profileManager.onEnable();
        blockRegenerationManager = new BlockRegenerationManager();
        blockRegenerationManager.onEnable();
        worldObjectManager = new WorldObjectManager();
        worldObjectManager.onEnable();

        // Second, read optional features
        addFeature(new ServerSpawn());
        addFeature(new SimpleChat());
        addFeature(new UsergroupChat());
        addFeature(new LobbyPlayer());
        addFeature(new PlayerGreeting());
        addFeature(new PlayerListText());
        addFeature(new PlayerBossBar());
        addFeature(new CitizenManager());
        addFeature(new WorldSettings());
        addFeature(new NewChestLoot());
        addFeature(new ChestLoot());
        addFeature(new DragonEggLoot());
        addFeature(new HiddenPaths());
        addFeature(new MobManager());
        addFeature(new DoubleJump());
        addFeature(new Lantern());
    }

    @Override
    public void onDisable() {
        // First disable all optional features
        disableFeatures();
        features.clear();

        // Lastly disable all required features
        blockRegenerationManager.onDisable();
        worldObjectManager.onDisable();
        profileManager.onDisable();
        databaseManager.onDisable();
    }

    /**
     * This will add features or game mechanics to be turned on by our main plugin.
     * In order for this to work correctly, we need to make sure that the class name
     * and the name in the config file are exactly the same. Otherwise this will not
     * work correctly.
     * <p>
     * Will attempt to automatically:
     * - Load configuration files
     * - Start async BukkitRunnables
     *
     * @param featureOptional The feature we are going to attempt to turn on.
     */
    private void addFeature(FeatureOptional featureOptional) {

        String featureName = featureOptional.getClass().getSimpleName();
        String configSection = "Features." + featureName;

        // Lets check if the feature was disabled or if their was perhaps a spelling error that
        // caused the feature to be disabled.
        if (!SpigotCore.PLUGIN.getConfig().getBoolean(configSection)) {
            if (SpigotCore.PLUGIN.getConfig().get(configSection) != null) {
                // The feature was disabled in the configuration file.
                Console.sendMessage(ChatColor.RED + "Disabled: " + featureName);
            } else {
                // The features main class name and the name in the config do not match.
                Console.sendMessage(ChatColor.DARK_RED + "FEATURE NOT IN CONFIG: " + featureName + " (check config spelling)");
            }
            return;
        }

        initFeature(featureOptional);
    }

    /**
     * Enables a given feature. Maintain operation order here.
     *
     * @param featureOptional The feature to enable.
     */
    private void initFeature(FeatureOptional featureOptional) {
        String featureName = featureOptional.getClass().getSimpleName();
        Console.sendMessage(ChatColor.GREEN + "Enabling: " + featureName);

        features.put(featureOptional.getClass(), featureOptional);

        // Load configurations first
        if (featureOptional instanceof LoadsConfig) {
            ((LoadsConfig) featureOptional).loadConfiguration();
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] File configuration loaded.");
        }

        // Get blank database templates
        if (featureOptional instanceof AbstractDatabaseFeature) {
            databaseManager.addDatabaseTemplate(
                    (AbstractDatabaseFeature) featureOptional,
                    ((AbstractDatabaseFeature) featureOptional).getBlankDatabaseTemplate());
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Database template loaded.");
        }

        // Next enable the feature.
        featureOptional.onEnable();

        // Finally, start async BukkitRunnables
        if (featureOptional instanceof AsyncFeature) {
            ((AsyncFeature) featureOptional).enableAsync();
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Async runnable loaded.");
        }
    }

    /**
     * This will disable any features that are currently enabled.
     * <p>
     * Will attempt to automatically:
     * - Save configuration files
     * - Cancel async BukkitRunnables
     */
    private void disableFeatures() {
        for (FeatureOptional featureOptional : features.values()) {

            String featureName = featureOptional.getClass().getSimpleName();
            Console.sendMessage(ChatColor.GREEN + "Disabling: " + featureName);

            // Stop async BukkitRunnables
            if (featureOptional instanceof AsyncFeature) ((AsyncFeature) featureOptional).cancel();

            // Save configuration files
            if (featureOptional instanceof SavesConfig) {
                ((SavesConfig) featureOptional).saveConfiguration();
                Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Configuration saved.");
            }

            // Finally disable the feature now!
            featureOptional.onDisable();
        }
    }
}