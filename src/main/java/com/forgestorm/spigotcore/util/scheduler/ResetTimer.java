package com.forgestorm.spigotcore.util.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Small Timer util. Great for cool downs or countdowns.
 */
@SuppressWarnings("unused")
public class ResetTimer extends BukkitRunnable {

    private final Map<Player, Integer> countDowns = new ConcurrentHashMap<>();

    @Override
    public void run() {
        for (Player player : countDowns.keySet()) {

            int count = countDowns.get(player);

            if (count <= 0) {
                countDowns.remove(player);
            } else {
                countDowns.replace(player, --count);
            }
        }
    }

    /**
     * Adds a player to this countdown timer.
     *
     * @param player The player to add
     * @param time   The scheduler to wait
     */
    public void addPlayer(Player player, int time) {
        countDowns.put(player, time);
    }

    /**
     * Removes a player from this countdown.
     *
     * @param player The player to remove.
     */
    public void removePlayer(Player player) {
        countDowns.remove(player);
    }

    /**
     * Checks to see if the player is in this countdown.
     *
     * @param player The player to check
     * @return True if the player is in the countdown, false otherwise.
     */
    public boolean containsPlayer(Player player) {
        return countDowns.containsKey(player);
    }
}
