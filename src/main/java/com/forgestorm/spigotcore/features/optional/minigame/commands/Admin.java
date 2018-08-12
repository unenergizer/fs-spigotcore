package com.forgestorm.spigotcore.features.optional.minigame.commands;

import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.features.optional.minigame.MinigameFramework;
import com.forgestorm.spigotcore.features.optional.minigame.constants.ArenaState;
import com.forgestorm.spigotcore.features.optional.minigame.constants.MinigameMessages;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.location.GameLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

public class Admin implements CommandExecutor {

    private static final String ERROR = MinigameMessages.ALERT.toString();
    private final MinigameFramework plugin;
    private final GameManager gameManager = GameManager.getInstance();

    public Admin(MinigameFramework plugin) {
        this.plugin = plugin;
    }

    private static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    private static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        // Send blank string ahead of other messages if it's a player commandSender.
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            player.sendMessage("");

            // Make sure the player is an admin and can run these commands.
//            if (!plugin.getSpigotCore().getProfileManager().getProfile(player).isAdmin()) {
            if (!commandSender.isOp()) {
                player.sendMessage(MinigameMessages.ALERT.toString() + MinigameMessages.COMMAND_ADMIN_NOT_OP.toString());
                CommonSounds.ACTION_FAILED.play(player);
                return false;
            }
        }

        //Check command args
        if (args.length == 1) {

            switch (args[0].toLowerCase()) {
                case "start":
                    GameLobby gameLobby = gameManager.getGameLobby();

                    if (!gameManager.isInLobby()) {
                        commandSender.sendMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_START_ERROR_01.toString());
                        return false;
                    }

                    if (!gameManager.shouldMinigameStart()) {
                        commandSender.sendMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_START_ERROR_02.toString());
                        String playerCount = Integer.toString(Bukkit.getOnlinePlayers().size());
                        commandSender.sendMessage(ERROR + ChatColor.AQUA + "Run " + ChatColor.DARK_AQUA +
                                "/mga minp " + playerCount + ChatColor.AQUA + " to change the minimal players to start.");
                        return false;
                    }

                    if (gameLobby.getCountdown() <= 2) {
                        commandSender.sendMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_START_ERROR_03.toString());
                        return false;
                    }

                    gameLobby.setCountdown(1);
                    Bukkit.broadcastMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_START.toString());
                    break;
                case "stop":
                    ArenaState arenaState = gameManager.getGameArena().getArenaState();

                    if (arenaState == ArenaState.ARENA_EXIT || arenaState == ArenaState.ARENA_SHOW_SCORES) {
                        commandSender.sendMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_STOP_ERROR_01.toString());
                        return false;
                    }

                    gameManager.getGameSelector().getMinigame().endMinigame();
                    Bukkit.broadcastMessage(ERROR + MinigameMessages.COMMAND_ADMIN_FORCE_STOP.toString());
                    break;
            }
        } else if (args.length == 2) {

            switch (args[0].toLowerCase()) {
                case "minimumplayers":
                case "minplayers":
                case "minp":
                    if (!isInteger(args[1])) {
                        commandSender.sendMessage(ERROR + ChatColor.RED + "" + ChatColor.BOLD + "Please enter a valid number!");
                        return false;
                    }
                    int oldMin = gameManager.getMinPlayersToStartGame();
                    int newMin = Integer.parseInt(args[1]);
                    gameManager.setMinPlayersToStartGame(newMin);
                    commandSender.sendMessage(MinigameMessages.ADMIN.toString() +
                            ChatColor.YELLOW + "Set minimum players to " + newMin + " from " + oldMin + ".");
                    break;
                case "maximumplayers":
                case "maxplayers":
                case "maxp":
                    if (!isInteger(args[1])) {
                        commandSender.sendMessage(ERROR + ChatColor.RED + "" + ChatColor.BOLD + "Please enter a valid number!");
                        return false;
                    }
                    int oldMax = gameManager.getMaxPlayersOnline();
                    int newMax = Integer.parseInt(args[1]);
                    if (!gameManager.setMaxPlayersOnline(commandSender, newMax)) return false;
                    commandSender.sendMessage(MinigameMessages.ADMIN.toString() +
                            ChatColor.YELLOW + "Set maximum players to " + newMax + " from " + oldMax + ".");
                    break;
            }

        } else {
            commandSender.sendMessage(ERROR + MinigameMessages.COMMAND_ADMIN_UNKNOWN.toString());
        }

        return false;
    }
}
