package com.forgestorm.spigotcore.features.optional.rpg.armor;

import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.rpg.ItemBuilder;
import com.forgestorm.spigotcore.features.optional.rpg.ItemLevel;
import com.forgestorm.spigotcore.features.optional.rpg.ItemQuality;
import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorManager implements FeatureOptional {

    private final ArmorListener armorListener = new ArmorListener();

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        armorListener.onEnable();
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
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
