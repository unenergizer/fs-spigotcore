package com.forgestorm.spigotcore.features.required.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface MenuTick {
    void onMenuTick(Player player, Inventory inventory);
}
