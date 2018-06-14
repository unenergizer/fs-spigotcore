package com.forgestorm.spigotcore.features.events;

import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class WorldObjectDespawnEvent extends Event implements Cancellable {

    private final BaseWorldObject baseWorldObject;
    private boolean cancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
