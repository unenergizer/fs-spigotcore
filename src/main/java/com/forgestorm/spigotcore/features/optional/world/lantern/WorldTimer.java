package com.forgestorm.spigotcore.features.optional.world.lantern;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.util.text.Console;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
class WorldTimer extends BukkitRunnable {

    private int day = 0;
    private long time = 0;
    private long lastTime = Long.MAX_VALUE;

    public void onEnable() {
        this.runTaskTimer(SpigotCore.PLUGIN, 0, 1);
    }

    public void onDisable() {
        this.cancel();
    }

    @Override
    public void run() {
        time = Bukkit.getWorlds().get(0).getTime();

        //If world is paused, prevent sending multiple events.
        if (time != lastTime) return;

        // DAWN
        if (time == 22916) triggerEvent(TimeOfDay.DAWN);

        // AFTERNOON
        if (time == 0) {
            day++;
            Console.sendMessage("Day: " + day);
            triggerEvent(TimeOfDay.AFTERNOON);
        }

        // DUSK
        if (time == 11616) triggerEvent(TimeOfDay.DUSK);

        // MIDNIGHT
        if (time == 18000) triggerEvent(TimeOfDay.MIDNIGHT);

//            Animate blockRegen
//            plugin.getWorldAnimator().shouldAnimate(scheduler);
        lastTime = time;
    }

    /**
     * This will trigger a new TimeOfTHeDayEvent.
     *
     * @param timesOfTheDay The current scheduler of day
     */
    private void triggerEvent(TimeOfDay timesOfTheDay) {
        TimeOfDayEvent exampleEvent = new TimeOfDayEvent(timesOfTheDay);
        Bukkit.getPluginManager().callEvent(exampleEvent);
    }
}
