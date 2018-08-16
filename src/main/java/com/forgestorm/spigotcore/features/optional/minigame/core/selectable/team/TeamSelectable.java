package com.forgestorm.spigotcore.features.optional.minigame.core.selectable.team;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.optional.minigame.constants.MinigameMessages;
import com.forgestorm.spigotcore.features.optional.minigame.constants.PedestalLocations;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.LobbySelectable;
import com.forgestorm.spigotcore.features.optional.minigame.player.PlayerMinigameData;
import com.forgestorm.spigotcore.features.optional.minigame.world.PedestalMapping;
import com.forgestorm.spigotcore.features.required.database.global.player.data.GlobalPlayerData;
import com.forgestorm.spigotcore.util.display.Hologram;
import com.forgestorm.spigotcore.util.scoreboard.ScoreboardManager;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 8/17/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */

public class TeamSelectable extends LobbySelectable {

    private final Map<LivingEntity, Team> teamEntities = new HashMap<>();
    private final Map<Team, Hologram> teamHolograms = new HashMap<>();

    @Override
    public void setup() {
        List<Team> teamsList = GameManager.getInstance().getGameSelector().getMinigame().getTeamList();

        // Determine the number of teams and get the center pedestal locations appropriately.
        PedestalMapping pedestalMapping = new PedestalMapping();
        int shiftOver = pedestalMapping.getShiftAmount(teamsList.size());

        int teamsSpawned = 0;

        // Spawn the teams
        for (Team team : teamsList) {

            // Get platform location
            PedestalLocations pedLoc = PedestalLocations.values()[9 + shiftOver + teamsSpawned];
            pedestalLocations.add(pedLoc);

            // Spawn platform
            platformBuilder.setPlatform(GameManager.getInstance().getGameSelector().getMinigame().getLobbyWorld(), pedLoc, team.getTeamPlatformMaterials());

            // Spawn entities
            LivingEntity entity = spawnSelectableEntity(
                    team.getTeamColor() + team.getTeamName(),
                    team.getTeamEntityType(),
                    pedLoc,
                    PlayerRanks.MINIGAME_TEAM);

            // Add the team selection entities UUID's to an array list.
            // This is used to keep track of which one is being clicked for team selection.
            teamEntities.put(entity, team);

            // Spawn holograms
            spawnHolograms(team, pedLoc.getLocation(GameManager.getInstance().getGameSelector().getMinigame().getLobbyWorld()));

            teamsSpawned++;
        }
    }

    @Override
    public void destroy() {

        // Remove entities
        for (LivingEntity entity : teamEntities.keySet()) {
            SpigotCore.PLUGIN.getScoreboardManager().removeEntityFromTeam(entity, PlayerRanks.MINIGAME_TEAM.getScoreboardTeamName());
            entity.remove();
        }

        // Remove platforms
        platformBuilder.clearPlatform(GameManager.getInstance().getGameSelector().getMinigame().getLobbyWorld(), pedestalLocations);

        // Remove Holograms
        for (Hologram hologram : teamHolograms.values()) hologram.remove();

        // Unregister listeners
        PlayerKickEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);

        // Clear list and maps
        teamEntities.clear();
        teamHolograms.clear();
    }

    @Override
    public void toggleInteract(Player player, Entity entity) {
        if (!teamEntities.containsKey(entity)) return;

        PlayerMinigameData playerMinigameData = gameManager.getPlayerMinigameManager().getPlayerProfileData(player);
        Team clickedTeam = teamEntities.get(entity);
        Team currentTeam = playerMinigameData.getSelectedTeam();
        Team queuedTeam = playerMinigameData.getQueuedTeam();
        String sameTeamMessage = "";

        boolean clickedSameTeam = false;
        if (currentTeam != null) {
            if (currentTeam.equals(clickedTeam)) {
                clickedSameTeam = true;
            }
        }

        boolean clickedSameQueuedTeam = false;
        if (queuedTeam != null) {
            if (queuedTeam.equals(clickedTeam)) {
                clickedSameQueuedTeam = true;
            }
        }

        //If the player has interacted with a team they are on, add a little message to the description.
        if (clickedSameTeam) {
            sameTeamMessage = " " + MinigameMessages.TEAM_ALREADY_ON_TEAM.toString();
        } else if (clickedSameQueuedTeam) {
            sameTeamMessage = " " + MinigameMessages.TEAM_ALREADY_ON_QUEUE.toString();
        }

        //Set the the players team.
        joinTeam(player, clickedTeam);

        //Set player a confirmation message.
        player.sendMessage("");
        player.sendMessage(MinigameMessages.GAME_BAR_TEAM.toString());
        player.sendMessage("");
        player.sendMessage(CenterChatText.centerChatMessage(ChatColor.GRAY + "Team: " +
                clickedTeam.getTeamColor() + clickedTeam.getTeamName() + sameTeamMessage));
        player.sendMessage("");

        for (String description : clickedTeam.getTeamDescription()) {
            String message = CenterChatText.centerChatMessage(ChatColor.YELLOW + description);
            player.sendMessage(message);
        }

        player.sendMessage("");
        player.sendMessage(MinigameMessages.GAME_BAR_BOTTOM.toString());
        player.sendMessage("");

        //Play a confirmation sound.
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, .5f, .6f);

        //Update the lobby scoreboard.
        gameManager.getGameLobby().getTarkanLobbyScoreboard().updatePlayerTeam(player, playerMinigameData);
    }

    /**
     * Holograms are spawned above entity heads
     *
     * @param team     The team we are setting up a hologram for.
     * @param location The location to spawn the holograms.
     */
    private void spawnHolograms(Team team, Location location) {
        List<String> hologramText = new ArrayList<>();
        int queuedCount = team.getQueuedPlayers().size();
        int currentTeamSize = team.getTeamPlayers().size();
        int maxTeamSize;

        if (team.getTeamSizes() == -1) {
            maxTeamSize = gameManager.getMaxPlayersOnline();
        } else {
            maxTeamSize = team.getTeamSizes();
        }

        hologramText.add(ChatColor.BOLD + "Players: " + Integer.toString(currentTeamSize) + " / " + maxTeamSize);
        hologramText.add(ChatColor.BOLD + "Queued Players: " + queuedCount);

        Hologram hologram = new Hologram(hologramText, location.add(.5, 3.1, .5));
        hologram.spawnHologram();

        teamHolograms.put(team, hologram);
    }

    /**
     * Update holograms after a queue update or a team enter.
     */
    private void updateHolograms() {
        // Run name change one tick later to fix strange bug.
        // The bug that happens is one player will see the updated
        // name but the others will not. Really strange.
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Team, Hologram> entry : teamHolograms.entrySet()) {
                    Team team = entry.getKey();
                    Hologram hologram = entry.getValue();
                    int queuedCount = team.getQueuedPlayers().size();
                    int currentTeamSize = team.getTeamPlayers().size();
                    int maxTeamSize;

                    if (team.getTeamSizes() == -1) {
                        maxTeamSize = gameManager.getMaxPlayersOnline();
                    } else {
                        maxTeamSize = team.getTeamSizes();
                    }

                    hologram.changeText(ChatColor.BOLD + "Players: " + Integer.toString(currentTeamSize) + " / " + maxTeamSize, 0);
                    hologram.changeText(ChatColor.BOLD + "Queued Players: " + queuedCount, 1);
                }
            }
        }.runTaskLater(SpigotCore.PLUGIN, 1);
    }

    /**
     * Updates the players prefix above their head and in the players Tab list.
     *
     * @param player The player to set a scoreboard prefix for.
     * @param team   The team the player is joining.
     */
    private void updatePlayerPrefix(Player player, Team team) {
        ScoreboardManager scoreboardManager = SpigotCore.PLUGIN.getScoreboardManager();
        GlobalPlayerData globalPlayerData = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player);

        // Make sure the scoreboard teamname is unique for the staff player.
        // This is done because staff can have the free rank (Usergroup 0) but
        // we want them to have a different tag than an actual free player.
        String staff = "";
        if (globalPlayerData.getPlayerAccount().isAdmin()) {
            staff = "A";
        }
        if (globalPlayerData.getPlayerAccount().isModerator()) {
            staff = "M";
        }

        //String teamName = ChatColor.stripColor((Integer.toString(globalPlayerData.getUserGroup()) + staff + team.getTeamName()).replace(" ", "_"));
        String teamName = ChatColor.stripColor(globalPlayerData.getPlayerAccount().getRank().getScoreboardTeamName() + staff + team.getTeamName()).replace(" ", "_");
        String teamPrefix = globalPlayerData.getPlayerAccount().getRank().getUsernamePrefix();

        // Remove any previous tag
        scoreboardManager.removePlayer(player);

        // Add them to the new team
        scoreboardManager.addPlayer(player, teamName, teamPrefix, "");

        Console.sendMessage("TeamName: " + teamName + ChatColor.RESET + " TeamPrefix: " + teamPrefix + " " + player.getName());
    }

    /**
     * This will select the players default team.
     *
     * @param player The player to setup.
     */
    public void initPlayer(Player player) {
        PlayerMinigameData playerMinigameData = gameManager.getPlayerMinigameManager().getPlayerProfileData(player);
        Team smallestTeam = null;
        Team teamToJoin = null;

        for (Team team : GameManager.getInstance().getGameSelector().getMinigame().getTeamList()) {
            // Try to enter empty team first.
            if (team.getTeamPlayers().size() == 0) {
                teamToJoin = team;
                break;
            } else {
                if (smallestTeam != null) {
                    if (team.getTeamPlayers().size() < smallestTeam.getTeamPlayers().size()) {
                        smallestTeam = team;
                    }
                } else {
                    smallestTeam = team;
                }
            }
        }

        if (teamToJoin == null) {
            teamToJoin = smallestTeam;
        }

        assert teamToJoin != null;

        teamToJoin.getTeamPlayers().add(player);
        playerMinigameData.setSelectedTeam(teamToJoin);

        // Team enter message.
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "You have joined the " +
                teamToJoin.getTeamColor() + ChatColor.BOLD + teamToJoin.getTeamName() + ChatColor.GREEN + " team.");

        // Update the prefix next to the players name.
        updatePlayerPrefix(player, teamToJoin);

        // Update the holograms
        updateHolograms();
    }

    /**
     * This will attempt to add players to a team and if it is not yet possible, we
     * will add them to a teams queue.
     *
     * @param player The player who wants to switch teams.
     * @param team   The team the player wants to enter.
     */
    private void joinTeam(Player player, Team team) {
        // Update any current queues!
        updateTeamJoinQueues();

        PlayerMinigameData playerMinigameData = gameManager.getPlayerMinigameManager().getPlayerProfileData(player);
        Team lastTeam = playerMinigameData.getSelectedTeam();
        Team lastQueuedTeam = playerMinigameData.getQueuedTeam();

        if (team == lastTeam) return; // cancel joining same team
        if (lastQueuedTeam != null || team == lastQueuedTeam) return; // cancel joining queued team again

        if (team.getTeamPlayers().size() < team.getTeamSizes() || team.getTeamSizes() == -1) {
            // Remove from last team or team queue.
            if (lastQueuedTeam != null) {
                lastQueuedTeam.getQueuedPlayers().remove(player);
            }
            if (lastTeam != null) lastTeam.getTeamPlayers().remove(player);
            playerMinigameData.setQueuedTeam(null);

            // Add players to this team.
            team.getTeamPlayers().add(player);
            playerMinigameData.setSelectedTeam(team);

            // Team enter message.
            player.sendMessage("");
            player.sendMessage(Text.color("&aYou have joined the " +
                    team.getTeamColor() + "&l" + team.getTeamName() + "&a team."));

        } else {
            // Remove from last team queue.
            lastQueuedTeam.getQueuedPlayers().remove(player);

            // Setup new queue
            team.getQueuedPlayers().add(player);
            playerMinigameData.setQueuedTeam(team);

            // Team queue message.
            player.sendMessage("");
            player.sendMessage(Text.color("&eYou have joined the " +
                    team.getTeamColor() + "&l" + team.getTeamName() + "&e queue."));
        }

        // Update the prefix next to the players name.
        updatePlayerPrefix(player, team);

        // Update the Holograms above the team entities.
        updateHolograms();
    }

    /**
     * This code is used to update and current team queues.  If players can move to a team,
     * we will make that happen.
     */
    private void updateTeamJoinQueues() {
        for (Team team : GameManager.getInstance().getGameSelector().getMinigame().getTeamList()) {
            if (team.getQueuedPlayers().isEmpty()) return;

            if (team.getTeamPlayers().size() < team.getTeamSizes()) {

                Player player = team.getQueuedPlayers().remove(); // Grabs next queued player
                PlayerMinigameData playerMinigameData = gameManager.getPlayerMinigameManager().getPlayerProfileData(player);

                // Remove queue team
                playerMinigameData.setQueuedTeam(null);

                // Join the team
                team.getTeamPlayers().add(player);
                playerMinigameData.setSelectedTeam(team);

                // Team enter message.
                player.sendMessage("");
                player.sendMessage(ChatColor.GREEN + "You have joined the " +
                        team.getTeamColor() + ChatColor.BOLD + team.getTeamName() + ChatColor.GREEN + " team.");
            }
        }
    }

    /**
     * Code to remove the player from queues and team lists.
     *
     * @param player The player who left the server.
     */
    private void playerQuit(Player player) {
        // Remove the player from team lists.
        for (Team team : GameManager.getInstance().getGameSelector().getMinigame().getTeamList()) {
            team.getTeamPlayers().remove(player);
            team.getDeadPlayers().remove(player);
            team.getQueuedPlayers().remove(player);
        }

        // Update queues and holograms.
        updateTeamJoinQueues();
        updateHolograms();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerQuit(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        playerQuit(event.getPlayer());
    }
}
