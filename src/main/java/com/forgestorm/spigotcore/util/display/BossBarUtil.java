package com.forgestorm.spigotcore.util.display;

import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("unused")
public class BossBarUtil {

    private final String message;
    private BossBar bar;

    public BossBarUtil(String message) {
        this.message = message;
        setupBossBar();
    }

    /**
     * This will setup a new bossbar.
     */
    private void setupBossBar() {
        bar = Bukkit.createBossBar(Text.color(message), BarColor.PURPLE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        bar.setVisible(true);
    }

    /**
     * Sends a player entity a boss bar message.
     *
     * @param player The player who will receive a boss bar message.
     */
    public void showBossBar(Player player) {
        bar.addPlayer(player);
    }

    /**
     * Removes a boss bar from a player.
     *
     * @param player Will remove a boss bar from this player.
     */
    public void removeBossBar(Player player) {
        bar.removePlayer(player);
    }

    /**
     * Removes a boss bar from all players.
     */
    public void removeAllBossBar() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            bar.removePlayer(players);
        }
    }

    /**
     * Sets the progress of the current bar. From 0 - 1;
     *
     * @param progress The progress of the current bar.
     */
    public void setBossBarProgress(double progress) {
        bar.setProgress(progress);
    }

    /**
     * Sets a new title for the bar.
     *
     * @param title The title of the bar.
     */
    public void setBossBarTitle(String title) {
        bar.setTitle(Text.color(title));
    }

    /**
     * Gets a list of players who are viewing this bossbar.
     *
     * @return Returns a list of players viewing this bossbar.
     */
    public List<Player> getBossBarViewers() {
        return bar.getPlayers();
    }
}
