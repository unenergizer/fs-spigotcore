package com.forgestorm.spigotcore.features.optional.minigame.core.menus;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 8/5/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */

class SpectatorPlayerTracker {
    // TODO: IMPLEMENT MENU
//public class SpectatorPlayerTracker extends Menu {
//
//    public SpectatorPlayerTracker(MinigameFramework plugin) {
//        super(plugin.getSpigotCore());
//        init("Player Tracker", getRowsNeeded());
//        makeMenuItems();
//    }
//
//    /**
//     * Generates the number of rows needed for the Player Tracker menu.
//     *
//     * @return The amount of rows we need for the menu.
//     */
//    private int getRowsNeeded() {
//        int playerCount = 0;
//        for (Player players : Bukkit.getOnlinePlayers()) {
//            if (GameManager.getInstance().getPlayerMinigameManager().getPlayerProfileData(players).isSpectator())
//                continue;
//            playerCount++;
//        }
//        return 1 + (playerCount / 9);
//    }
//
//    @Override
//    protected void makeMenuItems() {
//        int i = 0;
//        for (Player targetPlayer : Bukkit.getOnlinePlayers()) {
//            if (GameManager.getInstance().getPlayerMinigameManager().getPlayerProfileData(targetPlayer).isSpectator())
//                continue;
//            setItem(createPlayerSkull(targetPlayer), i++, new TeleportPlayer(targetPlayer));
//        }
//    }
//
//    /**
//     * This will create a player head item stack.
//     *
//     * @param player The player we need to make a head for.
//     * @return The ItemStack of the player head.
//     */
//    private ItemStack createPlayerSkull(Player player) {
//        //Make the skull item.
//        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
//        SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
//        skullMeta.setOwner(player.getName());
//        skullMeta.setDisplayName(player.getName());
//        skull.setItemMeta(skullMeta);
//
//        return skull;
//    }
}
