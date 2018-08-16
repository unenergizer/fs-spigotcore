package com.forgestorm.spigotcore.features.optional.minigame.core.games.mobmurder;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.minigame.MinigameFramework;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.games.Minigame;
import com.forgestorm.spigotcore.features.optional.minigame.core.games.mobmurder.kits.MurderKit;
import com.forgestorm.spigotcore.features.optional.minigame.core.score.StatType;
import com.forgestorm.spigotcore.features.optional.minigame.core.scoreboard.ArenaPointsCounter;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.kit.Kit;
import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.team.Team;
import com.forgestorm.spigotcore.util.math.RandomChance;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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

public class MobMurder extends Minigame {

    private final int maxScore = 50;
    private final List<EntityType> allowedMobs = new ArrayList<>();
    private SpawnMobs spawnMobs;
    private ArenaPointsCounter arenaPointsCounter;
    private final Random rn = new Random();

    public MobMurder(MinigameFramework plugin) {
        super(plugin);
    }

    @Override
    public void setupGame() {
        // Add allowed mobs
        allowedMobs.add(EntityType.MUSHROOM_COW);
        allowedMobs.add(EntityType.PIG);
        allowedMobs.add(EntityType.COW);
        allowedMobs.add(EntityType.SHEEP);

        // Setup scoreboard
        arenaPointsCounter = new ArenaPointsCounter(maxScore, "Points Collected");
        arenaPointsCounter.addAllPlayers();

        // Spawn mobs
        spawnMobs = new SpawnMobs();
        spawnMobs.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    public void disableGame() {
        // Stop spawning mobs.
        spawnMobs.cancelRunnable();

        // This can be null if the game ends during the tutorial stage.
        if (arenaPointsCounter != null) {
            arenaPointsCounter.removeAllPlayers();
        }

        // Unregister listeners
        EntityDeathEvent.getHandlerList().unregister(this);
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
        CreatureSpawnEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
    }

    @Override
    public World getLobbyWorld() {
        return Bukkit.getWorld("world");
    }

    @Override
    public List<String> getGamePlayTips() {
        ArrayList<String> tips = new ArrayList<>();
        tips.add("Run around as fast as you can and kill the farm animals.");
        tips.add("Use your weapons to kill them.");
        tips.add("You get points for each animal you kill.");
        tips.add("The first person to get " + maxScore + " points wins!");
        return tips;
    }

    @Override
    public List<String> getGamePlayRules() {
        ArrayList<String> rules = new ArrayList<>();
        rules.add("Run around as fast as you can and kill the farm animals.");
        rules.add("Use your weapons to kill them.");
        rules.add("You get points for each animal you kill.");
        rules.add("The first person to get " + maxScore + " points wins!");
        return rules;
    }

    @Override
    public List<Kit> getKits() {
        List<Kit> kits = new ArrayList<>();
        kits.add(new MurderKit());
        return kits;
    }

    @Override
    public List<Team> getTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team(
                0,
                "Player vs Mobs",
                ChatColor.BLUE,
                8,
                EntityType.MUSHROOM_COW,
                Material.STONE,
                new String[]{"Kill mobs to get the best score possible!"}));
        return teams;
    }

    @Override
    public List<StatType> getStatTypes() {
        List<StatType> scoreData = new ArrayList<>();
        scoreData.add(StatType.FIRST_KILL);
        return scoreData;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        Player player = (Player) event.getDamager();
        String name = entity.getName();
        Location loc = entity.getEyeLocation();
        int randCount = RandomChance.randomInt(1, 5);

        if (name.contains("-")) {
            arenaPointsCounter.addScore(player, -5);

            Bukkit.getWorld(GameManager.getInstance().getCurrentArenaWorldData().getWorldName()).spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
            Bukkit.getWorld(GameManager.getInstance().getCurrentArenaWorldData().getWorldName()).playSound(loc, Sound.ENTITY_SHEEP_DEATH, .7f, .7f);
            Bukkit.getWorld(GameManager.getInstance().getCurrentArenaWorldData().getWorldName()).playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, .6f, .5f);

            for (int i = 0; i <= randCount; i++) {
                Item item = Bukkit.getWorld(GameManager.getInstance().getCurrentArenaWorldData().getWorldName()).dropItem(loc, new ItemStack(Material.BONE));
                item.setVelocity(new Vector(rn.nextDouble() - 0.5, rn.nextDouble() / 2.0 + 0.3, rn.nextDouble() - 0.5).multiply(0.4));
            }

            entity.setHealth(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {

        if ((event.getEntity() instanceof Player)) return;
        if (event.getEntity().getKiller() == null) return;

        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        String name = entity.getName();
        World world = Bukkit.getWorld(GameManager.getInstance().getCurrentArenaWorldData().getWorldName());
        Location loc = entity.getEyeLocation();
        int randCount = RandomChance.randomInt(1, 5);

        if (name.contains("+1")) {
            arenaPointsCounter.addScore(player, 1);
        } else if (name.contains("+2")) {
            arenaPointsCounter.addScore(player, 2);

        } else if (name.contains("+3")) {
            arenaPointsCounter.addScore(player, 3);

        } else if (name.contains("+4")) {
            arenaPointsCounter.addScore(player, 4);

        } else if (name.contains("+5")) {
            arenaPointsCounter.addScore(player, 5);
        }

        //Prevent exp drops.
        event.setDroppedExp(0);
        world.spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
        world.playSound(loc, Sound.ENTITY_SHEEP_DEATH, .7f, .7f);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, .6f, .5f);

        for (int i = 0; i <= randCount; i++) {
            Item item = entity.getWorld().dropItem(loc, new ItemStack(Material.BONE));
            item.setVelocity(new Vector(rn.nextDouble() - 0.5, rn.nextDouble() / 2.0 + 0.3, rn.nextDouble() - 0.5).multiply(0.4));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!allowedMobs.contains(event.getEntityType())) return;
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        switch (event.getBlock().getType()) {
            case LONG_GRASS:
            case DOUBLE_PLANT:
            case SUGAR_CANE_BLOCK:
                event.setCancelled(false);
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }
}
