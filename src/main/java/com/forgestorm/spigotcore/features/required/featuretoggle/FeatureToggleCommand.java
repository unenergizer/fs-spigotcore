package com.forgestorm.spigotcore.features.required.featuretoggle;

import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureToggleCommand implements CommandExecutor {

    private FeatureToggleManager featureToggleManager;

    FeatureToggleCommand(FeatureToggleManager featureToggleManager) {
        this.featureToggleManager = featureToggleManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Map<Class, FeatureToggleManager.FeatureData> featureDataMap = featureToggleManager.getFeatureDataMap();

        if (!sender.isOp()) return false;
        if (args.length == 1) {
            if (!args[0].equalsIgnoreCase("list")) return false;
            sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Total Features: " + featureDataMap.size());
            sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] [+] / [-] = Enabled or disabled in config.");

            List<String> shownFeatures = new ArrayList<>();

            for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
                String info = "";

                if (featureData.isEnabledInConfig()) {
                    info = info + Text.color("&7[&a+&7]");
                } else {
                    info = info + Text.color("&7[&c-&7]");
                }

                if (featureData.isEnabled()) {
                    info = info + ChatColor.GREEN + featureData.getClazz().getSimpleName();
                } else {
                    info = info + ChatColor.RED + featureData.getClazz().getSimpleName();
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

                for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.getClazz().getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;
                    if (featureData.isEnabled()) {
                        sender.sendMessage(ChatColor.RED + "[FeatureToggleManager] " + simpleName + " is already enabled.");
                        return false;
                    }

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabling features: " + simpleName);
                    featureToggleManager.enableFeature(featureData.getFeatureOptional(), true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabling finished");

                    found = true;
                }

                if (!found)
                    sender.sendMessage(ChatColor.RED + "Could not enable. Feature " + args[1] + " was not found.");
                return false;
            }
            if (args[0].equalsIgnoreCase("disable")) {

                boolean found = false;

                for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.getClazz().getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;
                    if (!featureData.isEnabled()) {
                        sender.sendMessage(ChatColor.RED + "[FeatureToggleManager] " + simpleName + " is already disabled.");
                        return false;
                    }

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Disabling features: " + simpleName);
                    featureToggleManager.disableFeature(featureData.getFeatureOptional(), true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Disabling finished");

                    found = true;
                }

                if (!found)
                    sender.sendMessage(ChatColor.RED + "Could not disable. Feature " + args[1] + " was not found.");
                return false;
            }
            if (args[0].equalsIgnoreCase("reload")) {

                boolean found = false;

                for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.getClazz().getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Disabling features: " + simpleName);
                    featureToggleManager.disableFeature(featureData.getFeatureOptional(), true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabling features: " + simpleName);
                    featureToggleManager.enableFeature(featureData.getFeatureOptional(), true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Reload finished");

                    found = true;
                }

                if (!found)
                    sender.sendMessage(ChatColor.RED + "Could not reload. Feature " + args[1] + " was not found.");
                return false;
            }
            if (args[0].equalsIgnoreCase("reloadConfig")) {

                boolean found = false;

                for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.getClazz().getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;

                    // TODO: Allow the command sender to specify if config should save first.
                    featureToggleManager.reloadFeatureConfig(featureData.getFeatureOptional(), true);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Reload finished");

                    found = true;
                }

                if (!found)
                    sender.sendMessage(ChatColor.RED + "Could not reload. Feature " + args[1] + " was not found.");
                return false;
            }
            if (args[0].equalsIgnoreCase("status")) {

                boolean found = false;

                for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
                    String simpleName = featureData.getClazz().getSimpleName();

                    if (!simpleName.equalsIgnoreCase(args[1])) continue;

                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Status: " + simpleName);
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] EnabledInConfig: " + featureData.isEnabledInConfig());
                    sender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabled: " + featureData.isEnabled());

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
