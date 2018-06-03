package com.forgestorm.spigotcore.features.required.menu;

import org.bukkit.entity.Player;

public interface ClickAction {
    void onPlayerClick(Player player, MenuClickType menuClickType);
}
