package com.forgestorm.spigotcore.features.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateScoreboardEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;

    public UpdateScoreboardEvent(Player player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }
}