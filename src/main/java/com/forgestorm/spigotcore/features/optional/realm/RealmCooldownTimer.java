package com.forgestorm.spigotcore.features.optional.realm;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class RealmCooldownTimer extends BukkitRunnable {

    private static final int MAX_COOLDOWN = 2 * 60;
    private final Map<UUID, Integer> realmCooldownMap = new ConcurrentHashMap<>();

    @Override
    public void run() {
        for (Map.Entry<UUID, Integer> entry : realmCooldownMap.entrySet()) {
            int timeLeft = entry.getValue() - 1;
            entry.setValue(timeLeft);
            if (timeLeft <= 0) realmCooldownMap.remove(entry.getKey());
        }
    }

    /**
     * Adds the player to the realm cooldown timer.
     *
     * @param player The player to add.
     */
    void addRealmCooldown(Player player) {
        realmCooldownMap.put(player.getUniqueId(), MAX_COOLDOWN);
    }

    /**
     * Checks to see if the player is currently on a cooldown.
     *
     * @param player The player to check.
     * @return True if the player is on a cooldown.
     */
    boolean isOnCooldown(Player player) {
        return realmCooldownMap.containsKey(player.getUniqueId());
    }

    /**
     * Gets the scheduler left until the player is no longer on a cooldown.
     *
     * @param player The player to get the scheduler left for.
     * @return The scheduler left.
     */
    int getTimeLeft(Player player) {
        return realmCooldownMap.get(player.getUniqueId());
    }
}