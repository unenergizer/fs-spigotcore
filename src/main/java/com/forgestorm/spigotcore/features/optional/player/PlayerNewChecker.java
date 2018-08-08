package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.events.PlayerRankChangeEvent;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class PlayerNewChecker implements FeatureOptional, Listener {

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        GlobalProfileDataLoadEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onGlobalProfileDataLoad(GlobalProfileDataLoadEvent event) {
        PlayerAccount playerAccount = event.getGlobalPlayerData().getPlayerAccount();

        if (playerAccount.getRank() != PlayerRanks.NEW_PLAYER) return;

        long firstJoinDate = playerAccount.getFirstJoinDate().getTime();
        long waitTime = (TimeUnit.SECONDS.toMillis(30));
        long timeNow = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))).getTime();

        if (firstJoinDate + waitTime < timeNow) {
            Bukkit.getPluginManager().callEvent(new PlayerRankChangeEvent(event.getPlayer(), PlayerRanks.NEW_PLAYER, PlayerRanks.FREE_PLAYER));
            playerAccount.setRank(PlayerRanks.FREE_PLAYER);
            event.getPlayer().sendMessage(ChatColor.GREEN + "Yay! You are no longer considered a new player! :)");
        }
    }
}
