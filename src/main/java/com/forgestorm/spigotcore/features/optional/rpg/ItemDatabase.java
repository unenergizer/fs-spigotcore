package com.forgestorm.spigotcore.features.optional.rpg;

import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.text.Console;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemDatabase implements FeatureOptional, LoadsConfig {

    private final Map<Integer, AbstractItem> abstractItemMap = new HashMap<>();

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Console.sendMessage("&c[ItemDatabase] Total Items: " + abstractItemMap.size());

        for (AbstractItem item : abstractItemMap.values()) {
            Console.sendMessage("&c=====================");
            Console.sendMessage("&cname: " + item.name);
            Console.sendMessage("&cDescription: " + item.description);
            Console.sendMessage("&cMaterial: " + item.material.toString());
            Console.sendMessage("&cVersion: " + item.version);
        }
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        abstractItemMap.clear();
    }

    @Override
    public void loadConfiguration() {
        File file = new File(FilePaths.RPG_ITEM_DATABASE.toString());
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        Set<String> keys = config.getConfigurationSection("ItemID").getKeys(false);

        for (String itemID : keys) {
            String section = "ItemID." + itemID + ".";

            Console.sendMessage("Setting up #" + itemID);

            AbstractItem basicCitizen = new AbstractItem(
                    config.getString(section + "name"),
                    config.getString(section + "description"),
                    Material.valueOf(config.getString(section + "material")),
                    config.getInt(section + "version"));

            abstractItemMap.put(Integer.valueOf(itemID), basicCitizen);
        }
    }

    @AllArgsConstructor
    class AbstractItem {
        private String name;
        private String description;
        private Material material;
        private int version;
    }
}
