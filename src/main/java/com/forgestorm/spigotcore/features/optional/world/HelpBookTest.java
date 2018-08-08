package com.forgestorm.spigotcore.features.optional.world;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

public class HelpBookTest implements FeatureOptional, Listener {
    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        PlayerInteractEvent.getHandlerList().unregister(this);
        InventoryOpenEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onAnvilInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.ENCHANTMENT_TABLE) return;
        Location anvilLocation = event.getClickedBlock().getLocation();
        Bukkit.getServer().getWorlds().get(0).playEffect(anvilLocation, Effect.MOBSPAWNER_FLAMES, 1);
        Bukkit.getServer().getWorlds().get(0).playEffect(anvilLocation, Effect.MOBSPAWNER_FLAMES, 1);
        Bukkit.getServer().getWorlds().get(0).playEffect(anvilLocation, Effect.MOBSPAWNER_FLAMES, 1);
        Bukkit.getServer().getWorlds().get(0).playEffect(anvilLocation, Effect.MOBSPAWNER_FLAMES, 1);
    }

    @EventHandler
    public void onAnvilOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENCHANTING) event.setCancelled(true);
    }

    @EventHandler
    public void onAnvilBreak(BlockBreakEvent event) {
        if (!event.getBlock().getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) return;
        if (event.getBlock().getType() == Material.ENCHANTMENT_TABLE) event.setCancelled(true);
    }
}
