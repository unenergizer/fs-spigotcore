package com.forgestorm.spigotcore.world.lantern;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

class TimeOfDayEvent extends Event {

    @Getter
    private final TimeOfDay timeOfDay;
    private static final HandlerList HANDLERS = new HandlerList();

    public TimeOfDayEvent(TimeOfDay timeOfDay) {
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
