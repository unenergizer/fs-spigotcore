package com.forgestorm.spigotcore.features.required.database;

import com.forgestorm.spigotcore.features.required.database.global.player.data.GlobalPlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class ProfileDataLoadEvent extends Event {

    private Player player;
    private GlobalPlayerData globalPlayerData;

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
