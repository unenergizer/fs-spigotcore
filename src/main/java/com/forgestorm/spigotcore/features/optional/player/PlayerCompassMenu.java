package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.InventorySize;
import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.menu.AbstractMenu;
import com.forgestorm.spigotcore.features.required.menu.ClickAction;
import com.forgestorm.spigotcore.features.required.menu.MenuClickType;
import com.forgestorm.spigotcore.features.required.menu.MenuTick;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerCompassMenu implements FeatureOptional, Listener {
    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        SpigotCore.PLUGIN.getMenuManager().addMenu(new MainMenu());
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        SpigotCore.PLUGIN.getMenuManager().removeMenu(MainMenu.class);
        GlobalProfileDataLoadEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onProfileDataLoad(GlobalProfileDataLoadEvent event) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        compass.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        ItemMeta compassMeta = compass.getItemMeta();

        compassMeta.setDisplayName(Text.color("&c&lMain Game Menu"));
        List<String> lore = new ArrayList<>();
        lore.add(Text.color("&7A menu that allows you to join games, view your"));
        lore.add(Text.color("&7user profile, change your game settings, join"));
        lore.add(Text.color("&7your personal realm, get help, and much "));
        lore.add(Text.color("&7much more!"));
        lore.add(Text.color(" "));
        lore.add(Text.color("&aRight-Click&7: Opens the game menu."));
        lore.add(Text.color("&aShift + Left-Click Ground&7: Opens player realm."));
        lore.add(Text.color("&aShift + Right-Click Player&7: Add Builder to realm."));
        compassMeta.setLore(lore);

        compassMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        compassMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        compassMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        compassMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        compass.setItemMeta(compassMeta);

        event.getPlayer().getInventory().setItem(0, compass);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getPlayer().isSneaking()) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.COMPASS) return;
        SpigotCore.PLUGIN.getMenuManager().openMenu(player, MainMenu.class);
    }

    public class MainMenu extends AbstractMenu implements MenuTick {

        private int tickTest = 0;

        MainMenu() {
            super("Test Menu", InventorySize.ROWS_1);
        }

        @Override
        public void setupMenuItems() {
            ItemStack testItem = new ItemStack(Material.BED);
            setMenuItem(4, testItem, new TestAction());
        }

        @Override
        public void onMenuTick(Player player, Inventory inventory) {
            if (tickTest == 0) {
                inventory.setItem(8, new ItemStack(Material.COAL_ORE));
            }
            if (tickTest == 1) {
                inventory.setItem(8, new ItemStack(Material.DIAMOND_ORE));
            }
            if (tickTest == 2) {
                inventory.setItem(8, new ItemStack(Material.EMERALD_ORE));
            }
            if (tickTest == 3) {
                inventory.setItem(8, new ItemStack(Material.IRON_ORE));
            }

            tickTest++;
            if (tickTest >= 3) tickTest = 0;
        }
    }

    public class TestAction implements ClickAction {

        @Override
        public void onPlayerClick(Player player, MenuClickType menuClickType) {
            if (menuClickType == MenuClickType.LEFT_CLICK) player.sendMessage("Left click!");
            if (menuClickType == MenuClickType.RIGHT_CLICK) player.sendMessage("Right click!");
            if (menuClickType == MenuClickType.SHIFT_LEFT_CLICK) player.sendMessage("Shift-Left click!");
            if (menuClickType == MenuClickType.SHIFT_RIGHT_CLICK) player.sendMessage("Shift-Right click!");
        }
    }
}
