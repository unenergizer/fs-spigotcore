package com.forgestorm.spigotcore;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class AsyncFeature extends BukkitRunnable {
    public abstract void enableAsync();
}
