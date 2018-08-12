package com.forgestorm.spigotcore.features.optional.minigame.commands;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.features.optional.minigame.constants.ArenaState;
import com.forgestorm.spigotcore.features.optional.minigame.constants.MinigameMessages;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.location.GameLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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

@CommandAlias("minigame|mg")
public class MinigameAdminCommands extends FeatureOptionalCommand {

    private static final String ERROR = MinigameMessages.ALERT.toString();
    private final GameManager gameManager = GameManager.getInstance();


    @Override
    public void setupCommand(PaperCommandManager paperCommandManager) {

    }

    @Subcommand("start")
    public void start(CommandSender commandSender) {
        GameLobby gameLobby = gameManager.getGameLobby();

        if (!gameManager.isInLobby()) {
            commandSender.sendMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_START_ERROR_01.toString());
            return;
        }

        if (!gameManager.shouldMinigameStart()) {
            commandSender.sendMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_START_ERROR_02.toString());
            String playerCount = Integer.toString(Bukkit.getOnlinePlayers().size());
            commandSender.sendMessage(ERROR + ChatColor.AQUA + "Run " + ChatColor.DARK_AQUA +
                    "/mg minp " + playerCount + ChatColor.AQUA + " to change the minimal players to start.");
            return;
        }

        if (gameLobby.getCountdown() <= 2) {
            commandSender.sendMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_START_ERROR_03.toString());
            return;
        }

        gameLobby.setCountdown(1);
        Bukkit.broadcastMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_START.toString());
    }

    @Subcommand("stop")
    public void stop(CommandSender commandSender) {
        ArenaState arenaState = gameManager.getGameArena().getArenaState();

        if (arenaState == ArenaState.ARENA_EXIT || arenaState == ArenaState.ARENA_SHOW_SCORES) {
            commandSender.sendMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_STOP_ERROR_01.toString());
            return;
        }

        gameManager.getGameSelector().getMinigame().endMinigame();
        Bukkit.broadcastMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_STOP.toString());
    }

    @Subcommand("minimumplayers|minplayers|minp")
    public void setMinPlayers(CommandSender commandSender, int minPlayers) {
        int oldMin = gameManager.getMinPlayersToStartGame();
        gameManager.setMinPlayersToStartGame(minPlayers);
        commandSender.sendMessage(MinigameMessages.ADMIN.toString() +
                ChatColor.YELLOW + "Set minimum players to " + minPlayers + " from " + oldMin + ".");
    }

    @Subcommand("maximumplayers|maxplayers|maxp")
    public void setMaxPlayers(CommandSender commandSender, int maxPlayers) {
        int oldMax = gameManager.getMaxPlayersOnline();
        gameManager.setMaxPlayersOnline(commandSender, maxPlayers);
        commandSender.sendMessage(MinigameMessages.ADMIN.toString() +
                ChatColor.YELLOW + "Set maximum players to " + maxPlayers + " from " + oldMax + ".");
    }

}
