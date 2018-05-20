package com.forgestorm.spigotcore.world.blockregen;

import com.forgestorm.spigotcore.FeatureRequired;
import com.forgestorm.spigotcore.SpigotCore;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class will replace a block with another block.  After the time expires the block will switch back to its previous form.
 * This is great for block regeneration.  Professions, explosions, and more can be tracked here and regenerated after some time.
 */
@SuppressWarnings("WeakerAccess")
public class BlockRegenerationManager extends FeatureRequired {

    /**
     * Default time for a block to respawn, if a time is not supplied.
     */
    private static final int DEFAULT_REGEN_TIME = 60 * 3;

    /**
     * A list of info that describes what blockregen will be regenerated.
     */
    private final List<RegenerationInfo> regenerationInfoList = new CopyOnWriteArrayList<>();

    private BukkitRunnable syncRunnable;

    @Override
    public void onEnable() {
        syncRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                processRegenerationInfo();
            }
        };
        syncRunnable.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    public void onDisable() {
        syncRunnable.cancel();
        resetAllBlocks();
        regenerationInfoList.clear();
    }

    /**
     * Does necessary countdowns and replaces the regenerated blocks.
     */
    private void processRegenerationInfo() {
        for (RegenerationInfo regenerationInfo : regenerationInfoList) {

            int timeLeft = regenerationInfo.timeLeft;

            if (timeLeft <= 0) {
                // Set the block in the world.
                Block block = regenerationInfo.blockLocation.getBlock();
                block.setType(regenerationInfo.blockMaterial);
                block.setData(regenerationInfo.data);

                regenerationInfoList.remove(regenerationInfo);
            } else {

                regenerationInfo.timeLeft = timeLeft - 1;
            }

        }
    }

    /**
     * Resets all blockregen back to their original state.
     * This is mainly used for server reloads.
     */
    private void resetAllBlocks() {
        for (RegenerationInfo regenerationInfo : regenerationInfoList) {

            // Set the block in the world
            Block block = regenerationInfo.blockLocation.getBlock();
            block.setType(regenerationInfo.blockMaterial);
            block.setData(regenerationInfo.data);

            // Remove this entry
            regenerationInfoList.remove(regenerationInfo);
        }
    }

    /**
     * This will set a temporary block in a broken blockregen location.
     *
     * @param type      The type of block broken.
     * @param data      The original block data.
     * @param tempBlock The temporary block to replace the broken block.
     * @param location  The XYZ location in the world the block was broken.
     */
    public void setBlock(Material type, byte data, Material tempBlock, Location location) {
        setBlock(type, data, tempBlock, (byte) 0, location);
    }

    /**
     * This will set a temporary block in a broken blockregen location.
     *
     * @param type        The type of block broken.
     * @param data        The original block data.
     * @param tempBlock   The temporary block to replace the broken block.
     * @param location    The XYZ location in the world the block was broken.
     * @param respawnTime The amount of time it will take for this block to regenerate.
     */
    public void setBlock(Material type, byte data, Material tempBlock, Location location, int respawnTime) {
        setBlock(type, data, tempBlock, (byte) 0, location, respawnTime);
    }

    /**
     * This will set a temporary block in a broken blockregen location.
     *
     * @param type      The type of block broken.
     * @param data      The original block data.
     * @param tempBlock The temporary block to replace the broken block.
     * @param tempData  The temporary block data.
     * @param location  The XYZ location in the world the block was broken.
     */
    public void setBlock(Material type, byte data, Material tempBlock, byte tempData, Location location) {
        setBlock(type, data, tempBlock, tempData, location, DEFAULT_REGEN_TIME);
    }

    /**
     * This will set a temporary block in a broken blockregen location.
     *
     * @param type        The type of block broken.
     * @param data        The original block data.
     * @param tempBlock   The temporary block to replace the broken block.
     * @param tempData    The temporary block data.
     * @param location    The XYZ location in the world the block was broken.
     * @param respawnTime The amount of time it will take for this block to regenerate.
     */
    public void setBlock(Material type, byte data, Material tempBlock, byte tempData, Location location, int respawnTime) {
        Block block = location.getBlock();

        // Set the temporary block
        block.setType(tempBlock);
        block.setData(tempData);

        // Save the block info for replacement later
        regenerationInfoList.add(new RegenerationInfo(type, data, location, respawnTime));
    }

    @AllArgsConstructor
    private class RegenerationInfo {
        private final Material blockMaterial;
        private final byte data;
        private final Location blockLocation;

        private int timeLeft;
    }
}
