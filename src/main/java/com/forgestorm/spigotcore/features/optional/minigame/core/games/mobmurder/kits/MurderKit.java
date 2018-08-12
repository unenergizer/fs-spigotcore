package com.forgestorm.spigotcore.features.optional.minigame.core.games.mobmurder.kits;

import com.forgestorm.spigotcore.features.optional.minigame.core.selectable.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

public class MurderKit extends Kit {

    public MurderKit() {
        super("Slash the Mob", ChatColor.AQUA, EntityType.BLAZE, Material.BEDROCK, new String[]{"This is a dirty kit."});
    }

    @Override
    public void giveKit(Player player) {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);

        player.getInventory().setItem(0, sword);
    }
}
