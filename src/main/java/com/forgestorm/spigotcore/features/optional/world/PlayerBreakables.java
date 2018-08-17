package com.forgestorm.spigotcore.features.optional.world;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.math.RandomChance;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerBreakables implements FeatureOptional {

    private static final List<Material> BREAKABLES = new ArrayList<>();

    static {
        // Add items that players can destroy.
        BREAKABLES.add(Material.LONG_GRASS);
        BREAKABLES.add(Material.DEAD_BUSH);
//        BREAKABLES.add(Material.DOUBLE_PLANT);
        BREAKABLES.add(Material.BROWN_MUSHROOM);
        BREAKABLES.add(Material.RED_MUSHROOM);
        BREAKABLES.add(Material.RED_ROSE);
        BREAKABLES.add(Material.SAPLING);
        BREAKABLES.add(Material.SUGAR_CANE);
        BREAKABLES.add(Material.SUGAR_CANE_BLOCK);
        BREAKABLES.add(Material.YELLOW_FLOWER);
        BREAKABLES.add(Material.CHORUS_FLOWER);
        BREAKABLES.add(Material.WATER_LILY);
        BREAKABLES.add(Material.THIN_GLASS);
        BREAKABLES.add(Material.STAINED_GLASS);
        BREAKABLES.add(Material.STAINED_GLASS_PANE);
    }

    @Override
    public void onFeatureEnable(boolean manualEnable) {
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!BREAKABLES.contains(event.getBlock().getType())) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        Block block = event.getBlock();

        //noinspection deprecation
        SpigotCore.PLUGIN.getBlockRegenerationManager().setBlock(
                block.getType(),
                block.getData(),
                Material.AIR,
                block.getLocation(),
                10 + RandomChance.randomInt(1, 60));
    }
}
