package com.forgestorm.spigotcore.features.optional.minigame.core.location;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.events.PlayerTeleportSpawnEvent;
import com.forgestorm.spigotcore.features.optional.minigame.constants.MinigameMessages;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.location.access.LobbyAccess;
import com.forgestorm.spigotcore.features.optional.minigame.core.scoreboard.TarkanLobbyScoreboard;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.kit.KitSelectable;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.team.TeamSelectable;
import com.forgestorm.spigotcore.features.optional.minigame.world.PlatformBuilder;
import com.forgestorm.spigotcore.util.display.BossBarUtil;
import com.forgestorm.spigotcore.util.display.TipAnnouncer;
import com.forgestorm.spigotcore.util.text.Console;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 8/14/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */
@Getter
public class GameLobby extends GameLocation {

    private final PlatformBuilder platformBuilder = new PlatformBuilder();
    private final BossBarUtil bar = new BossBarUtil(MinigameMessages.BOSS_BAR_LOBBY_MESSAGE.toString());
    private final int maxCountdown = 30;
    private TarkanLobbyScoreboard tarkanLobbyScoreboard;
    //    private DoubleJump doubleJump;
    private TipAnnouncer tipAnnouncer;
    private KitSelectable kitSelectable;
    private TeamSelectable teamSelectable;
    private boolean countdownStarted = false;
    @Setter
    private int countdown = maxCountdown;
//    private SkillToggle skillToggle;

    @Override
    public void setupGameLocation() {
        // Initialize needed classes
        tarkanLobbyScoreboard = new TarkanLobbyScoreboard();
//     TODO:   doubleJump = new DoubleJump(plugin);

        // Kit Setup
        kitSelectable = new KitSelectable();
        kitSelectable.onEnable();

        // Team Setup
        teamSelectable = new TeamSelectable();
        teamSelectable.onEnable();

        // Display core tips
        tipAnnouncer = new TipAnnouncer(GameManager.getInstance().getGameSelector().getMinigame().getGamePlayTipsList());

        // Register Listeners
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        // TODO: Enable Skills
//        skillToggle = new SkillToggle(plugin.getSpigotCore());
//        skillToggle.enableSkills();

        // Set weather
        World world = Bukkit.getWorlds().get(0);
        world.setStorm(false);
        world.setWeatherDuration(0);

        // Start BukkitRunnable thread
        startCountdown();

        // Setup players
        allPlayersJoin(new LobbyAccess());
    }

    @Override
    public void destroyGameLocation() {
        // Destroy previously needed classes
        stopCountdown();

        // Destroy Kit Manager
        kitSelectable.onDisable();

        // Destroy Team Manager
        teamSelectable.onDisable();

        // Stop core play tips.
        tipAnnouncer.setShowTips(false);

        // TODO: Test Skill Implementation
//        skillToggle.disableSkills();
//        Console.sendMessage(Boolean.toString(skillToggle.isSkillsEnabled()));

        // Unregister stat listeners
        HandlerList.unregisterAll(this);

        // All players quit
        allPlayersQuit(new LobbyAccess());
    }

    @Override
    protected void showCountdown() {

        // Update TarkanScoreBoard animation
        tarkanLobbyScoreboard.animateScoreboard();

        // Do lobby countdown
        if (!gameManager.isInLobby()) return;

        // Do lobby countdown.
        if (gameManager.shouldMinigameStart()) {
            if (!countdownStarted)
                Console.sendMessage("GameLobby - performLobbyCountdown() -> Countdown started!");

            countdownStarted = true;

            // Show countdown message to the players.
            if (countdown == 30 || countdown == 20 || countdown == 10 || countdown <= 5 && countdown > 1) {
                String message = MinigameMessages.GAME_TIME_REMAINING_PLURAL.toString();

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.hasMetadata("NPC")) return;
                    SpigotCore.PLUGIN.getTitleManager().sendActionbar(players, message.replace("%s", Integer.toString(countdown)));
                    players.playSound(players.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
                }
            } else if (countdown <= 1) {

                String message = MinigameMessages.GAME_TIME_REMAINING_SINGULAR.toString();

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.hasMetadata("NPC")) return;
                    SpigotCore.PLUGIN.getTitleManager().sendActionbar(players, message);
                    players.playSound(players.getLocation(), Sound.BLOCK_NOTE_HARP, 1f, 1f);
                }

                //Do one last check to make sure the core should start.
                gameManager.switchToArena();
                countdown = maxCountdown;
            }

            countdown--;
        } else if (countdownStarted) {
            Console.sendMessage("GameLobby - performLobbyCountdown() -> Countdown canceled!");

            // If the countdown message does not equal the maxCountdown time,
            // then it is safe to assume that a countdown started, but then a
            // player left the server, thus stopping the countdown. Since this
            // happened, lets reset the countdown!
            if (countdown != maxCountdown) {
                // Reset defaults
                countdownStarted = false;
                countdown = maxCountdown;

                // Send countdown fail message.
                String message = MinigameMessages.GAME_COUNTDOWN_NOT_ENOUGH_PLAYERS.toString();
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.hasMetadata("NPC")) return;

                    //Show message
                    SpigotCore.PLUGIN.getTitleManager().sendActionbar(players, message);

                    //Play notification sound.
                    players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BASS, 1F, .5F);
                }
            }
        }
    }

    /**
     * Called when a player jumps into the void or into a end portal.
     * This will send them back to the main spawn position.
     *
     * @param player The player to teleport.
     */
    public void sendToSpawn(Player player) {
        Console.sendMessage("GameLobby - sendToSpawn()");
        //Teleport the player.
        Bukkit.getServer().getPluginManager().callEvent(new PlayerTeleportSpawnEvent(player));
    }

    /**
     * We listen to the EntityCombustEvent to prevent entities
     * from catching fire in the lobby. This usually happens
     * when we use a Zombie or a Skeleton as a kit or team
     * entity.
     *
     * @param event This is a Bukkit EntityCombustEvent.
     */
    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }

    /**
     * We listen to the FoodLevelChangeEvent to prevent players
     * food levels from to prevent them from starving and/or
     * loosing hunger.
     *
     * @param event This is a Bukkit FoodLevelChangeEvent.
     */
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    /**
     * We listen to the ItemSpawnEvent to watch for and prevent
     * the spawning of eggs. A egg will spawn from chickens who
     * are used as kit or as team entities.
     *
     * @param event This is a Bukkit ItemSpawnEvent.
     */
    @EventHandler
    public void onEggSpawn(ItemSpawnEvent event) {
        //Prevent chickens from laying eggs.
        if (event.getEntity().getItemStack().getType() == (Material.EGG)) {
            event.getEntity().remove();
            event.setCancelled(true);
        }
    }

    /**
     * We listen to the PlayerDropItemEvent to make sure players
     * are not dropping the WATCH device which will take them
     * back to the main hub.
     *
     * @param event This is a Bukkit PlayerDropItemEvent.
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.WATCH) event.setCancelled(true);
    }

    /**
     * We listen to the WeatherChangeEvent to prevent the world
     * from having weather changes.  The rain can become really
     * annoying to players, so we will disable that functionality
     * here.
     *
     * @param event This is a Bukkit WeatherChangeEvent.
     */
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) event.setCancelled(true);
    }
}
