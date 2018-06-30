package com.forgestorm.spigotcore.features.optional.world.lantern;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimeOfDayChangeEvent extends Event {

    @Getter
    private final TimeOfDay timeOfDay;
    private static final HandlerList HANDLERS = new HandlerList();

    TimeOfDayChangeEvent(TimeOfDay timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
