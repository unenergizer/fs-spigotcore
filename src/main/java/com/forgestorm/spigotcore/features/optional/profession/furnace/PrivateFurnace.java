package com.forgestorm.spigotcore.features.optional.profession.furnace;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.util.math.TimeUnit;
import com.forgestorm.spigotcore.util.math.exp.Experience;
import com.forgestorm.spigotcore.util.math.exp.ProfessionExperience;
import com.forgestorm.spigotcore.util.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-spigotcore
 * DATE: 5/9/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 RetroMMO.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */

public class PrivateFurnace {
}
//public class PrivateFurnace extends BukkitRunnable implements Listener {
//
//    private static final int minutesAllowed = 12;
//    private static final int totalTimeAllowed = 60 * minutesAllowed; // 12 minutes
//    private static final int timeInterval = 1; // time to subtract
//    private final Experience experienceCalculator = new ProfessionExperience();
//    private boolean cancel = false;
//    private Map<Inventory, LockedFurnace> lockedFurnaces = new HashMap<>();
//
//    public void onEnable() {
//
//        // Register Listeners
//        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
//    }
//
//    /**
//     * This will disable the Private Furnace functionality.
//     */
//    public void onDisable() {
//        // Stop the BukkitRunnable
//        cancel = true;
//
//        InventoryOpenEvent.getHandlerList().unregister(this);
//    }
//
//    /**
//     * A test to check the if the player opening the furnace is the actual owner.
//     *
//     * @param player           The player who is attempting to open a furnace.
//     * @param furnaceInventory The furnace the player is trying to use.
//     * @return True if the player is the furnace owner, false otherwise.
//     */
//    private boolean isFurnaceOwner(Player player, Inventory furnaceInventory) {
//        return player.getUniqueId().equals(lockedFurnaces.get(furnaceInventory).getOwner().getUniqueId());
//    }
//
//    /**
//     * We call this when a player attempts to open a furnace. We check to see
//     * who the furnace owner is. If they are not the owner then we cancel the
//     * event. If no owner exist, a new lock is created and the furnace becomes
//     * private for the next 5 minutes.
//     *
//     * @param player           The player who is attempting to open a furnace.
//     * @param furnaceInventory The furnace the player is trying to use.
//     * @return True if the furnace should not be opened, false otherwise.
//     */
//    private boolean registerPrivateFurnace(Player player, Inventory furnaceInventory) {
//        int maxFurnaceCount = getMaxFurnaceCount(player);
//        int furnaceCount = 0;
//
//        // Check to make sure this furnace isn't already locked.
//        if (lockedFurnaces.containsKey(furnaceInventory)) {
//            if (!isFurnaceOwner(player, furnaceInventory)) {
//                player.sendMessage(ChatColor.RED + "This furnace is being used. Please use another one.");
//                CommonSounds.ACTION_FAILED.play(player);
//                return true; // Not the furnace owner, stop player from opening the furnace.
//            }
//        } else {
//
//            // Check to see if the player already has a furnace.
//            Iterator<Map.Entry<Inventory, LockedFurnace>> it = lockedFurnaces.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<Inventory, LockedFurnace> entry = it.next();
//
//                if (entry.getValue().getOwner().equals(player)) {
//                    furnaceCount++;
//
//                    if (furnaceCount == maxFurnaceCount) {
//                        player.sendMessage(ChatColor.RED + "You already have " + furnaceCount + " furnace(s). You can't use anymore.");
//                        player.sendMessage(ChatColor.YELLOW + "Level your profession to gain access to more furnaces.");
//                        CommonSounds.ACTION_FAILED.play(player);
//                        return true; // Prevent a player who already has a furnace to open a new one.
//                    } else {
//                        return false;// The player can have more furnaces.
//                    }
//                }
//            }
//
//            // Register new locked furnace.
//            lockedFurnaces.put(furnaceInventory, new LockedFurnace(player, totalTimeAllowed));
//            player.sendMessage(ChatColor.AQUA + "This is now private. Only you can open it.");
//            CommonSounds.ACTION_SUCCESS.play(player);
//        }
//        return false;
//    }
//
//    /**
//     * This determines the maximum amount of furnaces a player can use.
//     *
//     * @param player The player requesting a furnace.
//     * @return The number of furnaces allowed to be used.
//     */
//    private int getMaxFurnaceCount(Player player) {
//        PlayerProfileData playerProfileData = plugin.getProfileManager().getProfile(player);
//        int cookingLevel = experienceCalculator.getLevel(playerProfileData.getCookingExperience());
//        int smeltingLevel = experienceCalculator.getLevel(playerProfileData.getSmeltingExperience());
//
//        // Cooking
//        if (cookingLevel < 20) {
//            return 1;
//        } else if (cookingLevel >= 20 && cookingLevel < 40) {
//            return 2;
//        } else if (cookingLevel >= 40 && cookingLevel < 60) {
//            return 3;
//        } else if (cookingLevel >= 60 && cookingLevel < 80) {
//            return 4;
//        } else if (cookingLevel >= 80) {
//            return 5;
//        }
//
//        // Smelting
//        if (smeltingLevel < 20) {
//            return 1;
//        } else if (smeltingLevel >= 20 && smeltingLevel < 40) {
//            return 2;
//        } else if (smeltingLevel >= 40 && smeltingLevel < 60) {
//            return 3;
//        } else if (smeltingLevel >= 60 && smeltingLevel < 80) {
//            return 4;
//        } else if (smeltingLevel >= 80) {
//            return 5;
//        }
//        return 0;
//    }
//
//    @Override
//    public void run() {
//        // Stop the BukkitRunnable if the disable method is ran.
//        if (cancel) {
//            cancel();
//            return;
//        }
//
//        // Make sure map isn't empty
//        if (lockedFurnaces.isEmpty()) return;
//
//        // Adjust furnace time.
//        Iterator<Map.Entry<Inventory, LockedFurnace>> it = lockedFurnaces.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<Inventory, LockedFurnace> entry = it.next();
//            LockedFurnace lockedFurnace = entry.getValue();
//            Player player = lockedFurnace.getOwner();
//            int timeLeft = lockedFurnace.getTimeLeft();
//
//            if (timeLeft <= 0) {
//                player.sendMessage(ChatColor.RED + "Your furnace has been unlocked. Please remove your items.");
//                it.remove();
//
//            } else {
//                String time = TimeUnit.toString(timeLeft);
//
//                // Show countdown messages
//                if (timeLeft > 30) {
//                    // Once every 60 seconds
//                    if (timeLeft % 60 == 0) {
//                        sendTimeLeftMessage(player, time, false);
//                    }
//                }
//                if (timeLeft == 30 || timeLeft <= 10) {
//                    sendTimeLeftMessage(player, time, true);
//                }
//
//                // Decrement time
//                lockedFurnace.setTimeLeft(timeLeft - timeInterval);
//            }
//        }
//
//    }
//
//    /**
//     * Sends the player a count down message.
//     *
//     * @param player  Recipient of the message.
//     * @param time    The time left.
//     * @param warning If we should display red text or not.
//     */
//    private void sendTimeLeftMessage(Player player, String time, boolean warning) {
//        if (warning) {
//            String message = "&aYou have &c%s &auntil the furnace is unlocked.";
//            player.sendMessage(Text.color(message.replace("%s", time)));
//            CommonSounds.ACTION_FAILED.play(player);
//        } else {
//            String message = ChatColor.GREEN + "&aYou have &e%s &auntil the furnace is unlocked.";
//            player.sendMessage(Text.color(message.replace("%s", time)));
//        }
//    }
//
//    @EventHandler
//    public void onFurnaceOpen(InventoryOpenEvent event) {
//        Inventory inventory = event.getInventory();
//
//        if (inventory.getType() != InventoryType.FURNACE) return;
//        Player player = (Player) event.getPlayer();
//        Location playerLocation = player.getLocation();
//
//        // Returns true if the furnace should not be opened.
//        if (registerPrivateFurnace(player, inventory)) {
//            event.setCancelled(true);
//            return;
//        }
//        player.playSound(playerLocation, Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1F);
//    }
//
//    @Getter
//    @AllArgsConstructor
//    private class LockedFurnace {
//        private final Player owner;
//        @Setter
//        private int timeLeft;
//    }
//}
