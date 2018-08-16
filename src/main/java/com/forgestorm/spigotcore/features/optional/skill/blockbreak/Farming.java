package com.forgestorm.spigotcore.features.optional.skill.blockbreak;

import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.constants.SkillType;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.events.FeatureProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.required.database.ProfileData;
import com.forgestorm.spigotcore.features.required.database.global.SqlSearchData;
import com.forgestorm.spigotcore.util.math.exp.SkillExperience;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

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

public class Farming extends BlockBreakSkill<FarmingProfileData> implements LoadsConfig {

    public Farming() {
        super(YamlConfiguration.loadConfiguration(new File(FilePaths.PROFESSION_FARMING.toString())),
                SkillType.FARMING);
    }

    @Override
    public void loadConfiguration() {
        ConfigurationSection farmingSection = fileConfiguration.getConfigurationSection("");

        // Add farming tools.
        for (String s : farmingSection.getKeys(false)) {
            blockBreakTools.put(s, skillType);
        }
    }

    @EventHandler
    public void onFeatureProfileLoad(FeatureProfileDataLoadEvent event) {
        if (!(event.getFeature() instanceof Farming)) return;
        Console.sendMessage("Data loaded for: " + event.getPlayer().getDisplayName() + ", Feature: " + event.getFeature().getClass().getName());
    }

    @Override
    public int getLevel(Player player) {
        return experienceCalculator.getLevel(getProfileData(player).getFarmingExp());
    }

    @Override
    public long currentExperience(Player player) {
        return getProfileData(player).getFarmingExp();
    }

    @Override
    public void setExperience(Player player, long expGained) {
        getProfileData(player).setFarmingExp(expGained);
    }

    @Override
    public Material getDropMaterial(String toolName, String blockName, byte blockData, Material blockType) {
        return Material.valueOf(fileConfiguration.getString(toolName + ".breaks." + blockName + "-" + blockData + ".drop"));
    }

    @Override
    public void setBlockRegen(Material blockType, byte blockData, byte tempData, Location blockLocation) {
        blockRegen.setBlock(blockType, (byte) 0, Material.AIR, blockLocation);
    }

    @Override
    public String getUpgrades(int rankUpgradeLevel) {
        String result = "";
        if (rankUpgradeLevel == 20) {
            result = "&aStone Hoe &7and &aCarrot Harvesting";
        } else if (rankUpgradeLevel == 40) {
            result = "&aIron Hoe &7and &aPotato Harvesting";
        } else if (rankUpgradeLevel == 60) {
            result = "&aDiamond Hoe &7and &aBeetroot Harvesting";
        } else if (rankUpgradeLevel == 80) {
            result = "&aGold Hoe &7and &aMelon Harvesting";
        } else if (rankUpgradeLevel == 100) {
            result = "&aPrestige level 1";
        }
        return Text.color(result);
    }

    @Override
    public boolean toolCheck(Player player, Material tool) {
        switch (tool) {
            case DIAMOND_HOE:
            case IRON_HOE:
            case WOOD_HOE:
            case GOLD_HOE:
            case STONE_HOE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void loadDatabase(Player player) {
        asyncDatastoreLoad(player);
    }

    @Override
    public ProfileData databaseLoad(Player player, Connection connection, ResultSet resultSet) throws SQLException {
        FarmingProfileData profileData = new FarmingProfileData();

        profileData.setFarmingExp(resultSet.getLong("experience"));
        profileData.setCropsHarvested(resultSet.getInt("crops_harvested"));
        profileData.setCropsFailed(resultSet.getInt("crops_failed"));
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public void databaseSave(Player player, FarmingProfileData profileData, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE fs_skill_farming SET experience=?, crops_harvested=?, crops_failed=? WHERE uuid=?");

        preparedStatement.setLong(1, profileData.getFarmingExp());
        preparedStatement.setInt(2, profileData.getCropsHarvested());
        preparedStatement.setInt(3, profileData.getCropsFailed());
        preparedStatement.setString(4, player.getUniqueId().toString());

        preparedStatement.execute();
    }

    @Override
    public ProfileData firstTimeSave(Player player, Connection connection) throws SQLException {
        SkillExperience skillExperience = new SkillExperience();
        long experience = skillExperience.getExperience(1);
        int cropsHarvested = 0;
        int cropsFailed = 0;


        PreparedStatement newPlayerStatement = connection.prepareStatement("INSERT INTO fs_skill_farming " +
                "(uuid, experience, crops_harvested, crops_failed) " +
                "VALUES(?, ?, ?, ?)");

        newPlayerStatement.setString(1, player.getUniqueId().toString());
        newPlayerStatement.setLong(2, experience);
        newPlayerStatement.setInt(3, cropsHarvested);
        newPlayerStatement.setInt(4, cropsFailed);

        newPlayerStatement.execute();

        FarmingProfileData profileData = new FarmingProfileData();
        profileData.setFarmingExp(experience);
        profileData.setCropsHarvested(cropsHarvested);
        profileData.setCropsFailed(cropsFailed);
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public SqlSearchData searchForData(Player player, Connection connection) {
        return new SqlSearchData("fs_skill_farming", "uuid", player.getUniqueId().toString());
    }

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        onEnable();
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        onDisable();
    }
}
