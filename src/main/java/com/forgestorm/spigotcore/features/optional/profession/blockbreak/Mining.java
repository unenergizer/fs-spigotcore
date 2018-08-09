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

public class Mining extends BlockBreakProfession<MiningProfileData> implements LoadsConfig {

    public Mining() {
        super(YamlConfiguration.loadConfiguration(new File(FilePaths.PROFESSION_MINING.toString())), ProfessionType.MINING);
    }

    @EventHandler
    public void onFeatureProfileLoad(FeatureProfileDataLoadEvent event) {
        if (!(event.getFeature() instanceof Mining)) return;
        Console.sendMessage("Data loaded for: " + event.getPlayer().getDisplayName() + ", Feature: " + event.getFeature().getClass().getName());
    }

    @Override
    public void loadConfiguration() {
        ConfigurationSection miningSection = fileConfiguration.getConfigurationSection("");

        // Add mining tools.
        for (String s : miningSection.getKeys(false)) {
            blockBreakTools.put(s, professionType);
        }
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

    @Override
    public int getLevel(Player player) {
        return experienceCalculator.getLevel(getProfileData(player).getMiningExp());
    }

    @Override
    public long currentExperience(Player player) {
        return getProfileData(player).getMiningExp();
    }

    @Override
    public void setExperience(Player player, long expGained) {
        getProfileData(player).setMiningExp(expGained);
    }

    @Override
    public Material getDropMaterial(String toolName, String blockName, byte blockData, Material blockType) {
        return blockType;
    }

    @Override
    public void setBlockRegen(Material blockType, byte blockData, byte tempData, Location blockLocation) {
        blockRegen.setBlock(blockType, blockData, Material.STONE, blockLocation);
    }

    @Override
    public String getUpgrades(int rankUpgradeLevel) {
        String result = "";
        if (rankUpgradeLevel == 20) {
            result = "&aStone Pickaxe &7and &aMine Iron Ore";
        } else if (rankUpgradeLevel == 40) {
            result = "&aIron Pickaxe &7and &aMine Emerald Ore";
        } else if (rankUpgradeLevel == 60) {
            result = "&aDiamond Pickaxe &7and &aMine Lapis Ore";
        } else if (rankUpgradeLevel == 80) {
            result = "&aGold Pickaxe &7and &aMine Gold Ore";
        } else if (rankUpgradeLevel == 100) {
            result = "&aPrestige level 1";
        }
        return Text.color(result);
    }

    @Override
    public boolean toolCheck(Player player, Material tool) {
        switch (tool) {
            case DIAMOND_PICKAXE:
            case GOLD_PICKAXE:
            case IRON_PICKAXE:
            case STONE_PICKAXE:
            case WOOD_PICKAXE:
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
        MiningProfileData profileData = new MiningProfileData();

        profileData.setMiningExp(resultSet.getLong("experience"));
        profileData.setOresMined(resultSet.getInt("ores_mined"));
        profileData.setOresFailed(resultSet.getInt("ores_failed"));
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public void databaseSave(Player player, MiningProfileData profileData, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE fs_profession_mining SET experience=?, ores_mined=?, ores_failed=? WHERE uuid=?");

        preparedStatement.setLong(1, profileData.getMiningExp());
        preparedStatement.setInt(2, profileData.getOresMined());
        preparedStatement.setInt(3, profileData.getOresFailed());
        preparedStatement.setString(4, player.getUniqueId().toString());

        preparedStatement.execute();
    }

    @Override
    public ProfileData firstTimeSave(Player player, Connection connection) throws SQLException {
        ProfessionExperience professionExperience = new ProfessionExperience();
        long experience = professionExperience.getExperience(1);
        int oresMined = 0;
        int oresFailed = 0;


        PreparedStatement newPlayerStatement = connection.prepareStatement("INSERT INTO fs_profession_mining " +
                "(uuid, experience, ores_mined, ores_failed) " +
                "VALUES(?, ?, ?, ?)");

        newPlayerStatement.setString(1, player.getUniqueId().toString());
        newPlayerStatement.setLong(2, experience);
        newPlayerStatement.setInt(3, oresMined);
        newPlayerStatement.setInt(4, oresFailed);

        newPlayerStatement.execute();

        MiningProfileData profileData = new MiningProfileData();
        profileData.setMiningExp(experience);
        profileData.setOresMined(oresMined);
        profileData.setOresFailed(oresFailed);
        profileData.setDataLoaded(true);

        return profileData;
    }

    @Override
    public SqlSearchData searchForData(Player player, Connection connection) {
        return new SqlSearchData("fs_profession_mining", "uuid", player.getUniqueId().toString());
    }
}
