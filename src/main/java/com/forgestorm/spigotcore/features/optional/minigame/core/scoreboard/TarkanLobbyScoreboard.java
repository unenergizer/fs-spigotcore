package com.forgestorm.spigotcore.features.optional.minigame.core.scoreboard;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.Messages;
import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.events.UpdateScoreboardEvent;
import com.forgestorm.spigotcore.features.optional.minigame.MinigameFramework;
import com.forgestorm.spigotcore.features.optional.minigame.constants.MinigameMessages;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.location.GameLobby;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.kit.Kit;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.team.Team;
import com.forgestorm.spigotcore.features.optional.minigame.player.PlayerMinigameData;
import com.forgestorm.spigotcore.features.required.database.global.player.data.GlobalPlayerData;
import com.forgestorm.spigotcore.util.math.exp.PlayerExperience;
import com.forgestorm.spigotcore.util.text.Console;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 6/2/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be
 * reproduced, distributed, or transmitted in any form or by any means,
 * including photocopying, recording, or other electronic or mechanical methods,
 * without the prior written permission of the owner.
 */

public class TarkanLobbyScoreboard implements Listener {

    private final MinigameFramework plugin;
    private final GameManager gameManager;
    private final GameLobby gameLobby;
    private int gameWaitingAnimate = 1;
    private TitleManagerAPI titleManagerAPI = SpigotCore.PLUGIN.getTitleManager();
    private PlayerExperience expCalc = new PlayerExperience();


    public TarkanLobbyScoreboard() {
        gameManager = GameManager.getInstance();

        plugin = GameManager.getInstance().getPlugin();
        gameLobby = gameManager.getGameLobby();

        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    /**
     * Adds a player and gives them a scoreboard.
     *
     * @param player The player that will receive a scoreboard.
     */
    public void addPlayer(Player player) {
        if (titleManagerAPI.hasScoreboard(player)) return; // Prevent duplicate scoreboard adds.
        Console.sendMessage("TarkanLobbyScoreboard - addPlayer(" + player.getDisplayName() + ")");

        // Tarkan board setup
        titleManagerAPI.giveScoreboard(player);

        //Setup player's scoreboard.
        setBoardData(player);
        updatePlayerCountAndGameStatus(Bukkit.getOnlinePlayers().size());

        // Set scoreboard title
        titleManagerAPI.setScoreboardTitle(player, Messages.SCOREBOARD_TITLE.toString());
    }

    /**
     * Removes a player scoreboard..
     *
     * @param player The player that will have their scoreboard removed.
     */
    public void removePlayer(Player player) {
        if (!titleManagerAPI.hasScoreboard(player)) return;
        Console.sendMessage("TarkanLobbyScoreboard - removePlayer(" + player.getDisplayName() + ")");

        titleManagerAPI.removeScoreboard(player);
    }

    /**
     * This updates the player counts on peoples scoreboard when
     * players enter or exit the server.
     *
     * @param currentPlayers The player count to display in the
     *                       scoreboard
     */
    public void updatePlayerCountAndGameStatus(int currentPlayers) {
        if (Bukkit.getOnlinePlayers().size() < 1) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("NPC")) return;
            Console.sendMessage("TarkanLobbyScoreboard - updatePlayerCountAndGameStatus(" + player.getDisplayName() + ")");
            String maxPlayers = Integer.toString(gameManager.getMaxPlayersOnline());

            // Game status
            if (gameManager.shouldMinigameStart()) {
                titleManagerAPI.setScoreboardValue(player, 7, MinigameMessages.TSB_STATUS.toString() +
                        MinigameMessages.SB_GAME_STATUS_READY.toString());
            } else {
                titleManagerAPI.setScoreboardValue(player, 7, MinigameMessages.TSB_STATUS.toString() +
                        MinigameMessages.SB_GAME_STATUS_WAITING_1.toString());
            }

            // Player count
            String pCount = Integer.toString(currentPlayers) + ChatColor.GRAY + "/" + ChatColor.RESET + maxPlayers;
            titleManagerAPI.setScoreboardValue(player, 8, MinigameMessages.TSB_PLAYERS.toString() + pCount);
        }
    }

    /**
     * Updates the scoreboard with the players new Kit selection.
     *
     * @param player             The player who's scoreboard we will update.
     * @param playerMinigameData The players minigame data that contains the new kit they clicked.
     */
    public void updatePlayerKit(Player player, PlayerMinigameData playerMinigameData) {
        Console.sendMessage("TarkanLobbyScoreboard - updatePlayerKit(" + player.getDisplayName() + ")");
        Kit kit = playerMinigameData.getSelectedKit();
        String kitName = kit.getKitColor() + kit.getKitName();
        titleManagerAPI.setScoreboardValue(player, 11, trimString(MinigameMessages.TSB_KIT.toString() +
                kitName));
    }

    /**
     * Updates the scoreboard with the players new Team selection.
     *
     * @param player             The player who's scoreboard we will update.
     * @param playerMinigameData The players minigame data that contains the new team they clicked.
     */
    public void updatePlayerTeam(Player player, PlayerMinigameData playerMinigameData) {
        Console.sendMessage("TarkanLobbyScoreboard - updatePlayerTeam(" + player.getDisplayName() + ")");
        Team team = playerMinigameData.getSelectedTeam();
        String teamName = team.getTeamColor() + team.getTeamName();
        titleManagerAPI.setScoreboardValue(player, 10, trimString(MinigameMessages.TSB_TEAM.toString() +
                teamName));
    }

    /**
     * Set the scores for the lobby scoreboard.
     *
     * @param player The player that we are setting scores for.
     */
    private void setBoardData(Player player) {
        if (!titleManagerAPI.hasScoreboard(player)) return;
        Console.sendMessage("TarkanLobbyScoreboard - setBoardData(" + player.getDisplayName() + ")");

        GlobalPlayerData globalPlayerData = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player);
        PlayerMinigameData playerMinigameData = gameManager.getPlayerMinigameManager().getPlayerProfileData(player);
        String gameName = gameManager.getGameSelector().getMinigameType().getFriendlyName();

        // Blank line 1
        titleManagerAPI.setScoreboardValue(player, 1, Messages.SCOREBOARD_BLANK_LINE_1.toString());

        // GEMS
        titleManagerAPI.setScoreboardValue(player, 2, Messages.SCOREBOARD_GEMS.toString() +
                globalPlayerData.getPlayerEconomy().getGems());

        // LEVEL
        titleManagerAPI.setScoreboardValue(player, 3, Messages.SCOREBOARD_LEVEL.toString() +
                expCalc.getLevel(globalPlayerData.getPlayerAccount().getExperience()));

        // XP
        titleManagerAPI.setScoreboardValue(player, 4, Messages.SCOREBOARD_XP.toString() +
                expCalc.getPercentToLevel(globalPlayerData.getPlayerAccount().getExperience()) + "%");

        // Blank line 2
        titleManagerAPI.setScoreboardValue(player, 5, Messages.SCOREBOARD_BLANK_LINE_2.toString());

        // GAME
        titleManagerAPI.setScoreboardValue(player, 6, trimString(MinigameMessages.TSB_GAME.toString() +
                gameName));

        // STATUS
        titleManagerAPI.setScoreboardValue(player, 7, MinigameMessages.TSB_STATUS.toString());

        // PLAYERS
        titleManagerAPI.setScoreboardValue(player, 8, MinigameMessages.TSB_PLAYERS.toString());

        // Blank line 3
        titleManagerAPI.setScoreboardValue(player, 9, MinigameMessages.TSB_BLANK_LINE_3.toString());

        // TEAM
        Team team = playerMinigameData.getSelectedTeam();
        String teamName = team.getTeamColor() + team.getTeamName();
        titleManagerAPI.setScoreboardValue(player, 10, trimString(MinigameMessages.TSB_TEAM.toString() +
                teamName));

        // KIT
        Kit kit = playerMinigameData.getSelectedKit();
        String kitName = kit.getKitColor() + kit.getKitName();
        titleManagerAPI.setScoreboardValue(player, 11, trimString(MinigameMessages.TSB_KIT.toString() +
                kitName));

        // Blank line 4
        titleManagerAPI.setScoreboardValue(player, 12, MinigameMessages.TSB_BLANK_LINE_4.toString());

        // SERVER
        titleManagerAPI.setScoreboardValue(player, 13, Messages.SCOREBOARD_SERVER.toString());

        titleManagerAPI.setScoreboardValue(player, 14, Bukkit.getServer().getServerName());
    }

    /**
     * This will make sure a string is not longer than 14 characters.
     * If it is, we will shorten the string.
     *
     * @param input The string we want to trim.
     * @return The trimmed string.
     */
    private String trimString(String input) {
        Console.sendMessage("TarkanLobbyScoreboard - trimString()");
        final int maxWidth = 42; // Tarkan Scoreboard length is 42.

        // Check to see if the input length is greater than the maxWidth of characters.
        if (input.length() > maxWidth) {
            int amountOver = input.length() - maxWidth;
            return input.substring(0, input.length() - amountOver - 2) + "..";
        } else {
            // The input is less than 15 characters so it does not need to be trimmed.
            return input;
        }
    }

    private boolean shouldAnimate = false;

    /**
     * This will do a small scoreboard animation letting the player know
     * we are currently waiting on more players before the core is ready
     * to begin the initial countdown.
     */
    public void animateScoreboard() {
        if (gameManager.shouldMinigameStart() && !shouldAnimate) return;

        // Update animation frame.
        if (gameWaitingAnimate != 5) {
            gameWaitingAnimate++;
        } else {
            gameWaitingAnimate = 1;
        }

        if (gameLobby.isCountdownStarted() && gameLobby.getCountdown() <= 20) {
            shouldAnimate = true;

            // Loop through player list and update all players with new animation.
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!titleManagerAPI.hasScoreboard(player)) return;
                Console.sendMessage("TarkanLobbyScoreboard - animateScoreboard(" + player.getDisplayName() + ") -> Show countdown");

                // Update game countdown
                String msg = MinigameMessages.TSB_STATUS.toString() + ChatColor.GREEN + "Starting in " + ChatColor.YELLOW + gameLobby.getCountdown();
                titleManagerAPI.setScoreboardValue(player, 7, msg);

            }
        } else if (shouldAnimate) {
            shouldAnimate = false;
            // Loop through player list and update all players with new animation.
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!titleManagerAPI.hasScoreboard(player)) return;
                Console.sendMessage("TarkanLobbyScoreboard - animateScoreboard(" + player.getDisplayName() + ") -> Reset countdown");

                updatePlayerCountAndGameStatus(Bukkit.getOnlinePlayers().size());
            }
        }
    }

    @EventHandler
    public void onUpdateScoreboard(UpdateScoreboardEvent event) {
        Player player = event.getPlayer();

        if (gameManager.getPlayerMinigameManager().getPlayerProfileData(player).isSpectator()) return;
        if (titleManagerAPI.hasScoreboard(player)) {
            Console.sendMessage("TarkanLobbyScoreboard - onUpdateScoreboard(" + player.getDisplayName() + ")");
        }

        setBoardData(player);
    }

    @EventHandler
    public void onProfileLoad(GlobalProfileDataLoadEvent event) {
        Player player = event.getPlayer();

        if (gameManager.getPlayerMinigameManager().getPlayerProfileData(player).isSpectator()) return;
        if (!titleManagerAPI.hasScoreboard(player)) {
            Console.sendMessage("TarkanLobbyScoreboard - onProfileLoad(" + player.getDisplayName() + ")");
        }

        addPlayer(player);
    }
}
