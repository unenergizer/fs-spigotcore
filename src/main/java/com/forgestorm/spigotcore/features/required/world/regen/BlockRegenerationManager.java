package com.forgestorm.spigotcore.features.required.world.regen;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Regenerates broken blocks by {@link com.forgestorm.spigotcore.features.optional.FeatureOptional} features
 * if required.
 * <p>
 * RULES:
 * <ul>
 *     <li>Blocks must be replaced when the server shuts down. No exceptions!</li>
 * </ul>
 */
@SuppressWarnings("WeakerAccess")
public class BlockRegenerationManager implements FeatureRequired {

    /**
     * Default time for a block to respawn, if a time is not supplied.
     */
    private static final int DEFAULT_REGEN_TIME = 60 * 3;

    /**
     * A list of info that describes what blockRegen will be regenerated.
     */
    private final List<RegenerationInfo> regenerationInfoList = new CopyOnWriteArrayList<>();

    private BukkitRunnable syncRunnable;

    @Override
    public void onServerStartup() {
        syncRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                processRegenerationInfo();
            }
        };
        syncRunnable.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    public void onServerShutdown() {
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
     * Resets all blockRegen back to their original state.
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
     * This will set a temporary block in a broken blockRegen location.
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
     * This will set a temporary block in a broken blockRegen location.
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
     * This will set a temporary block in a broken blockRegen location.
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
     * This will set a temporary block in a broken blockRegen location.
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

    /**
     * Simple data class that contains information on a block that must be regenerated.
     */
    @AllArgsConstructor
    private class RegenerationInfo {

        /**
         * The original broken blocks material.
         */
        private final Material blockMaterial;

        /**
         * The material data of the original block.
         */
        private final byte data;

        /**
         * The location the block was broken.
         */
        private final Location blockLocation;

        /**
         * The time left until the block is reset.
         */
        private int timeLeft;
    }
}
