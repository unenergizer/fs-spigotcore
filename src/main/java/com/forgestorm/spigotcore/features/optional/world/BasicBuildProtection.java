package com.forgestorm.spigotcore.features.optional.world;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicBuildProtection implements FeatureOptional {

    private DoorResetTimer doorResetTimer;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        doorResetTimer = new DoorResetTimer();
        doorResetTimer.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        doorResetTimer.cancel();
    }

    private boolean preventBuild(Player player) {
        return !player.isOp() || player.getGameMode() != GameMode.CREATIVE;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(preventBuild(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(preventBuild(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() == null) return;
        event.setCancelled(event.getRightClicked().getType() == EntityType.ITEM_FRAME && preventBuild(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        switch (event.getClickedBlock().getType()) {
            case TRAP_DOOR:
            case STONE_BUTTON:
            case WOOD_BUTTON:
            case WOOD_PLATE:
            case STONE_PLATE:
            case GOLD_PLATE:
            case IRON_PLATE:
            case FLOWER_POT:
                event.setCancelled(preventBuild(event.getPlayer()));
                break;
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case IRON_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case WOOD_DOOR:
            case WOODEN_DOOR:
                if (doorResetTimer.contains(event.getClickedBlock()))
                    doorResetTimer.removeDoor(event.getClickedBlock());
                doorResetTimer.addDoor(event.getClickedBlock());
                break;
            default:
                event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPaintingBreak(HangingBreakByEntityEvent event) {
        event.setCancelled(event.getEntity().getType() == EntityType.PAINTING && preventBuild((Player) event.getRemover()));
    }

    class DoorResetTimer extends BukkitRunnable {

        private final Map<Block, Integer> countDowns = new ConcurrentHashMap<>();

        @Override
        public void run() {
            for (Map.Entry<Block, Integer> entry : countDowns.entrySet()) {
                Block block = entry.getKey();
                int count = entry.getValue();

                if (count <= 0) {
                    BlockState blockState = block.getState();
                    if (((Door) blockState.getData()).isTopHalf()) {
                        blockState = block.getRelative(BlockFace.DOWN).getState();
                    }

                    Openable openable = (Openable) blockState.getData();
                    openable.setOpen(!openable.isOpen());
                    blockState.setData((MaterialData) openable);

                    blockState.update();
                    block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, .5f);

                    countDowns.remove(entry.getKey());
                } else {
                    entry.setValue(count - 1);
                    countDowns.replace(entry.getKey(), entry.getValue());
                }
            }
        }

        private void addDoor(Block block) {
            countDowns.put(block, 5);
        }

        private void removeDoor(Block block) {
            countDowns.remove(block);
        }

        private boolean contains(Block block) {
            return countDowns.containsKey(block);
        }
    }
}
