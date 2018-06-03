package com.forgestorm.spigotcore.features.events;

import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.required.database.global.player.data.GlobalPlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class PlayerRankChangeEvent extends Event {

    private Player player;
    private PlayerRanks oldPlayerRank;
    private PlayerRanks newPlayerRank;

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
