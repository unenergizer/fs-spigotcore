package com.forgestorm.spigotcore.util.world;

import org.bukkit.Chunk;

@SuppressWarnings("WeakerAccess")
public class ChunkUtil {

    /**
     * Compares chunks based on their world coordinates. Chunks can not be
     * safely checked for .equals(). However, you can safely compare
     * chunks by their location.
     *
     * @param chunk1 The first chunk.
     * @param chunk2 The second chunk.
     * @return True if the chunk locations match, false otherwise.
     */
    public static boolean chunkCompare(Chunk chunk1, Chunk chunk2) {
        return chunk1.getX() == chunk2.getX() && chunk1.getZ() == chunk2.getZ();
    }
}
