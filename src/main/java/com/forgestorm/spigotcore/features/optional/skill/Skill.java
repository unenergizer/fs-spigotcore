package com.forgestorm.spigotcore.features.optional.skill;


import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.constants.Messages;
import com.forgestorm.spigotcore.constants.SkillType;
import com.forgestorm.spigotcore.features.required.database.AbstractDatabaseFeature;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.math.exp.Experience;
import com.forgestorm.spigotcore.util.math.exp.SkillExperience;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import com.forgestorm.spigotcore.util.text.ProgressBarString;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-skills
 * DATE: 8/6/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */

public abstract class Skill<T> extends AbstractDatabaseFeature<T> {

    protected final SkillType skillType;
    protected final Experience experienceCalculator = new SkillExperience();
    private final int expOffSet = experienceCalculator.getExpOffSet();
    protected final FileConfiguration fileConfiguration;

    protected Skill(FileConfiguration fileConfiguration, SkillType skillType) {
        this.fileConfiguration = fileConfiguration;
        this.skillType = skillType;
    }

    /**
     * This is mainly used by skill menu's to get the players stat rank.
     *
     * @param level The skill level.
     * @return A rank message.
     */
    public static String getSkillRank(int level) {
        if (level < 20) {
            return "&7Rank: &fNovice";
        } else if (level < 40) {
            return "&7Rank: &aIntermediate";
        } else if (level < 60) {
            return "&7Rank: &9Proficient";
        } else if (level < 80) {
            return "&7Rank: &5Expert";
        } else {
            return "&7Rank: &6Grand Master";
        }
    }

    /**
     * This is mainly used by skill menu's to get the players stat rank.
     *
     * @param level The skill level.
     * @return A rank message.
     */
    private static int getNextSkillUpgradeLevel(int level) {
        if (level >= 20 && level < 40) {
            return 40;
        } else if (level >= 40 && level < 60) {
            return 60;
        } else if (level >= 60 && level < 80) {
            return 80;
        } else if (level >= 80) {
            return 100;
        }
        return 20;
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract String getUpgrades(int rankUpgradeLevel);

    public abstract boolean toolCheck(Player player, Material tool);

    public abstract void loadDatabase(Player player);

    @EventHandler
    public void onToolEquip(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItem(event.getNewSlot());

        if (itemStack == null) return;
        if (!toolCheck(player, itemStack.getType())) return; // Check to see if a skill item was put into their hand
        if (!isProfileDataLoaded(player)) loadDatabase(player);
    }

    /**
     * Calls the skill toggle event. Great if a skill needs to be canceled for
     * whatever reason.
     *
     * @param player The player who performed a skill action.
     * @return True if the event is cancelled, false otherwise.
     */
    protected boolean skillToggleEvent(Player player) {
        SkillToggleEvent skillToggleEvent = new SkillToggleEvent(player, skillType);
        Bukkit.getPluginManager().callEvent(skillToggleEvent);
        return skillToggleEvent.isCancelled();
    }

    /**
     * This will show the experience gained to the player and show leveling notifications.
     *
     * @param player            The player who is performing a skill action.
     * @param currentExperience The skills current experience prior to the skill action.
     * @param experienceGained  The experience gained after the skill action.
     */
    protected void showExperienceMessages(Player player, long currentExperience, long experienceGained) {
        int oldLevel = experienceCalculator.getLevel(currentExperience);
        int level = experienceCalculator.getLevel(currentExperience + experienceGained);
        int upgradeLevel = getNextSkillUpgradeLevel(level);
        double percentTillUpgrade = (level * 100) / upgradeLevel;
        String percentString = Integer.toString((int) percentTillUpgrade);
        long exp = currentExperience + experienceGained;
        String skillName = skillType.getSkillName();

        // Send ActionBar title message.
        SpigotCore.PLUGIN.getTitleManager().sendActionbar(player, generateActionBarMessage(experienceGained, exp, level));

        //Level up check
        if (oldLevel == level) return;

        // Toggle skill level up event!
        SkillLevelEvent skillLevelEvent = new SkillLevelEvent(player, skillType, level);
        Bukkit.getPluginManager().callEvent(skillLevelEvent);

        // Show Leveling message!
        player.sendMessage("");
        player.sendMessage(Messages.BAR_LEVEL_UP.toString());
        player.sendMessage("");
        player.sendMessage(formatLevelUpMessage(Messages.LEVEL_UP_01.toString()));
        player.sendMessage(formatLevelUpMessage("&7Your &3" + skillName + " &7skill is now level &3" + level + "&7!!!"));
        player.sendMessage("");
        player.sendMessage(formatLevelUpMessage("&7Upgrade Progression: &d" + percentString + "&7% (level " + level + "/" + upgradeLevel + ")"));
        player.sendMessage(formatLevelUpMessage("&bLevel " + level + " " + ProgressBarString.buildBar(percentTillUpgrade) + " &bLevel " + upgradeLevel));
        player.sendMessage(formatLevelUpMessage("&7Level " + upgradeLevel + " Unlocks: " + getUpgrades(upgradeLevel)));
        player.sendMessage(Messages.BAR_BOTTOM.toString());

        // Play success sound
        CommonSounds.ACTION_SUCCESS.play(player);

        // Show Fireworks for leveling!
        for (double i = 0; i < 2; i++) {
            Firework fw = player.getWorld().spawn(player.getLocation().subtract(0, -1, 0), Firework.class);
            FireworkMeta fm = fw.getFireworkMeta();
            fm.addEffect(FireworkEffect.builder()
                    .flicker(false)
                    .trail(false)
                    .with(FireworkEffect.Type.STAR)
                    .withColor(Color.YELLOW)
                    .withFade(Color.YELLOW)
                    .build());
            fw.setFireworkMeta(fm);
        }
    }

    /**
     * Helper method to auto center and color a message.
     *
     * @param message The message to format.
     * @return A formatted message.
     */
    private String formatLevelUpMessage(String message) {
        return CenterChatText.centerChatMessage(message);
    }

    /**
     * This will generate a message to show the player for using their skill item.
     *
     * @param expGain   The amount of exp they have gained this block break.
     * @param itemEXP   The amount of exp they have in total.
     * @param itemLevel The current level of their skill.
     * @return Returns a detailed and formatted message to show to the player.
     */
    private String generateActionBarMessage(long expGain, long itemEXP, int itemLevel) {
        double expPercent = experienceCalculator.getPercentToLevel(itemEXP);
        int expGoal = experienceCalculator.getExperience(itemLevel + 1) - expOffSet;
        int friendlyExpShow = (int) itemEXP - expOffSet;
        String bar = ProgressBarString.buildBar(expPercent);
        return ChatColor.GRAY + "" + ChatColor.BOLD +
                "EXP: " +
                bar +
                ChatColor.GRAY + ChatColor.BOLD + " " + expPercent + "%" +
                ChatColor.RESET + ChatColor.GRAY + " [" +
                ChatColor.BLUE + friendlyExpShow + " / " + expGoal +
                ChatColor.RESET + ChatColor.GRAY + "] "
                + ChatColor.GREEN + "+" + ChatColor.GRAY + expGain + " EXP";
    }

    /**
     * Generic fail notification to show the user if the skill action failed.
     *
     * @param player The player we will send the message to.
     */
    protected void sendChatFailMessage(Player player, String message) {
        // Send failed notifications.
        player.sendMessage(message);
        CommonSounds.ACTION_FAILED.play(player);
    }

    /**
     * Generic fail notification to show the user if the skill action failed.
     *
     * @param player The player we will send the message to.
     */
    protected void sendActionBarFailMessage(Player player, String message) {
        // Send failed notifications.
        SpigotCore.PLUGIN.getTitleManager().sendActionbar(player, ChatColor.RED + "" + ChatColor.BOLD + message);
        CommonSounds.ACTION_FAILED.play(player);
    }

    /**
     * Some skills will have a chance to drop gems.
     *
     * @param player The player who we might drop gems next to.
     */
    protected void dropGems(Player player, Location location) {
        int chance = RandomChance.randomInt(1, 100) - 1;
        if (chance >= 96) {
            int amount = RandomChance.randomInt(1, 3) - 1;
            ItemStack gems = new ItemStack(Material.EMERALD);
            gems.setAmount(amount);
            player.getWorld().dropItemNaturally(location, gems);
        }
    }
}
