package com.forgestorm.spigotcore.features.optional.skill.fishing;

import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.constants.SkillType;
import com.forgestorm.spigotcore.features.events.FeatureProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.optional.skill.Skill;
import com.forgestorm.spigotcore.features.required.database.ProfileData;
import com.forgestorm.spigotcore.features.required.database.global.SqlSearchData;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.math.exp.SkillExperience;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-spigotcore
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

public class FishingSkill extends Skill<FishingProfileData> {

    public FishingSkill() {
        super(YamlConfiguration.loadConfiguration(new File(FilePaths.PROFESSION_FISHING.toString())),
                SkillType.FISHING);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    /**
     * Called when a particular fishing action is achieved.
     *
     * @param player The player who toggled the fishing skill.
     */
    private void toggleFishingSkill(Player player) {

        if (!isProfileDataLoaded(player)) return;

        long currentExperience = getProfileData(player).getFishingExp();
        long currentLevel = experienceCalculator.getLevel(currentExperience);
        long experienceGained = 0;
        int tier = 0;

        if (20 > currentLevel) {
            //Tier 1 Fishing
            tier = 1;
            experienceGained = fileConfiguration.getLong(0 + ".exp");
        } else if (currentLevel < 40) {
            //Tier 2 Fishing
            tier = 2;
            experienceGained = fileConfiguration.getLong(20 + ".exp");
        } else if (currentLevel < 60) {
            //Tier 3 Fishing
            tier = 3;
            experienceGained = fileConfiguration.getLong(40 + ".exp");
        } else if (currentLevel < 80) {
            //Tier 4 Fishing
            tier = 4;
            experienceGained = fileConfiguration.getLong(60 + ".exp");
        } else if (currentLevel > 80) {
            //Tier 5 Fishing
            tier = 5;
            experienceGained = fileConfiguration.getLong(80 + ".exp");
        }

        World world = player.getWorld();

        // Fish drops
        int fishType = RandomChance.randomInt(1, tier);
        switch (fishType) {
            case 1:
                world.dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH));
                break;
            case 2:
                //noinspection deprecation
                world.dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 1, (short) 0, (byte) 1));
                break;
            case 3:
                //noinspection deprecation
                world.dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 1, (short) 0, (byte) 2));
                break;
            case 4:
                //noinspection deprecation
                world.dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 1, (short) 0, (byte) 3));
                break;
            case 5:
                world.dropItem(player.getLocation(), new ItemStack(Material.COOKED_FISH));
                break;

        }

        // Drop Gems
        dropGems(player, player.getLocation());

        // Set the experience
        getProfileData(player).setFishingExp(currentExperience + experienceGained);

        // Show experience related messages.
        showExperienceMessages(player, currentExperience, experienceGained);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        event.setExpToDrop(0); // Prevent player from getting exp
        PlayerFishEvent.State state = event.getState();
        Player player = event.getPlayer();

        // Fishing skill can only happen on "CAUGHT" states.
        if (state != PlayerFishEvent.State.CAUGHT_FISH && state != PlayerFishEvent.State.CAUGHT_ENTITY) return;

        // Call the event. If canceled, stop execution.
        if (skillToggleEvent(player)) return;

        // Toggle the fishing skill.
        toggleFishingSkill(event.getPlayer());

        // Remove the default fish. Will replace with our own.
        event.getCaught().remove();
    }

    @Override
    public String getUpgrades(int rankUpgradeLevel) {
        String result = "";
        if (rankUpgradeLevel == 20) {
            result = "&aCatch Better Fish";
        } else if (rankUpgradeLevel == 40) {
            result = "&aCatch Better Fish";
        } else if (rankUpgradeLevel == 60) {
            result = "&aCatch Better Fish";
        } else if (rankUpgradeLevel == 80) {
            result = "&aCatch Better Fish";
        } else if (rankUpgradeLevel == 100) {
            result = "&aPrestige level 1";
        }
        return Text.color(result);
    }

    @Override
    public boolean toolCheck(Player player, Material tool) {
        return tool == Material.FISHING_ROD;
    }

    @Override
    public void loadDatabase(Player player) {
        asyncDatastoreLoad(player);
    }

    @Override
    public ProfileData databaseLoad(Player player, Connection connection, ResultSet resultSet) throws SQLException {
        FishingProfileData profileData = new FishingProfileData();

        profileData.setFishingExp(resultSet.getLong("experience"));
        profileData.setFishCaught(resultSet.getInt("fish_caught"));
        profileData.setFishLost(resultSet.getInt("fish_lost"));
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public void databaseSave(Player player, FishingProfileData profileData, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE fs_skill_fishing SET experience=?, fish_caught=?, fish_lost=? WHERE uuid=?");

        preparedStatement.setLong(1, profileData.getFishingExp());
        preparedStatement.setInt(2, profileData.getFishCaught());
        preparedStatement.setInt(3, profileData.getFishLost());
        preparedStatement.setString(4, player.getUniqueId().toString());

        preparedStatement.execute();
    }

    @Override
    public ProfileData firstTimeSave(Player player, Connection connection) throws SQLException {
        SkillExperience skillExperience = new SkillExperience();
        long experience = skillExperience.getExperience(1);
        int fishCaught = 0;
        int fishLost = 0;


        PreparedStatement newPlayerStatement = connection.prepareStatement("INSERT INTO fs_skill_fishing " +
                "(uuid, experience, fish_caught, fish_lost) " +
                "VALUES(?, ?, ?, ?)");

        newPlayerStatement.setString(1, player.getUniqueId().toString());
        newPlayerStatement.setLong(2, experience);
        newPlayerStatement.setInt(3, fishCaught);
        newPlayerStatement.setInt(4, fishLost);

        newPlayerStatement.execute();

        FishingProfileData profileData = new FishingProfileData();
        profileData.setFishingExp(experience);
        profileData.setFishCaught(fishCaught);
        profileData.setFishLost(fishLost);
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public SqlSearchData searchForData(Player player, Connection connection) {
        return new SqlSearchData("fs_skill_fishing", "uuid", player.getUniqueId().toString());
    }

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        onEnable();
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        onDisable();
    }

    @EventHandler
    public void onFeatureProfileLoad(FeatureProfileDataLoadEvent event) {
        if (!(event.getFeature() instanceof FishingSkill)) return;
        Console.sendMessage("Data loaded for: " + event.getPlayer().getDisplayName() + ", Feature: " + event.getFeature().getClass().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isProfileDataLoaded(player)) {
            asyncDatastoreSave(player);
            player.sendMessage(Text.color("&aSaving your &e" + skillType.getSkillName() + " &adata..."));
        }
    }
}
