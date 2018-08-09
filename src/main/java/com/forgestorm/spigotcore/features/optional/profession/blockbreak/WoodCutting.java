package com.forgestorm.spigotcore.features.optional.profession.blockbreak;

import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.constants.ProfessionType;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.events.FeatureProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.required.database.ProfileData;
import com.forgestorm.spigotcore.features.required.database.global.SqlSearchData;
import com.forgestorm.spigotcore.util.math.exp.ProfessionExperience;
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

public class WoodCutting extends BlockBreakProfession<WoodCuttingProfileData> implements LoadsConfig {

    public WoodCutting() {
        super(YamlConfiguration.loadConfiguration(new File(FilePaths.PROFESSION_WOOD_CUTTING.toString())),
                ProfessionType.WOOD_CUTTING);
    }

    @Override
    public void loadConfiguration() {
        ConfigurationSection woodCuttingSection = fileConfiguration.getConfigurationSection("");

        // Add wood cutting tools.
        for (String s : woodCuttingSection.getKeys(false)) {
            blockBreakTools.put(s, professionType);
        }
    }

    @Override
    public int getLevel(Player player) {
        return experienceCalculator.getLevel(getProfileData(player).getWoodCuttingExp());
    }

    @Override
    public long currentExperience(Player player) {
        return getProfileData(player).getWoodCuttingExp();
    }

    @Override
    public void setExperience(Player player, long expGained) {
        getProfileData(player).setWoodCuttingExp(expGained);
    }

    @Override
    public Material getDropMaterial(String toolName, String blockName, byte blockData, Material blockType) {
        return blockType;
    }

    @Override
    public void setBlockRegen(Material blockType, byte blockData, byte tempData, Location blockLocation) {
        blockRegen.setBlock(blockType, blockData, Material.STAINED_CLAY, tempData, blockLocation);
    }

    @Override
    public String getUpgrades(int rankUpgradeLevel) {
        String result = "";
        if (rankUpgradeLevel == 20) {
            result = "&aStone Axe &7and &aChop Spruce Logs";
        } else if (rankUpgradeLevel == 40) {
            result = "&aIron Axe &7and &aChop Birch Logs";
        } else if (rankUpgradeLevel == 60) {
            result = "&aDiamond Axe &7and &aChop Jungle Logs";
        } else if (rankUpgradeLevel == 80) {
            result = "&aGold Axe &7and &aChop Acacia Logs";
        } else if (rankUpgradeLevel == 100) {
            result = "&aPrestige level 1";
        }
        return Text.color(result);
    }

    @Override
    public boolean toolCheck(Player player, Material tool) {
        switch (tool) {
            case DIAMOND_AXE:
            case GOLD_AXE:
            case IRON_AXE:
            case STONE_AXE:
            case WOOD_AXE:
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
        WoodCuttingProfileData profileData = new WoodCuttingProfileData();

        profileData.setWoodCuttingExp(resultSet.getLong("experience"));
        profileData.setLogsHarvested(resultSet.getInt("logs_harvested"));
        profileData.setLogsFailed(resultSet.getInt("logs_failed"));
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public void databaseSave(Player player, WoodCuttingProfileData profileData, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE fs_profession_wood_cutting SET experience=?, logs_harvested=?, logs_failed=? WHERE uuid=?");

        preparedStatement.setLong(1, profileData.getWoodCuttingExp());
        preparedStatement.setInt(2, profileData.getLogsHarvested());
        preparedStatement.setInt(3, profileData.getLogsFailed());
        preparedStatement.setString(4, player.getUniqueId().toString());

        preparedStatement.execute();
    }

    @Override
    public ProfileData firstTimeSave(Player player, Connection connection) throws SQLException {
        ProfessionExperience professionExperience = new ProfessionExperience();
        long experience = professionExperience.getExperience(1);
        int logsHarvested = 0;
        int logsFailed = 0;


        PreparedStatement newPlayerStatement = connection.prepareStatement("INSERT INTO fs_profession_wood_cutting " +
                "(uuid, experience, logs_harvested, logs_failed) " +
                "VALUES(?, ?, ?, ?)");

        newPlayerStatement.setString(1, player.getUniqueId().toString());
        newPlayerStatement.setLong(2, experience);
        newPlayerStatement.setInt(3, logsHarvested);
        newPlayerStatement.setInt(4, logsFailed);

        newPlayerStatement.execute();

        WoodCuttingProfileData profileData = new WoodCuttingProfileData();
        profileData.setWoodCuttingExp(experience);
        profileData.setLogsHarvested(logsHarvested);
        profileData.setLogsFailed(logsFailed);
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public SqlSearchData searchForData(Player player, Connection connection) {
        return new SqlSearchData("fs_profession_wood_cutting", "uuid", player.getUniqueId().toString());
    }

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        onEnable();
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        onDisable();
        FeatureProfileDataLoadEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onFeatureProfileLoad(FeatureProfileDataLoadEvent event) {
        if (!(event.getFeature() instanceof WoodCutting)) return;
        Console.sendMessage("Data loaded for: " + event.getPlayer().getDisplayName() + ", Feature: " + event.getFeature().getClass().getName());
    }
}
