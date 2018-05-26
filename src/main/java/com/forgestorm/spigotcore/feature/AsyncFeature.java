package com.forgestorm.spigotcore.feature;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class AsyncFeature extends BukkitRunnable {
    public abstract void enableAsync();
}
