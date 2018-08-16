package com.forgestorm.spigotcore.features.optional.minigame.core.score;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.score.statlisteners.StatListener;
import com.forgestorm.spigotcore.util.text.Console;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 6/22/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */

public class StatManager {

    private final GameManager gameManager = GameManager.getInstance();
    private final Map<Player, Map<StatType, Double>> playerStats = new HashMap<>();
    private final List<StatListener> statListeners = new ArrayList<>();

    /**
     * Register all stats that the game will listen to during the game play.
     *
     * @param statType A list of StatTypes to listen to.
     */
    public void initStats(List<StatType> statType) {
        // Build stat type lists for scores.
        List<Player> players = Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.hasMetadata("NPC"))
                .filter(player -> !gameManager.getPlayerMinigameManager().getPlayerProfileData(player).isSpectator())
                .collect(Collectors.toList());


        // For each player register a statType and default value
        for (Player player : players) {
            Map<StatType, Double> statsForPlayer = new HashMap<>();
            for (StatType data : statType) statsForPlayer.put(data, 0.0);
            playerStats.put(player, statsForPlayer);
        }

        // Save stat stat listeners
        for (StatType stat : statType) {
            StatListener statListener = stat.registerListener();

            // Automatically register event handlers
            Bukkit.getPluginManager().registerEvents(statListener, SpigotCore.PLUGIN);

            statListeners.add(statListener);
        }
    }

    /**
     * This will add 1 point for a given stat point.
     *
     * @param statType The stat type we want to add a point for.
     * @param player   The player who we are adding points for.
     */
    public void addStat(StatType statType, Player player) {
        addStat(statType, player, 1);
    }

    /**
     * This will add a variant amount of stat points to a given stat.
     *
     * @param statType The stat type we want to add a point for.
     * @param player   The player who we are adding points for.
     * @param amount   The amount of points to add.
     */
    public void addStat(StatType statType, Player player, double amount) {

        Map<StatType, Double> stats = playerStats.get(player);
        stats.replace(statType, stats.get(statType) + amount);
        playerStats.replace(player, stats);
    }

    /**
     * Unregister listeners from minigame.
     */
    public void deregisterListeners() {
        statListeners.forEach(StatListener::deregister);
        statListeners.forEach(HandlerList::unregisterAll); // Unregisters events
    }

    /**
     * This will take all the given stat points and update the database.
     * TODO: Actually update the database with the new stat points.
     */
    public void updateDatabase() {
        playerStats.forEach((player, stats) ->
                stats.forEach((statType, amount) -> Console.sendMessage(player.getName() + " : " + statType + "=" + amount))
        );
    }
}
