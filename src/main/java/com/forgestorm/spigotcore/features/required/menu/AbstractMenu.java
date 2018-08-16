package com.forgestorm.spigotcore.features.required.menu;

import com.forgestorm.spigotcore.constants.InventorySize;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
@Getter
public abstract class AbstractMenu {
    /**
     * Key: Inventory Slot
     * Value: The menu item for that slot.
     */
    private final Map<Integer, MenuItem> menuItemMap = new HashMap<>();

    private String menuName;
    private InventorySize inventorySize;

    public AbstractMenu(String menuName, InventorySize inventorySize) {
        this.menuName = menuName;
        this.inventorySize = inventorySize;

        setupMenuItems();
    }

    protected void setMenuItem(int slot, ItemStack itemStack, ClickAction clickAction) {
        menuItemMap.put(slot, new MenuItem(itemStack, clickAction));
    }

    public abstract void setupMenuItems();
}
