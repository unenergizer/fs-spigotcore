package com.forgestorm.spigotcore.features.optional.world;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Hidden paths are a Zelda like features. Players can use TNT to
 * blow up a particular wall revealing a hidden path. When using
 * the block regeneration manager, the blockRegen will respawn after
 * some scheduler.
 */
public class HiddenPaths implements FeatureOptional, Listener {

    private static final List<Material> EXPLODABLES = new ArrayList<>();

    static {
        // Add items that TNT can destroy.
        EXPLODABLES.add(Material.CLAY);
        EXPLODABLES.add(Material.LONG_GRASS);
        EXPLODABLES.add(Material.DEAD_BUSH);
        EXPLODABLES.add(Material.DOUBLE_PLANT);
        EXPLODABLES.add(Material.RED_MUSHROOM);
        EXPLODABLES.add(Material.RED_ROSE);
        EXPLODABLES.add(Material.SAPLING);
        EXPLODABLES.add(Material.SUGAR_CANE);
        EXPLODABLES.add(Material.SUGAR_CANE_BLOCK);
        EXPLODABLES.add(Material.YELLOW_FLOWER);
        EXPLODABLES.add(Material.WATER_LILY);
    }

    @Override
    public void onFeatureEnable(boolean manualEnable) {
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        if (event.getEntity().getType() != EntityType.PRIMED_TNT) return;

        event.setCancelled(true);
        event.getLocation().getWorld().playEffect(event.getLocation(), Effect.EXPLOSION_HUGE, null);

        for (Block block : event.blockList()) {
            if (!EXPLODABLES.contains(block.getType())) continue;

            if (block.getType() == Material.CLAY) {
                // Respawn clay walls slower
                SpigotCore.PLUGIN.getBlockRegenerationManager().setBlock(
                        block.getType(),
                        block.getData(),
                        Material.AIR,
                        block.getLocation(),
                        60 * 3 + RandomChance.randomInt(1, 60));
            } else {
                SpigotCore.PLUGIN.getBlockRegenerationManager().setBlock(
                        block.getType(),
                        block.getData(),
                        Material.AIR,
                        block.getLocation(),
                        10 + RandomChance.randomInt(1, 60));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!player.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) return;
        if (block.getType() != Material.TNT) return;

        // If the block underneath is powered, it is okay to set TNT.
        if (block.isBlockIndirectlyPowered()) {
            player.sendMessage(Text.color("&a&lTNT was set, RUN!"));
            event.setCancelled(false);
        } else {

            // If the block is not powered, do not let player set TNT
            player.sendMessage(Text.color("&c&lYou can not set TNT here!"));
            event.setCancelled(true);
        }
    }
}
