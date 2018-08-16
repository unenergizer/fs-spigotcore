package com.forgestorm.spigotcore.features.required.featuretoggle;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.features.InitCommands;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.SavesConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.ShutdownTask;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.features.required.database.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.util.text.Console;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * INFO:
 * Used to enable and disable {@link FeatureOptional} plugin features as needed.
 * <p>
 * COMMANDS:
 * <ul>
 * <li>/feature list = List all features registered with toggle manager.</li>
 * <li>/feature enable FeatureName = Safely starts a feature wither or not it was disabled on server start.</li>
 * <li>/feature disable FeatureName = Safely stops a feature.</li>
 * <li>/feature reload FeatureName = Safely stops and starts (reloads) a feature.</li>
 * </ul>
 * <p>
 * RULES:
 * <ul>
 * <li>Features are not allowed to interact with each other. No exceptions!</li>
 * </ul>
 */
public class FeatureToggleManager extends FeatureRequired {

    /**
     * Contains a list of all SpigotCore features that were added in
     * this {@link SpigotCore}.
     * Key {@link FeatureOptional}
     * Value: {@link FeatureData}
     * <p>
     * Note: Our goal is to never have any features directly talk to
     * each other and to never access this map except to load and
     * unload our core features.
     * </p>
     */
    @Getter
    private final Map<Class, FeatureData> featureDataMap = new HashMap<>();

    /**
     * A delay created to make sure all tasks start X amount of scheduler
     * after the server is fully loaded up. This is done mainly to give
     * the WorldSettings features (if enabled) enough scheduler to clear all
     * world entities without effecting ones we may spawn in.
     * <p>
     * Note: 20 ticks is 1 second.
     * Example: 20 ticks * 5 = 5 seconds.
     */
    public static final int FEATURE_TASK_START_DELAY = 20 * 5;

    /**
     * Provide easy access to PaperCommandManager
     */
//    private final PaperCommandManager paperCommandManager = SpigotCore.PLUGIN.getPaperCommandManager();
    @Override
    public void initFeatureStart() {
        FeatureToggleCommand featureToggleCommand = new FeatureToggleCommand(this);
        featureToggleCommand.setupCommand(SpigotCore.PLUGIN.getPaperCommandManager());
        featureToggleCommand.enableCommand();
    }

    @Override
    public void initFeatureClose() {
        shutdownAllFeatures();
        featureDataMap.clear();
    }

    /**
     * Find the FeatureData for this FeatureOptional class.
     *
     * @param featureOptional The reference we will search for.
     * @return {@link FeatureData}
     */
    private FeatureData getFeatureData(FeatureOptional featureOptional) {
        return featureDataMap.get(featureOptional.getClass());
    }

    /**
     * This will add featureDataMap or game mechanics to be turned on by our main plugin.
     * In order for this to work correctly, we need to make sure that the class name
     * and the name in the config file are exactly the same. Otherwise this will not
     * work correctly.
     * <p>
     * Will attempt to automatically:
     * - Load configuration files
     * - Start async BukkitRunnables
     *
     * @param featureOptionalList A list of features we are going to attempt to turn on.
     */
    public void addFeatures(List<FeatureOptional> featureOptionalList) {
        Console.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Total features loaded: " + featureOptionalList.size());

        int enabledFeatures = 0;
        for (FeatureOptional featureOptional : featureOptionalList) {
            String featureName = featureOptional.getClass().getSimpleName();
            String configSection = "Features." + featureName;
            boolean enabledInConfig = true;

            // Lets check if the features was disabled or if their was perhaps a spelling error in the config that
            // caused the features to be disabled.
            if (!SpigotCore.PLUGIN.getConfig().getBoolean(configSection)) {
                if (SpigotCore.PLUGIN.getConfig().get(configSection) != null) {
                    // The features was disabled in the configuration file.
                    Console.sendMessage(ChatColor.RED + "Disabled: " + featureName);
                } else {
                    // The featureDataMap main class name and the name in the config do not match.
                    Console.sendMessage(ChatColor.DARK_RED + "FEATURE NOT IN CONFIG: " + featureName + " (check config spelling)");
                }
                enabledInConfig = false;
            } else {
                enabledFeatures++;
            }
            featureDataMap.put(featureOptional.getClass(), new FeatureData(featureOptional.getClass(), featureOptional, enabledInConfig));
        }

        Console.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Total features starting: " + enabledFeatures);

        // Start all enabled features.
        for (FeatureData featureData : featureDataMap.values()) {
            if (featureData.isEnabledInConfig) enableFeature(featureData.featureOptional, false);
        }
    }

    /**
     * Enables a given features. Maintain operation order here.
     *
     * @param featureOptional The features to enable.
     * @param manualEnable    False if the feature was enabled by the server. True otherwise.
     */
    void enableFeature(FeatureOptional featureOptional, boolean manualEnable) {
        FeatureData featureData = getFeatureData(featureOptional);
        String featureName = featureOptional.getClass().getSimpleName();

        if (featureData.isEnabled) return;
        featureData.isEnabled = true;

        Console.sendMessage(ChatColor.GREEN + "Enabling: " + featureName);

        // Load configurations first
        if (featureOptional instanceof LoadsConfig) {
            ((LoadsConfig) featureOptional).loadConfiguration();
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] File configuration loaded.");
        }

        // Next enable the features.
        featureOptional.onFeatureEnable(manualEnable);

        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(featureOptional, SpigotCore.PLUGIN);

        // Initialize commands
        if (featureOptional instanceof InitCommands) {
            featureData.addCommands(((InitCommands) featureOptional).registerAllCommands());
        }
    }

    /**
     * Disables a features.
     *
     * @param featureOptional The features to disable.
     * @param manualDisable   False if the feature was disabled by the server. True otherwise.
     */
    void disableFeature(FeatureOptional featureOptional, boolean manualDisable) {
        FeatureData featureData = getFeatureData(featureOptional);
        String featureName = featureOptional.getClass().getSimpleName();

        if (!featureData.isEnabled) return;
        featureData.isEnabled = false;

        Console.sendMessage(ChatColor.GREEN + "Disabling: " + featureName);

        // Unregister all event listeners for specific feature
        HandlerList.unregisterAll(featureOptional);

        // Unregister commands
        if (featureOptional instanceof InitCommands) {
            featureData.disableCommands();
        }

        // Save configuration files
        if (featureOptional instanceof SavesConfig) {
            ((SavesConfig) featureOptional).saveConfiguration();
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Configuration saved.");
        }

        if (featureOptional instanceof AbstractDatabaseFeature) {
            // TODO: Do something here? Or remove?
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Database data saved.");
        }

        featureOptional.onFeatureDisable(manualDisable);
    }

    /**
     * This will shutdown any featureDataMap that are currently enabled.
     * Once all features are shutdown, they can not be turned on again without
     * a server restart.
     */
    private void shutdownAllFeatures() {
        for (FeatureData featureData : featureDataMap.values()) {
            disableFeature(featureData.featureOptional, false);
            if (featureData instanceof ShutdownTask) ((ShutdownTask) featureData).onServerShutdown();
        }
    }

    /**
     * Reloads a configuration file for a FeatureOptional feature.
     *
     * @param feature                The feature we will reload the config for.
     * @param saveConfigBeforeReload If we should save the loaded config before reloading.
     * @return True if the configuration was reloaded, false otherwise.
     */
    boolean reloadFeatureConfig(FeatureOptional feature, boolean saveConfigBeforeReload) {
        boolean didReload = false;
        if (saveConfigBeforeReload && feature instanceof SavesConfig) {
            ((SavesConfig) feature).saveConfiguration();
            Console.sendMessage("[FeatureToggleManager] Config saved for " + feature.getClass().getSimpleName());
        }
        if (feature instanceof LoadsConfig) {
            ((LoadsConfig) feature).loadConfiguration();
            Console.sendMessage("[FeatureToggleManager] Config loaded for " + feature.getClass().getSimpleName());
            didReload = true;
        }
        return didReload;
    }

    /**
     * Data class that holds information about a {@link FeatureOptional} object.
     */
    @Getter
    class FeatureData {

        private final List<FeatureOptionalCommand> commandMap = new ArrayList<>();
        private final Class clazz;
        private final FeatureOptional featureOptional;
        private final boolean isEnabledInConfig;
        private boolean isEnabled = false;

        FeatureData(Class clazz, FeatureOptional featureOptional, boolean isEnabledInConfig) {
            this.clazz = clazz;
            this.featureOptional = featureOptional;
            this.isEnabledInConfig = isEnabledInConfig;
        }

        void addCommands(List<FeatureOptionalCommand> commandList) {
            for (FeatureOptionalCommand featureOptionalCommand : commandList) {
                commandMap.add(featureOptionalCommand);
                Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureOptional.getClass().getSimpleName()
                        + "] Enabled " + featureOptionalCommand.getClass().getSimpleName() + " commands");
                featureOptionalCommand.setupCommand(SpigotCore.PLUGIN.getPaperCommandManager());
                featureOptionalCommand.enableCommand();
            }
        }

        void disableCommands() {
            for (FeatureOptionalCommand featureOptionalCommand : commandMap) {
                featureOptionalCommand.disableCommand();
                Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureOptional.getClass().getSimpleName()
                        + "] Disabled " + featureOptionalCommand.getClass().getSimpleName() + " commands");
            }
        }
    }
}
