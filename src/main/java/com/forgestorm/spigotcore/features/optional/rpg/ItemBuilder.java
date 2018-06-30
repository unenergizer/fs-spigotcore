package com.forgestorm.spigotcore.features.optional.rpg;

import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.text.Text;
import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    public ItemStack buildArmor(ItemQuality itemQuality, ItemLevel itemLevel) {
        Map<Attribute, Double> baseAttributes = new HashMap<>();
        Map<Attribute, Double> extraAttributes = new HashMap<>();
        List<String> loreList = new ArrayList<>();

        ItemStack armorBase = getRandomArmorItemStack(itemLevel);

        // Add base attributes
        baseAttributes.put(Attribute.HEALTH, 3447887.0);

        // Add extra attributes
        extraAttributes.put(Attribute.DODGE, 234234.0);

        // TODO: Generate item value

        // Set Item Description Meta
        ItemMeta itemMeta = armorBase.getItemMeta();

        itemMeta.setDisplayName(Text.color(itemQuality.getName() + " " + armorBase.getType().name().toLowerCase()));
        loreList.add(""); // Add blank line

        for (Map.Entry<Attribute, Double> entry : baseAttributes.entrySet()) {
            loreList.add(ChatColor.GRAY + entry.getKey().name() + " " + entry.getValue());
        }

        if (!extraAttributes.isEmpty()) {
            for (Map.Entry<Attribute, Double> entry : extraAttributes.entrySet()) {
                loreList.add(ChatColor.GREEN + entry.getKey().name() + " " + entry.getValue());
            }
        }

        itemMeta.setLore(loreList);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        armorBase.setItemMeta(itemMeta);

        // Set NBTI Item Data
        NBTItem nbtItem = new NBTItem(armorBase);

        for (Map.Entry<Attribute, Double> entry : baseAttributes.entrySet()) {
            nbtItem.setDouble(entry.getKey().name(), entry.getValue());
        }
        for (Map.Entry<Attribute, Double> entry : extraAttributes.entrySet()) {
            nbtItem.setDouble(entry.getKey().name(), entry.getValue());
        }

        return nbtItem.getItem();
    }

    // TODO
    public ItemStack buildWeapon(ItemLevel itemLevel) {
        return new ItemStack(Material.FIRE);
    }

    private ItemStack getRandomArmorItemStack(ItemLevel itemLevel) {
        String randSuffix = "";
        int rand = RandomChance.randomInt(1, 4);

        if (rand == 1) {
            randSuffix = "BOOTS";
        } else if (rand == 2) {
            randSuffix = "CHESTPLATE";
        } else if (rand == 3) {
            randSuffix = "HELMET";
        } else if (rand == 4) {
            randSuffix = "LEGGINGS";
        }

        return new ItemStack(Material.valueOf(itemLevel.name() + "_" + randSuffix));
    }
}
