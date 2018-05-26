package com.forgestorm.spigotcore.world.blockregen;

import com.forgestorm.spigotcore.feature.FeatureOptional;
import com.forgestorm.spigotcore.SpigotCore;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Hidden paths are a Zelda like feature. Players can use TNT to
 * blow up a particular wall revealing a hidden path. When using
 * the block regeneration manager, the blockregen will respawn after
 * some time.
 */
public class HiddenPaths implements FeatureOptional, Listener {

    private final List<Material> breakables = new ArrayList<>();

    @Override
    public void onEnable() {
        // Add items that TNT can destroy.
        breakables.add(Material.CLAY);
        breakables.add(Material.LONG_GRASS);
        breakables.add(Material.DEAD_BUSH);
        breakables.add(Material.DOUBLE_PLANT);
        breakables.add(Material.RED_MUSHROOM);
        breakables.add(Material.RED_ROSE);
        breakables.add(Material.SAPLING);
        breakables.add(Material.SUGAR_CANE);
        breakables.add(Material.SUGAR_CANE_BLOCK);
        breakables.add(Material.YELLOW_FLOWER);
        breakables.add(Material.WATER_LILY);

        // Register Events
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onDisable() {
        // Unregister Events
        EntityExplodeEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        if (event.getEntity().getType() != EntityType.PRIMED_TNT) return;

        event.setCancelled(true);
        event.getLocation().getWorld().playEffect(event.getLocation(), Effect.EXPLOSION_HUGE, null);

        for (Block block : event.blockList()) {
            if (!breakables.contains(block.getType())) continue;

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

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!player.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) return;
        if (block.getType() != Material.TNT) return;

        // If the block underneath is powered, it is okay to set TNT.
        if (block.isBlockIndirectlyPowered()) {
            player.sendMessage(Text.color("&a&lTNT was set, RUN!"));
        } else {

            // If the block is not powered, do not let player set TNT
            player.sendMessage(Text.color("&c&lYou can not set TNT here!"));
            event.setCancelled(true);
        }
    }
}
