package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.database.ProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerAccount;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class PlayerNewChecker implements FeatureOptional, Listener {

    @Override
    public void onEnable(boolean manualEnable) {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        ProfileDataLoadEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onGlobalProfileDataLoad(ProfileDataLoadEvent event) {
        PlayerAccount playerAccount = event.getGlobalPlayerData().getPlayerAccount();

        if (playerAccount.getRank() != PlayerRanks.NEW_PLAYER) return;

        long firstJoinDate = playerAccount.getFirstJoinDate().getTime();
        long waitTime = (TimeUnit.MINUTES.toMillis(1));
        long timeNow = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))).getTime();

        if (firstJoinDate + waitTime < timeNow) {
            playerAccount.setRank(PlayerRanks.FREE_PLAYER);
            event.getPlayer().sendMessage(ChatColor.GREEN + "Yay! You are no longer considered a new player! :)");
        }
    }
}
