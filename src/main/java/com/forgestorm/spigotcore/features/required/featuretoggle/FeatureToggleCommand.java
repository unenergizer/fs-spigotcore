package com.forgestorm.spigotcore.features.required.featuretoggle;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandAlias("feature|f")
public class FeatureToggleCommand extends FeatureOptionalCommand {

    private final FeatureToggleManager featureToggleManager;
    private Map<Class, FeatureToggleManager.FeatureData> featureDataMap;

    FeatureToggleCommand(FeatureToggleManager featureToggleManager) {
        this.featureToggleManager = featureToggleManager;
    }

    @Override
    public void setupCommand(PaperCommandManager paperCommandManager) {
        paperCommandManager.getCommandContexts().registerIssuerAwareContext(CommandSender.class, BukkitCommandExecutionContext::getSender);
        paperCommandManager.getCommandCompletions().registerCompletion("featuresDisabled", c -> {
            List<String> features = new ArrayList<>();
            for (FeatureToggleManager.FeatureData featureData : SpigotCore.PLUGIN.getFeatureToggleManager().getFeatureDataMap().values()) {
                if (featureData.isEnabled()) continue;
                features.add(featureData.getClazz().getSimpleName());
            }
            return features;
        });
        paperCommandManager.getCommandCompletions().registerCompletion("featuresEnabled", c -> {
            List<String> features = new ArrayList<>();
            for (FeatureToggleManager.FeatureData featureData : SpigotCore.PLUGIN.getFeatureToggleManager().getFeatureDataMap().values()) {
                if (!featureData.isEnabled()) continue;
                features.add(featureData.getClazz().getSimpleName());
            }
            return features;
        });
        paperCommandManager.getCommandCompletions().registerCompletion("allFeatures", c -> {
            List<String> features = new ArrayList<>();
            for (FeatureToggleManager.FeatureData featureData : SpigotCore.PLUGIN.getFeatureToggleManager().getFeatureDataMap().values()) {
                features.add(featureData.getClazz().getSimpleName());
            }
            return features;
        });
    }

    @PreCommand
    public boolean onPreCommand(CommandSender commandSender) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(Text.color("&cYou are not authorized to use this command."));
            if (commandSender instanceof Player) CommonSounds.ACTION_FAILED.play((Player) commandSender);
            return true;
        }
        featureDataMap = featureToggleManager.getFeatureDataMap();
        return false;
    }

    @Subcommand("list|l")
    public void listFeatures(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Total Features: " + featureDataMap.size());
        commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] [+] / [-] = Enabled or disabled in config.");

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
                commandSender.sendMessage(shownMessage.toString());
                shown = 0;
                shownMessage = new StringBuilder();
            }

            shown++;
            shownMessage.append(string).append(ChatColor.RESET).append(", ");
        }
    }

    @Subcommand("enable|e")
    @CommandCompletion("@featuresDisabled")
    @Syntax("<FeatureName> - A optional feature we want to enable.")
    public void enableFeature(CommandSender commandSender, String featureName) {
        boolean found = false;

        for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
            String simpleName = featureData.getClazz().getSimpleName();

            if (!simpleName.equalsIgnoreCase(featureName)) continue;
            if (featureData.isEnabled()) {
                commandSender.sendMessage(ChatColor.RED + "[FeatureToggleManager] " + simpleName + " is already enabled.");
                return;
            }

            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabling features: " + simpleName);
            featureToggleManager.enableFeature(featureData.getFeatureOptional(), true);
            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabling finished");

            found = true;
        }

        if (!found)
            commandSender.sendMessage(ChatColor.RED + "Could not enable. Feature " + featureName + " was not found.");
    }

    @Subcommand("disable|d")
    @CommandCompletion("@featuresEnabled")
    @Syntax("<FeatureName> - A optional feature we want to disable.")
    public void disableCommand(CommandSender commandSender, String featureName) {
        boolean found = false;

        for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
            String simpleName = featureData.getClazz().getSimpleName();

            if (!simpleName.equalsIgnoreCase(featureName)) continue;
            if (!featureData.isEnabled()) {
                commandSender.sendMessage(ChatColor.RED + "[FeatureToggleManager] " + simpleName + " is already disabled.");
                return;
            }

            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Disabling features: " + simpleName);
            featureToggleManager.disableFeature(featureData.getFeatureOptional(), true);
            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Disabling finished");

            found = true;
        }

        if (!found)
            commandSender.sendMessage(ChatColor.RED + "Could not disable. Feature " + featureName + " was not found.");
    }

    @Subcommand("reload|r")
    @CommandCompletion("@featuresEnabled")
    @Syntax("<FeatureName> - Reloads this feature.")
    public void reloadFeature(CommandSender commandSender, String featureName) {
        boolean found = false;

        for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
            String simpleName = featureData.getClazz().getSimpleName();

            if (!simpleName.equalsIgnoreCase(featureName)) continue;

            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Disabling features: " + simpleName);
            featureToggleManager.disableFeature(featureData.getFeatureOptional(), true);
            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabling features: " + simpleName);
            featureToggleManager.enableFeature(featureData.getFeatureOptional(), true);
            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Reload finished");

            found = true;
        }

        if (!found)
            commandSender.sendMessage(ChatColor.RED + "Could not reload. Feature " + featureName + " was not found.");
    }

    @Subcommand("reloadConfig|rc")
    @CommandCompletion("@featuresEnabled")
    @Syntax("<FeatureName> - Reloads the config file for this feature.")
    public void reloadConfig(CommandSender commandSender, String featureName) {
        boolean found = false;

        for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
            String simpleName = featureData.getClazz().getSimpleName();

            if (!simpleName.equalsIgnoreCase(featureName)) continue;

            // TODO: Allow the command sender to specify if config should save first.
            featureToggleManager.reloadFeatureConfig(featureData.getFeatureOptional(), true);
            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Reload finished");

            found = true;
        }

        if (!found)
            commandSender.sendMessage(ChatColor.RED + "Could not reload. Feature " + featureName + " was not found.");
    }

    @Subcommand("status|s")
    @CommandCompletion("@allFeatures")
    @Syntax("<FeatureName> - Gets the status of this feature.")
    public void featureStatus(CommandSender commandSender, String featureName) {
        boolean found = false;

        for (FeatureToggleManager.FeatureData featureData : featureDataMap.values()) {
            String simpleName = featureData.getClazz().getSimpleName();

            if (!simpleName.equalsIgnoreCase(featureName)) continue;

            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Status: " + simpleName);
            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] EnabledInConfig: " + featureData.isEnabledInConfig());
            commandSender.sendMessage(ChatColor.YELLOW + "[FeatureToggleManager] Enabled: " + featureData.isEnabled());

            found = true;
        }

        if (!found)
            commandSender.sendMessage(ChatColor.RED + "Could not get status. Feature " + featureName + " was not found.");
    }
}
