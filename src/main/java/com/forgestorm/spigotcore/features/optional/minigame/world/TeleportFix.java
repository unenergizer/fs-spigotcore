package com.forgestorm.spigotcore.features.optional.minigame.world;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 6/5/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 *
 * ADDITIONAL CODE:
 * Teleport Fix By: https://gist.github.com/Zidkon/3779464
 */
class TeleportFix implements Listener {


    public TeleportFix() {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    public void onDisable() {
        PlayerTeleportEvent.getHandlerList().unregister(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        final Player player = event.getPlayer();
        final int visibleDistance = Bukkit.getViewDistance() * 16;

        int TELEPORT_FIX_DELAY = 15;
        new BukkitRunnable() {
            @Override
            public void run() {
                // Refresh nearby clients
                final List<Player> nearby = getPlayersWithin(player, visibleDistance);

                // Hide every player
                if (player.hasMetadata("NPC")) return;
                updateEntities(player, nearby, false);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (player.hasMetadata("NPC")) return;
                        updateEntities(player, nearby, true);
                    }
                }.runTaskLater(SpigotCore.PLUGIN, 1);

            }
        }.runTaskLater(SpigotCore.PLUGIN, TELEPORT_FIX_DELAY);
    }

    private void updateEntities(Player tpedPlayer, List<Player> players, boolean visible) {
        // Hide or show every player to tpedPlayer
        // and hide or show tpedPlayer to every player.

        boolean isSpectator = GameManager.getInstance().getPlayerMinigameManager().getPlayerProfileData(tpedPlayer).isSpectator();
        for (Player player : players) {
            if (visible) {
                tpedPlayer.showPlayer(player);
                if (!isSpectator) player.showPlayer(tpedPlayer);
            } else {
                tpedPlayer.hidePlayer(player);
                player.hidePlayer(tpedPlayer);
            }
        }
    }

    private List<Player> getPlayersWithin(Player player, int distance) {
        List<Player> res = new ArrayList<>();
        int d2 = distance * distance;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player && onlinePlayer.getWorld() == player.getWorld() && onlinePlayer.getLocation().distanceSquared(player.getLocation()) <= d2) {
                res.add(onlinePlayer);
            }
        }
        return res;
    }
}