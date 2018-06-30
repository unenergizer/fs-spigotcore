package com.forgestorm.spigotcore.features.optional.world.lantern;

import com.forgestorm.spigotcore.SpigotCore;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
class WorldTime extends BukkitRunnable {

    private int day = 0;
    private long time = 0;
    private long lastTime = -1;

    void onEnable() {
        this.runTaskTimerAsynchronously(SpigotCore.PLUGIN, 0, 1);
    }

    void onDisable() {
        this.cancel();
    }

    @Override
    public void run() {
        time = Bukkit.getWorlds().get(0).getTime();

        //If world is paused, prevent sending multiple events.
        if (time == lastTime) return;

        // DAWN
        if (time == 22916) triggerEvent(TimeOfDay.DAWN);

        // AFTERNOON
        if (time == 0) {
            day++;
            triggerEvent(TimeOfDay.AFTERNOON);
        }

        // DUSK
        if (time == 11616) triggerEvent(TimeOfDay.DUSK);

        // MIDNIGHT
        if (time == 18000) triggerEvent(TimeOfDay.MIDNIGHT);

        lastTime = time;
    }

    /**
     * This will trigger a new TimeOfTHeDayEvent.
     *
     * @param timesOfTheDay The current scheduler of day
     */
    private void triggerEvent(TimeOfDay timesOfTheDay) {
        Bukkit.getPluginManager().callEvent(new TimeOfDayChangeEvent(timesOfTheDay));
    }
}
