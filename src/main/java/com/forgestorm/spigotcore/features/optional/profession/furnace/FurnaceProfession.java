package com.forgestorm.spigotcore.features.optional.profession.furnace;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.constants.ProfessionType;
import com.forgestorm.spigotcore.features.optional.profession.Profession;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceExtractEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

public abstract class FurnaceProfession {
}

//public abstract class FurnaceProfession extends Profession implements LoadConfiguration {
//
//    private final List<Material> furnaceMaterials = new ArrayList<>();
//
//    FurnaceProfession(ProfessionType professionType) {
//        super(YamlConfiguration.loadConfiguration(new File(FilePaths.PROFESSION_COOKING_AND_SMELTING.toString())),
//                professionType);
//
//        loadConfiguration();
//    }
//
//    @Override
//    public void onEnable() {
//        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
//    }
//
//    @Override
//    public void onDisable() {
//        FurnaceExtractEvent.getHandlerList().unregister(this);
//    }
//
//    @Override
//    public void loadConfiguration() {
//        ConfigurationSection smeltingSection = fileConfiguration.getConfigurationSection("");
//
//        // Add smelting materials
//        for (String s : smeltingSection.getKeys(false)) {
//            furnaceMaterials.add(Material.valueOf(s));
//        }
//    }
//
//    public abstract long getExperience(PlayerProfileData playerProfileData);
//
//    public abstract void setExperience(PlayerProfileData playerProfileData, long experience);
//
//    /**
//     * This is the furnace profession. This includes cooking and smelting.
//     *
//     * @param player   The player who used the furnace.
//     * @param amount   The amount of items cooked or smelted.
//     * @param material The material that was cooked or smelted.
//     */
//    private void toggleFurnaceProfession(Player player, int amount, Material material) {
//        String item = material.toString();
//
//        if (!furnaceMaterials.contains(material)) {
//            player.sendMessage(ChatColor.RED + "Smelting this item does not reward experience.");
//            CommonSounds.ACTION_FAILED.play(player);
//            return;
//        }
//
//        PlayerProfileData playerProfileData = plugin.getProfileManager().getProfile(player);
//        int experienceGained = fileConfiguration.getInt(item + ".exp") * amount;
//        long currentExperience = getExperience(playerProfileData);
//
//        // Set new experience
//        setExperience(playerProfileData, currentExperience + experienceGained);
//
//        // Show experience related messages.
//        showExperienceMessages(player, currentExperience, experienceGained);
//    }
//
//    @EventHandler
//    public void onFurnaceExtract(FurnaceExtractEvent event) {
//        event.setExpToDrop(0); // Do not give default Minecraft experience.
//        Player player = event.getPlayer();
//
//        // Call the event. If canceled, stop execution.
//        if (professionToggleEvent(player)) return;
//
//        toggleFurnaceProfession(player, event.getItemAmount(), event.getItemType());
//    }
//}
