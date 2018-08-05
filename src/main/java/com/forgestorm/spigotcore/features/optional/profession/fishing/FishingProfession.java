package com.forgestorm.spigotcore.features.optional.profession.fishing;

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

public class FishingProfession {
//public class FishingProfession extends Profession {
//
//    public FishingProfession() {
//        super(YamlConfiguration.loadConfiguration(new File(FilePaths.PROFESSION_FISHING.toString())),
//                ProfessionType.FISHING);
//    }
//
//    @Override
//    public void onEnable() {
//        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
//    }
//
//    @Override
//    public void onDisable() {
//        PlayerFishEvent.getHandlerList().unregister(this);
//    }
//
//    /**
//     * Called when a particular fishing action is achieved.
//     *
//     * @param player The player who toggled the fishing profession.
//     */
//    private void toggleFishingProfession(Player player) {
//        PlayerProfileData playerProfileData = plugin.getProfileManager().getProfile(player);
//        long currentExperience = playerProfileData.getFishingExperience();
//        long currentLevel = experienceCalculator.getLevel(currentExperience);
//        long experienceGained = 0;
//        int tier = 0;
//
//        if (currentLevel < 20) {
//            //Tier 1 Fishing
//            tier = 1;
//            experienceGained = fileConfiguration.getLong(0 + ".exp");
//        } else if (currentLevel >= 20 && currentLevel < 40) {
//            //Tier 2 Fishing
//            tier = 2;
//            experienceGained = fileConfiguration.getLong(20 + ".exp");
//        } else if (currentLevel >= 40 && currentLevel < 60) {
//            //Tier 3 Fishing
//            tier = 3;
//            experienceGained = fileConfiguration.getLong(40 + ".exp");
//        } else if (currentLevel >= 60 && currentLevel < 80) {
//            //Tier 4 Fishing
//            tier = 4;
//            experienceGained = fileConfiguration.getLong(60 + ".exp");
//        } else if (currentLevel > 80) {
//            //Tier 5 Fishing
//            tier = 5;
//            experienceGained = fileConfiguration.getLong(80 + ".exp");
//        }
//
//        World world = player.getWorld();
//
//        // Fish drops
//        int fishType = RandomChance.randomInt(1, tier);
//        switch (fishType) {
//            case 1:
//                world.dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH));
//                break;
//            case 2:
//                //noinspection deprecation
//                world.dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 1, (short) 0, (byte) 1));
//                break;
//            case 3:
//                //noinspection deprecation
//                world.dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 1, (short) 0, (byte) 2));
//                break;
//            case 4:
//                //noinspection deprecation
//                world.dropItem(player.getLocation(), new ItemStack(Material.RAW_FISH, 1, (short) 0, (byte) 3));
//                break;
//            case 5:
//                world.dropItem(player.getLocation(), new ItemStack(Material.COOKED_FISH));
//                break;
//
//        }
//
//        // Drop Gems
//        dropGems(player, player.getLocation());
//
//        // Set the experience
//        playerProfileData.setFishingExperience(currentExperience + experienceGained);
//
//        // Show experience related messages.
//        showExperienceMessages(player, currentExperience, experienceGained);
//    }
//
//    @EventHandler
//    public void onPlayerFish(PlayerFishEvent event) {
//        event.setExpToDrop(0); // Prevent player from getting exp
//        PlayerFishEvent.State state = event.getState();
//        Player player = event.getPlayer();
//
//        // Fishing profession can only happen on "CAUGHT" states.
//        if (state != PlayerFishEvent.State.CAUGHT_FISH && state != PlayerFishEvent.State.CAUGHT_ENTITY) return;
//
//        // Call the event. If canceled, stop execution.
//        if (professionToggleEvent(player)) return;
//
//        // Toggle the fishing profession.
//        toggleFishingProfession(event.getPlayer());
//
//        // Remove the default fish. Will replace with our own.
//        event.getCaught().remove();
//    }
//
//    @Override
//    public String getUpgrades(int rankUpgradeLevel) {
//        String result = "";
//        if (rankUpgradeLevel == 20) {
//            result = "&aCatch Better Fish";
//        } else if (rankUpgradeLevel == 40) {
//            result = "&aCatch Better Fish";
//        } else if (rankUpgradeLevel == 60) {
//            result = "&aCatch Better Fish";
//        } else if (rankUpgradeLevel == 80) {
//            result = "&aCatch Better Fish";
//        } else if (rankUpgradeLevel == 100) {
//            result = "&aPrestige level 1";
//        }
//        return Text.color(result);
//    }
}
