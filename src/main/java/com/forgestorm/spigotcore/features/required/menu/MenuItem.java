package com.forgestorm.spigotcore.features.required.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class MenuItem {
    private ItemStack menuItem;
    private ClickAction clickAction;
}
