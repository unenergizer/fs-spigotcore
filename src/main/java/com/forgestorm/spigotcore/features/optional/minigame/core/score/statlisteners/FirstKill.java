package com.forgestorm.spigotcore.features.optional.minigame.core.score.statlisteners;

import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.features.optional.minigame.core.score.StatType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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

public class FirstKill implements StatListener {

    private boolean firstKillRegistered = false;

    @Override
    public void register() {
    }

    @Override
    public void deregister() {
    }

    @EventHandler
    public void onEntityDeath(EntityDamageByEntityEvent event) {
        if (firstKillRegistered) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player defender = (Player) event.getEntity();
        if (defender.getHealth() - event.getDamage() <= 1) {
            firstKillRegistered = true;
            GameManager.getInstance().getStatManager().addStat(StatType.FIRST_KILL, damager);
            Bukkit.broadcastMessage(ChatColor.YELLOW + damager.getName() + " got the first kill!");
        }
    }
}
