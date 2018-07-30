package com.forgestorm.spigotcore.features.optional.rpg.armor;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.rpg.ItemBuilder;
import com.forgestorm.spigotcore.features.optional.rpg.ItemLevel;
import com.forgestorm.spigotcore.features.optional.rpg.ItemQuality;
import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorManager implements FeatureOptional, Listener {

    private ArmorListener armorListener = new ArmorListener();

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
        armorListener.onEnable();
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        ArmorEquipEvent.getHandlerList().unregister(this);
        armorListener.onDisable();
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        getItemStats(event.getPlayer(), event.getNewArmorPiece());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        setTestItemStats(event.getPlayer());
    }

    private void getItemStats(Player player, ItemStack itemStack) {
        NBTItem nbti = new NBTItem(itemStack);

        for (String keys : nbti.getKeys()) {
            player.sendMessage(ChatColor.YELLOW + keys + ": " + nbti.getDouble(keys));
        }
    }

    private void setTestItemStats(Player player) {
        ItemBuilder itemBuilder = new ItemBuilder();

        player.getInventory().setItem(30, itemBuilder.buildArmor(ItemQuality.LEGENDARY, ItemLevel.CHAINMAIL));
    }
}
