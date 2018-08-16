package com.forgestorm.spigotcore.features.optional.minigame.core.location.access;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.location.GameLobby;
import com.forgestorm.spigotcore.features.optional.minigame.player.PlayerMinigameData;
import com.forgestorm.spigotcore.features.optional.minigame.world.TeleportFix2;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 8/12/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */

public class LobbyAccess implements AccessBehavior {

    private final GameManager gameManager = GameManager.getInstance();
    private final GameLobby gameLobby = gameManager.getGameLobby();

    @Override
    public void playerJoin(Player player) {
        PlayerMinigameData playerMinigameData = gameManager.getPlayerMinigameManager().getPlayerProfileData(player);

        // Set default kit
        playerMinigameData.setSelectedKit(gameManager.getGameSelector().getMinigame().getKitList().get(0));

        // Set default team
        gameLobby.getTeamSelectable().initPlayer(player);

        // Send the player the boss bar.
        gameLobby.getBar().showBossBar(player);

        // Lets change some player Bukkit/Spigot defaults
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);

        // Teleport the player to the main spawn.
        gameLobby.sendToSpawn(player);

        // Setup player for double jump.
        // TODO: gameLobby.getDoubleJump().setupPlayer(player);

        // Do teleport fix!
        TeleportFix2.fixTeleport(player);

        // Add the scoreboard if the players profile has been loaded in SpigotCore plugin.
        if (SpigotCore.PLUGIN.getGlobalDataManager().hasGlobalPlayerData(player))
            gameLobby.getTarkanLobbyScoreboard().addPlayer(player);
    }

    @Override
    public void playerQuit(Player player) {
        // Remove the scoreboard.
        gameLobby.getTarkanLobbyScoreboard().removePlayer(player);

        // Remove the boss bar.
        gameLobby.getBar().removeBossBar(player);

        // Remove player double jump.
        // TODO: gameLobby.getDoubleJump().removePlayer(player);
    }
}
