package com.forgestorm.spigotcore.features.optional.minigame.core.games.oitc;

import com.forgestorm.spigotcore.features.optional.minigame.MinigameFramework;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.games.Minigame;
import com.forgestorm.spigotcore.features.optional.minigame.core.games.oitc.kits.BasicKit;
import com.forgestorm.spigotcore.features.optional.minigame.core.score.StatType;
import com.forgestorm.spigotcore.features.optional.minigame.core.scoreboard.ArenaPointsCounter;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.kit.Kit;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.team.Team;
import com.forgestorm.spigotcore.features.optional.minigame.player.PlayerMinigameData;
import com.forgestorm.spigotcore.util.math.RandomChance;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 8/6/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */

public class OneInTheChamber extends Minigame {

    private final int maxScore = 10;
    private ArenaPointsCounter arenaPointsCounter;

    public OneInTheChamber(MinigameFramework plugin) {
        super(plugin);
    }

    @Override
    public void setupGame() {
        cancelPVP = false;
        arenaPointsCounter = new ArenaPointsCounter(maxScore, "Players Killed");
        arenaPointsCounter.addAllPlayers();
    }

    @Override
    public void disableGame() {
        // This can be null if the game ends during the tutorial stage.
        if (arenaPointsCounter != null) {
            arenaPointsCounter.removeAllPlayers();
        }

        // Unregister listeners
        HandlerList.unregisterAll(this);
    }

    @Override
    public World getLobbyWorld() {
        return Bukkit.getWorld("world");
    }

    @Override
    public List<String> getGamePlayTips() {
        ArrayList<String> tips = new ArrayList<>();
        tips.add("Use your bow to shoot enemy players.");
        tips.add("You get one arrow for each enemy player you kill.");
        tips.add("The first person to get " + maxScore + " kills wins!");
        return tips;
    }

    @Override
    public List<String> getGamePlayRules() {
        ArrayList<String> rules = new ArrayList<>();
        rules.add("Use your bow to shoot enemy players.");
        rules.add("You get one arrow for each enemy player you kill.");
        rules.add("The first person to get " + maxScore + " kills wins!");
        return rules;
    }

    @Override
    public List<Kit> getKits() {
        List<Kit> kits = new ArrayList<>();

        kits.add(new BasicKit());

        return kits;
    }

    @Override
    public List<Team> getTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team(
                0,
                "Player vs Player",
                ChatColor.BLUE,
                8,
                EntityType.DONKEY,
                Material.STONE,
                new String[]{"Kill other players to get the best score possible!"}));
        return teams;
    }

    @Override
    public List<StatType> getStatTypes() {
        List<StatType> scoreData = new ArrayList<>();
        scoreData.add(StatType.FIRST_KILL);
        return scoreData;
    }

    /**
     * Gives the player some ammo. Usually called when a
     * player gets a kill.
     *
     * @param player The player who will get ammo.
     */
    private void giveAmmo(Player player) {
        Inventory playerInv = player.getInventory();
        int arrows = 0;

        //Get the players current arrow count.
        if (player.getInventory().getItem(2) != null) {
            arrows = playerInv.getItem(2).getAmount();
        }

        ItemStack arrow = new ItemStack(Material.ARROW, 1 + arrows);
        player.getInventory().setItem(2, arrow);

    }

    /**
     * Respawns a killed player to a random location.
     *
     * @param player The player to respawn.
     */
    private void respawnPlayer(Player player) {
        // Heal the player
        player.setHealth(20);
        player.setFoodLevel(20);

        // Give Kit Items
        PlayerMinigameData playerMinigameData = GameManager.getInstance().getPlayerMinigameManager().getPlayerProfileData(player);
        playerMinigameData.getSelectedKit().giveKit(player);

        // Get random location
        List<Location> locationList = GameManager.getInstance().getTeamSpawnLocations().get(0).getLocations();
        int index = RandomChance.randomInt(1, locationList.size());

        // Send player to this random location!
        player.teleport(locationList.get(index - 1));
        player.sendMessage(ChatColor.RED + "You were killed! Sending you to next spawn position.");
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (player.getHealth() - event.getDamage() <= 1) {
            event.setCancelled(true);
            respawnPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player defender = (Player) event.getEntity();
        double hitPoints = defender.getHealth();
        double damage = event.getDamage();

        if (event.getDamager() instanceof Arrow) {
            /////////////////////////////////////////////////////////////
            /// Player killed by arrow
            /////////////////////////////////////////////////////////////

            Arrow arrow = (Arrow) event.getDamager();

            if (!(arrow.getShooter() instanceof Player)) return;

            Player damager = (Player) arrow.getShooter();

            //Make sure the player did not shoot themselves.
            if (damager.equals(defender)) return;
            event.setCancelled(true);

            //Give damager a point.
            arenaPointsCounter.addScore(damager, 1);
            giveAmmo(damager);

            //Respawn the defender.
            respawnPlayer(defender);

        } else if (event.getDamager() instanceof Player) {
            /////////////////////////////////////////////////////////////
            /// If player kills player with weapon (besides bow & arrow)
            /////////////////////////////////////////////////////////////

            Player damager = (Player) event.getDamager();

            if (hitPoints - damage > 1) return;
            event.setCancelled(true);

            //Give damager a point.
            arenaPointsCounter.addScore(damager, 1);
            giveAmmo(damager);

            //Respawn the defender.
            respawnPlayer(defender);

        } else {
            /////////////////////////////////////////////////////////////
            /// Player hurt themselves
            /////////////////////////////////////////////////////////////

            if (hitPoints - damage > 1) return;
            event.setCancelled(true);

            //Respawn the defender.
            respawnPlayer(defender);
        }
    }
}
