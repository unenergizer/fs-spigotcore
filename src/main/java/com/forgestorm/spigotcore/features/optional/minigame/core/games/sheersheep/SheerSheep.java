package com.forgestorm.spigotcore.features.optional.minigame.core.games.sheersheep;

import com.forgestorm.spigotcore.features.optional.minigame.MinigameFramework;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.games.Minigame;
import com.forgestorm.spigotcore.features.optional.minigame.core.games.sheersheep.kits.ExplosiveShears;
import com.forgestorm.spigotcore.features.optional.minigame.core.games.sheersheep.kits.KnifeParty;
import com.forgestorm.spigotcore.features.optional.minigame.core.score.StatType;
import com.forgestorm.spigotcore.features.optional.minigame.core.scoreboard.ArenaPointsCounter;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.kit.Kit;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.team.Team;
import com.forgestorm.spigotcore.util.math.RandomChance;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class SheerSheep extends Minigame {

    private final int maxScore = 80;
    private SpawnSheep spawnSheep;
    private ArenaPointsCounter arenaPointsCounter;

    public SheerSheep(MinigameFramework plugin) {
        super(plugin);
    }

    @Override
    public void setupGame() {
        arenaPointsCounter = new ArenaPointsCounter(maxScore, "Wool Collected");
        arenaPointsCounter.addAllPlayers();

        spawnSheep = new SpawnSheep();
        spawnSheep.run();
    }

    @Override
    public void disableGame() {
        // Cancel threads
        spawnSheep.cancelRunnable();

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
    public List<Kit> getKits() {
        List<Kit> kits = new ArrayList<>();

        kits.add(new ExplosiveShears());
        kits.add(new KnifeParty());

        return kits;
    }

    @Override
    public List<Team> getTeams() {
        List<Team> team = new ArrayList<>();
        team.add(new Team(
                0,
                "Individual Team",
                ChatColor.GREEN,
                -1,
                EntityType.SHEEP,
                Material.BOOKSHELF,
                new String[]{"Every player from themselves!!", "Compete to be the best!"}));
        return team;
    }

    @Override
    public List<StatType> getStatTypes() {
        List<StatType> statTypes = new ArrayList<>();
        statTypes.add(StatType.PICKUP_WOOL);
        return statTypes;
    }

    @Override
    public List<String> getGamePlayTips() {
        ArrayList<String> tips = new ArrayList<>();
        tips.add("Run around as fast as you can to shear the sheeps.");
        tips.add("Right click with your sheers to shear a sheep.");
        tips.add("The first person to get " + maxScore + " wool wins!");
        return tips;
    }

    @Override
    public List<String> getGamePlayRules() {
        ArrayList<String> rules = new ArrayList<>();
        rules.add("Run around as fast as you can to shear the sheeps.");
        rules.add("Right click with your sheers to shear a sheep.");
        rules.add("The first person to get " + maxScore + " wool wins!");
        return rules;
    }

    @EventHandler
    public void onEntitySheer(PlayerShearEntityEvent event) {
        Sheep sheep = (Sheep) event.getEntity();
        Location sheepEyeLocation = sheep.getEyeLocation();
        World world = Bukkit.getWorld(GameManager.getInstance().getCurrentArenaWorldData().getWorldName());
        Random rn = new Random();
        int randCount = RandomChance.randomInt(1, 5);

        world.spawnParticle(Particle.EXPLOSION_LARGE, sheepEyeLocation, 1);
        world.playSound(sheepEyeLocation, Sound.ENTITY_SHEEP_DEATH, .7f, .7f);
        world.playSound(sheepEyeLocation, Sound.ENTITY_GENERIC_EXPLODE, .6f, .5f);

        for (int i = 0; i <= randCount; i++) {
            Item item = world.dropItem(sheepEyeLocation, new ItemStack(Material.BONE));
            item.setVelocity(new Vector(rn.nextDouble() - 0.5, rn.nextDouble() / 2.0 + 0.3, rn.nextDouble() - 0.5).multiply(0.4));
        }

        sheep.setHealth(0);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemPickUp(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack().getType() != Material.WOOL) return;
        event.setCancelled(false);
        Player player = event.getPlayer();
        int amount = event.getItem().getItemStack().getAmount();
        arenaPointsCounter.addScore(player, amount);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Sheep)) return;
        event.setCancelled(false);
    }
}
