package com.forgestorm.spigotcore.features.required.world;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.text.Text;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeleportManager extends FeatureRequired {

    private final List<TeleportData> teleportDataList = new ArrayList<>();
    private Location endGateway;
    private BukkitTask animationTick;

    @Override
    protected void initFeatureStart() {
        endGateway = new Location(Bukkit.getWorlds().get(0), 0.5, 83, 500.5);

        animationTick = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(SpigotCore.PLUGIN, 0, 20);
    }

    @Override
    protected void initFeatureClose() {
        animationTick.cancel();
    }

    public void teleportPlayer(Player player, Location location) {
//        player.teleport(endGateway);
//        player.setFallDistance(0F);
//        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 100));
        teleportDataList.add(new TeleportData(player, location));
    }

    private void tick() {
        Iterator<TeleportData> itr = teleportDataList.iterator();

        while (itr.hasNext()) {
            TeleportData teleportData = itr.next();
            int tick = teleportData.getAnimationTicks();
            Player player = teleportData.getPlayer();

            // == Animation Start ============================

            int rand = RandomChance.randomInt(0, 100);
            int color = RandomChance.randomInt(1, 7);

            String text = "";
            for (int i = 0; i <= tick; i++) {
                text = text + "&r &r ";
            }
            if (tick > 1 && tick <= 10) {
                SpigotCore.PLUGIN.getTitleManager().sendTitle(player, Text.color("&" + color + "&l" + "&k/" + text + "&" + color + "&l" + "&k/&r "), 0, 3, 0);
            }

            if (tick > 0 && tick <= 12) {
                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, rand / 100f);
            }

            if (tick == -1) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, rand / 100f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 260, 1));
            }

            if (tick == 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 100));
                player.setGameMode(GameMode.ADVENTURE);
            }

            if (tick == 1) {
                player.setFallDistance(0F);
                player.teleport(endGateway);
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 3 * 20, 100));
            }

            if (tick == 10) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 100));
            }

            if (tick == 11) {
                player.setFallDistance(0F);
                player.teleport(teleportData.destination);
                player.setGameMode(teleportData.originalGameMode);
                player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_HIT, .6f, .5f);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .3f, .1f);
            }

//            // TODO: REMOVE NEXT THREE LINES!! Do not reset ticks, remove from list.
//            if (tick == 18) {
//                tick = -1;
//            }

            // == Animation End ============================

            if (tick == 18) {
                itr.remove();
            } else {
                teleportData.setAnimationTicks(tick + 1);
            }
        }
    }

    @Getter
    @Setter
    private class TeleportData {
        private Player player;
        private Location destination;
        private int animationTicks = -1;
        private GameMode originalGameMode;

        TeleportData(Player player, Location destination) {
            this.player = player;
            this.destination = destination;
            this.originalGameMode = player.getGameMode();
        }
    }
}
