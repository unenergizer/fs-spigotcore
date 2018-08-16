package com.forgestorm.spigotcore.features.optional.skill.furnace;

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

abstract class FurnaceSkill {
}

//public abstract class FurnaceSkill extends Skill implements LoadConfiguration {
//
//    private final List<Material> furnaceMaterials = new ArrayList<>();
//
//    FurnaceSkill(SkillType skillType) {
//        super(YamlConfiguration.loadConfiguration(new File(FilePaths.PROFESSION_COOKING_AND_SMELTING.toString())),
//                skillType);
//
//        loadConfiguration();
//    }
//
//    @Override
//    public void onEnable() {
//    }
//
//    @Override
//    public void onDisable() {
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
//     * This is the furnace skill. This includes cooking and smelting.
//     *
//     * @param player   The player who used the furnace.
//     * @param amount   The amount of items cooked or smelted.
//     * @param material The material that was cooked or smelted.
//     */
//    private void toggleFurnaceSkill(Player player, int amount, Material material) {
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
//        if (skillToggleEvent(player)) return;
//
//        toggleFurnaceSkill(player, event.getItemAmount(), event.getItemType());
//    }
//}
