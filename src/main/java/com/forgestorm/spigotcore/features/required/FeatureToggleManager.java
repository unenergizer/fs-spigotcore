package com.forgestorm.spigotcore.features.required;

import co.aikar.commands.BaseCommand;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.*;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.FeatureShutdown;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
 *     <li>/feature list = List all features registered with toggle manager.</li>
 *     <li>/feature enable FeatureName = Safely starts a feature wither or not it was disabled on server start.</li>
 *     <li>/feature disable FeatureName = Safely stops a feature.</li>
 *     <li>/feature reload FeatureName = Safely stops and starts (reloads) a feature.</li>
 * </ul>
 * <p>
 * RULES:
 * <ul>
 *     <li>Features are not allowed to interact with each other. No exceptions!</li>
 * </ul>
 */
public class FeatureToggleManager implements FeatureRequired, CommandExecutor {

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
    private final Map<Class, FeatureData> featureDataMap = new HashMap<>();

    /**
     * A delay created to make sure all tasks start X amount of time
     * after the server is fully loaded up. This is done mainly to give
     * the WorldSettings features (if enabled) enough time to clear all
     * world entities without effecting ones we may spawn in.
     * <p>
     * Note: 20 ticks is 1 second.
     * Example: 20 ticks * 5 = 5 seconds.
     */
    public static final int FEATURE_TASK_START_DELAY = 20 * 5;

    @Override
    public void onServerStartup() {
        //SpigotCore.PLUGIN.getCommand("features").setExecutor(this);
    }

    @Override
    public void onServerShutdown() {
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

            // Lets check if the features was disabled or if their was perhaps a spelling error that
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
    private void enableFeature(FeatureOptional featureOptional, boolean manualEnable) {
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
        featureOptional.onEnable(manualEnable);

        // Initialize commands
        if (featureOptional instanceof InitCommand && featureOptional instanceof InitCommandList) {
            Console.sendMessage(ChatColor.RED + " - [" + featureName + "] Did not register commands. Only implement one InitCommand interface!");

        } else if (featureOptional instanceof InitCommand) {
            BaseCommand baseCommand = ((InitCommand) featureOptional).defineCommands();
            SpigotCore.PLUGIN.getCommandManager().registerCommand(baseCommand);
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Registered command: " + baseCommand.getClass().getSimpleName());

        } else if (featureOptional instanceof InitCommandList) {
            List<BaseCommand> baseCommandList = ((InitCommandList) featureOptional).defineCommands();
            for (BaseCommand baseCommand : baseCommandList) {
                SpigotCore.PLUGIN.getCommandManager().registerCommand(baseCommand);
                Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Registered command: " + baseCommand.getClass().getSimpleName());
            }
        }
    }

    /**
     * Disables a features.
     *
     * @param featureOptional The features to disable.
     * @param manualDisable   False if the feature was disabled by the server. True otherwise.
     */
    private void disableFeature(FeatureOptional featureOptional, boolean manualDisable) {
        FeatureData featureData = getFeatureData(featureOptional);
        String featureName = featureOptional.getClass().getSimpleName();

        if (!featureData.isEnabled) return;
        featureData.isEnabled = false;

        Console.sendMessage(ChatColor.GREEN + "Disabling: " + featureName);

        // Unregister commands
        if (featureOptional instanceof InitCommand) {
            BaseCommand baseCommand = ((InitCommand) featureOptional).defineCommands();
            SpigotCore.PLUGIN.getCommandManager().unregisterCommand(baseCommand);
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Unregistered command: " + baseCommand.getClass().getSimpleName());

        } else if (featureOptional instanceof InitCommandList) {
            List<BaseCommand> baseCommandList = ((InitCommandList) featureOptional).defineCommands();
            for (BaseCommand baseCommand : baseCommandList) {
                SpigotCore.PLUGIN.getCommandManager().unregisterCommand(baseCommand);
                Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Unregistered command: " + baseCommand.getClass().getSimpleName());
            }
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

        featureOptional.onDisable(manualDisable);
    }

    /**
     * This will shutdown any featureDataMap that are currently enabled.
     * Once all features are shutdown, they can not be turned on again without
     * a server restart.
     */
    private void shutdownAllFeatures() {
        for (FeatureData featureData : featureDataMap.values()) {
            disableFeature(featureData.featureOptional, false);
            if (featureData instanceof FeatureShutdown) ((FeatureShutdown) featureData).onServerShutdown();
        }
    }

    /**
     * Reloads a configuration file for a FeatureOptional feature.
     *
     * @param feature                The feature we will reload the config for.
     * @param saveConfigBeforeReload If we should save the loaded config before reloading.
     * @return True if the configuration was reloaded, false otherwise.
     */
    private boolean reloadFeatureConfig(FeatureOptional feature, boolean saveConfigBeforeReload) {
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
    private class FeatureData {

        private final Class clazz;
        private final FeatureOptional featureOptional;
        private final boolean isEnabledInConfig;
        private boolean isEnabled = false;

        FeatureData(Class clazz, FeatureOptional featureOptional, boolean isEnabledInConfig) {
            this.clazz = clazz;
            this.featureOptional = featureOptional;
            this.isEnabledInConfig = isEnabledInConfig;
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) return false;
        if (args.length == 1) {
            if (!args[0].equalsIgnoreCase("list")) return false;
            sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Total Features: " + featureDataMap.size());
            sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] [+] / [-] = Enabled or disabled in config.");

            List<String> shownFeatures = new ArrayList<>();

            for (FeatureData featureData : featureDataMap.values()) {
                String info = "";

                if (featureData.isEnabledInConfig) {
                    info = info + Text.color("&7[&a+&7]");
                } else {
                    info = info + Text.color("&7[&c-&7]");
                }

                if (featureData.isEnabled) {
                    info = info + ChatColor.GREEN + featureData.clazz.getSimpleName();
                } else {
                    info = info + ChatColor.RED + featureData.clazz.getSimpleName();
                }

                shownFeatures.add(info);
            }

            int shown = 0;
            StringBuilder shownMessage = new StringBuilder();
            for (String string : shownFeatures) {
                if (shown >= 3) {
                    sender.sendMessage(shownMessage.toString());
                    shown = 0;
                    shownMessage = new StringBuilder();
                }

                shown++;
                shownMessage.append(string).append(ChatColor.RESET).append(", ");
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("enable")) {

                boolean found = false;

                for (FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.clazz.getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;
                    if (featureData.isEnabled) {
                        sender.sendMessage(ChatColor.RED + "[FeatureToggleManager] " + simpleName + " is already enabled.");
                        return false;
                    }

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabling features: " + simpleName);
                    enableFeature(featureData.featureOptional, true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabling finished");

                    found = true;
                }

                if (!found)
                    sender.sendMessage(ChatColor.RED + "Could not enable. Feature " + args[1] + " was not found.");
                return false;
            }
            if (args[0].equalsIgnoreCase("disable")) {

                boolean found = false;

                for (FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.clazz.getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;
                    if (!featureData.isEnabled) {
                        sender.sendMessage(ChatColor.RED + "[FeatureToggleManager] " + simpleName + " is already disabled.");
                        return false;
                    }

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Disabling features: " + simpleName);
                    disableFeature(featureData.featureOptional, true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Disabling finished");

                    found = true;
                }

                if (!found)
                    sender.sendMessage(ChatColor.RED + "Could not disable. Feature " + args[1] + " was not found.");
                return false;
            }
            if (args[0].equalsIgnoreCase("reload")) {

                boolean found = false;

                for (FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.clazz.getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Disabling features: " + simpleName);
                    disableFeature(featureData.featureOptional, true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabling features: " + simpleName);
                    enableFeature(featureData.featureOptional, true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Reload finished");

                    found = true;
                }

                if (!found)
                    sender.sendMessage(ChatColor.RED + "Could not reload. Feature " + args[1] + " was not found.");
                return false;
            }
            if (args[0].equalsIgnoreCase("reloadConfig")) {

                boolean found = false;

                for (FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.clazz.getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;

                    // TODO: Allow the command sender to specify if config should save first.
                    reloadFeatureConfig(featureData.featureOptional, true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Reload finished");

                    found = true;
                }

                if (!found)
                    sender.sendMessage(ChatColor.RED + "Could not reload. Feature " + args[1] + " was not found.");
                return false;
            }
            if (args[0].equalsIgnoreCase("status")) {

                boolean found = false;

                for (FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.clazz.getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Status: " + simpleName);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] EnabledInConfig: " + featureData.isEnabledInConfig);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabled: " + featureData.isEnabled);

                    found = true;
                }

                if (!found)
                    sender.sendMessage(ChatColor.RED + "Could not get status. Feature " + args[1] + " was not found.");
                return false;
            }
            return false;
        }
        return false;
    }
}
