package com.forgestorm.spigotcore.features.required.menu;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.util.text.Console;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MenuManager implements FeatureRequired, Listener {

    private final Map<Class, AbstractMenu> menuMap = new HashMap<>();
    private final Map<Player, AbstractMenu> activeMenus = new ConcurrentHashMap<>();
    private final Map<Player, ShiftClickFixer> shiftClickedItemStackMap = new HashMap<>();
    private boolean canOpenMenus = false;

    private BukkitTask menuTickTask;

    @Override
    public void onServerStartup() {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        canOpenMenus = true;

        menuTickTask = new BukkitRunnable() {
            @Override
            public void run() {
                inventoryPulse();
            }
        }.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    public void onServerShutdown() {
        canOpenMenus = false;
        menuTickTask.cancel();

        for (Player player : Bukkit.getOnlinePlayers()) {
            removePlayer(player);
        }

        menuMap.clear();
        activeMenus.clear();
        shiftClickedItemStackMap.clear();

        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);
        InventoryInteractEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerKickEvent.getHandlerList().unregister(this);
    }

    /**
     * For any active inventory that requires pulse updating, we perform that action here.
     * Example use is a menu that shows how many players are on the server. The pulse could
     * keep the player count updated.
     */
    private void inventoryPulse() {
        for (Map.Entry<Player, AbstractMenu> entry : activeMenus.entrySet()) {
            if (!(entry.getValue() instanceof MenuTick)) continue;
            ((MenuTick) entry.getValue()).onMenuTick(entry.getKey(), entry.getKey().getOpenInventory().getTopInventory());
        }
    }

    /**
     * Adds an Abstract menu for player use.
     *
     * @param abstractMenu The menu to add.
     */
    public void addMenu(AbstractMenu abstractMenu) {
        menuMap.put(abstractMenu.getClass(), abstractMenu);
    }

    /**
     * Removes a menu. This menu can no longer be opened.
     * If a player has this menu open, we close it.
     *
     * @param abstractMenu The menu to remove.
     */
    public void removeMenu(Class abstractMenu) {
        for (Map.Entry<Player, AbstractMenu> entry : activeMenus.entrySet()) {
            if (entry.getValue().getClass() != abstractMenu) continue;
            entry.getKey().closeInventory();
            activeMenus.remove(entry.getKey());
        }
        menuMap.remove(abstractMenu);
    }

    /**
     * Opens a menu for a player.
     *
     * @param player     The player to show the menu to.
     * @param menuToOpen The menu we wish to open.
     */
    public void openMenu(Player player, Class menuToOpen) {
        if (!canOpenMenus) return;
        AbstractMenu abstractMenu = menuMap.get(menuToOpen);

        if (menuToOpen == null) return;

        Inventory inventory = Bukkit.createInventory(null, abstractMenu.getInventorySize().getSize(), ChatColor.BOLD + abstractMenu.getMenuName());

        for (Map.Entry<Integer, MenuItem> entrySet : abstractMenu.getMenuItemMap().entrySet()) {
            inventory.setItem(entrySet.getKey(), entrySet.getValue().getMenuItem());
        }

        activeMenus.put(player, abstractMenu);
        player.openInventory(inventory);
    }

    /**
     * Removes the player from teh ActiveMenuList.
     *
     * @param player The player we want to remove.
     */
    private void removePlayer(Player player) {
        activeMenus.remove(player);

        // Attempts to fix shift+clicking item into inventory
        // !!! Be careful as this might be a way to dupe items !!!
        if (!shiftClickedItemStackMap.containsKey(player)) return;
        ShiftClickFixer shiftClickFixer = shiftClickedItemStackMap.get(player);

        if (player.getInventory().getItem(shiftClickFixer.inventorySlot) == null
                || player.getInventory().getItem(shiftClickFixer.inventorySlot).getType() == Material.AIR) {
            player.getInventory().setItem(shiftClickFixer.inventorySlot, shiftClickFixer.itemStack);

            // Never comment out the below console text. Keep records of item replacements.
            // Must watch for suspicious activity. Need to prevent dupes.
            Console.sendMessage("[MenuManager] Restored player item: " + shiftClickFixer.itemStack.getType().toString());
            Console.sendMessage("[MenuManager] For Player: " + player.getName());
            Console.sendMessage("[MenuManager] Slot: " + shiftClickFixer.inventorySlot);
        }
        shiftClickedItemStackMap.remove(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        //noinspection SuspiciousMethodCalls
        if (!activeMenus.containsKey(event.getWhoClicked())) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        event.getAction();

        // Attempt to fix the shift+clicking of items into the inventory.
        if (event.getClickedInventory().getType() != InventoryType.CHEST) {
            if (event.isShiftClick()) {
                shiftClickedItemStackMap.put(player, new ShiftClickFixer(event.getCursor(), event.getRawSlot()));
            }
        }

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;

        AbstractMenu abstractMenu = activeMenus.get(player);

        if (!abstractMenu.getMenuItemMap().containsKey(event.getRawSlot())) return; // Clicked blank spot

        ClickAction clickAction = abstractMenu.getMenuItemMap().get(event.getRawSlot()).getClickAction();

        if (event.isLeftClick() && !event.isShiftClick()) clickAction.onPlayerClick(player, MenuClickType.LEFT_CLICK);
        if (event.isRightClick() && !event.isShiftClick()) clickAction.onPlayerClick(player, MenuClickType.RIGHT_CLICK);
        if (event.isShiftClick() && event.isLeftClick())
            clickAction.onPlayerClick(player, MenuClickType.SHIFT_LEFT_CLICK);
        if (event.isShiftClick() && event.isRightClick())
            clickAction.onPlayerClick(player, MenuClickType.SHIFT_RIGHT_CLICK);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        //noinspection SuspiciousMethodCalls
        if (activeMenus.containsKey(event.getWhoClicked())) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        //noinspection SuspiciousMethodCalls
        if (activeMenus.containsKey(event.getWhoClicked())) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        removePlayer((Player) event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        removePlayer(event.getPlayer());
    }

    /**
     * Holds data that contains what the player shift+clicked last.
     */
    @AllArgsConstructor
    private class ShiftClickFixer {
        private final ItemStack itemStack;
        private final int inventorySlot;
    }
}
