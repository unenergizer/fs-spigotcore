package com.forgestorm.spigotcore.features.optional.citizen;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player clicks a Citizen NPC.
 */
@SuppressWarnings("WeakerAccess")
public class CitizenToggleEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Player player;
    @Getter
    private final Player citizen;
    @Getter
    private final CitizenType citizenType;

    public CitizenToggleEvent(Player player, Player citizen, CitizenType citizenType) {
        this.player = player;
        this.citizen = citizen;
        this.citizenType = citizenType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
