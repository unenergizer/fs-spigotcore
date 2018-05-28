package com.forgestorm.spigotcore.feature;

import com.forgestorm.spigotcore.SpigotCore;
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

public class FeatureManager implements FeatureRequired, CommandExecutor {

    /**
     * Contains a list of all SpigotCore features that were added in
     * this {@link SpigotCore}.
     * Key {@link FeatureOptional}
     * Value: {@link FeatureData}
     * <p>
     * Note: Our goal is to never have any feature directly talk to
     * each other and to never access this map except to load and
     * unload our core features.
     * </p>
     */
    private final Map<Class, FeatureData> featureDataMap = new HashMap<>();

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

    @Override
    public void onServerStartup() {
        SpigotCore.PLUGIN.getCommand("feature").setExecutor(this);
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
        Console.sendMessage(ChatColor.YELLOW + "[FeatureManager] Total features loaded: " + featureOptionalList.size());

        int enabledFeatures = 0;
        for (FeatureOptional featureOptional : featureOptionalList) {
            String featureName = featureOptional.getClass().getSimpleName();
            String configSection = "Features." + featureName;
            boolean enabledInConfig = true;

            // Lets check if the feature was disabled or if their was perhaps a spelling error that
            // caused the feature to be disabled.
            if (!SpigotCore.PLUGIN.getConfig().getBoolean(configSection)) {
                if (SpigotCore.PLUGIN.getConfig().get(configSection) != null) {
                    // The feature was disabled in the configuration file.
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

        Console.sendMessage(ChatColor.YELLOW + "[FeatureManager] Total features starting: " + enabledFeatures);

        // Start all enabled features.
        for (FeatureData featureData : featureDataMap.values()) {
            if (featureData.isEnabledInConfig) enableFeature(featureData.featureOptional, false);
        }
    }

    /**
     * Enables a given feature. Maintain operation order here.
     *
     * @param featureOptional The feature to enable.
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

        // Get blank database templates
        if (featureOptional instanceof AbstractDatabaseFeature) {
            SpigotCore.PLUGIN.getDatabaseManager().addDatabaseTemplate(
                    (AbstractDatabaseFeature) featureOptional,
                    ((AbstractDatabaseFeature) featureOptional).getBlankDatabaseTemplate());
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Database template loaded.");
        }

        // Next enable the feature.
        featureOptional.onEnable(manualEnable);

        // Finally, start async BukkitRunnables
        if (featureOptional instanceof AsyncFeature) {
            ((AsyncFeature) featureOptional).enableAsync();
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Async runnable loaded.");
        }
    }

    /**
     * Disables a feature.
     *
     * @param featureOptional The feature to disable.
     */
    private void disableFeature(FeatureOptional featureOptional, boolean manualDisable) {
        FeatureData featureData = getFeatureData(featureOptional);
        String featureName = featureOptional.getClass().getSimpleName();

        if (!featureData.isEnabled) return;
        featureData.isEnabled = false;

        Console.sendMessage(ChatColor.GREEN + "Disabling: " + featureName);

        // Stop async BukkitRunnables
        if (featureOptional instanceof AsyncFeature) ((AsyncFeature) featureOptional).cancel();

        // Save configuration files
        if (featureOptional instanceof SavesConfig) {
            ((SavesConfig) featureOptional).saveConfiguration();
            Console.sendMessage(ChatColor.DARK_GREEN + " - [" + featureName + "] Configuration saved.");
        }

        // Disable the feature now
        featureOptional.onDisable(manualDisable);
        featureData.isEnabled = false;
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
            sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Total Features: " + featureDataMap.size());
            sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] [+] / [-] = Enabled or disabled in config.");

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
                        sender.sendMessage(ChatColor.RED + "[FeatureManager] " + simpleName + " is already enabled.");
                        return false;
                    }

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Enabling feature: " + simpleName);
                    enableFeature(featureData.featureOptional, true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Enabling finished");

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
                        sender.sendMessage(ChatColor.RED + "[FeatureManager] " + simpleName + " is already disabled.");
                        return false;
                    }

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Disabling feature: " + simpleName);
                    disableFeature(featureData.featureOptional, true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Disabling finished");

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

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Disabling feature: " + simpleName);
                    disableFeature(featureData.featureOptional, true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Enabling feature: " + simpleName);
                    enableFeature(featureData.featureOptional, true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Reload finished");

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

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Status: " + simpleName);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] EnabledInConfig: " + featureData.isEnabledInConfig);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureManager] Enabled: " + featureData.isEnabled);

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
