package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.events.PlayerRankChangeEvent;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerScoreboardTeams implements FeatureOptional, Listener {

    private Scoreboard scoreboard;
    private Objective objectivePlayerList;
    private Objective objectivePlayerHP;

    @Override
    public void onEnable(boolean manualEnable) {
        SpigotCore.PLUGIN.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        enableScoreboard();
        setupTeams();

        new BukkitRunnable() {
            @Override
            public void run() {
                setupAllPlayerEntities();
            }
        }.runTaskLater(SpigotCore.PLUGIN, 20);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        GlobalProfileDataLoadEvent.getHandlerList().unregister(this);
        PlayerRankChangeEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerKickEvent.getHandlerList().unregister(this);

        disableScoreboard();
    }

    /**
     * Enables a new scoreboard and adds objectives.
     */
    private void enableScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        // PlayerList Text
        objectivePlayerList = scoreboard.registerNewObjective("FS-PlayerRanks", "dummy");
        objectivePlayerList.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        // Text under player name plate
        objectivePlayerHP = scoreboard.registerNewObjective("UserHP", "dummy");
        objectivePlayerHP.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objectivePlayerHP.setDisplayName(ChatColor.RED + "\u2764");
    }

    /**
     * Fully disables the scoreboard.
     * Removes all players from teams.
     * Removes all teams.
     * Unregisters all scoreboard objectives.
     */
    private void disableScoreboard() {
        // Remove players from teams and the team itself
        for (Team team : scoreboard.getTeams()) {
            for (String entry : team.getEntries()) {
                team.removeEntry(entry);
            }
            team.unregister();
        }

        // Unregister objectives
        objectivePlayerList.unregister();
        objectivePlayerList = null;
        objectivePlayerHP.unregister();
        objectivePlayerHP = null;

        scoreboard = null;
    }

    /**
     * This will setup all of the teams the player can join.
     * This is used to show tags next to their names.
     */
    private void setupTeams() {
        for (PlayerRanks playerRanks : PlayerRanks.values()) {
            Team team = scoreboard.registerNewTeam(playerRanks.getScoreboardTeamName());

            team.setPrefix(playerRanks.getUsernamePrefix());

            if (playerRanks == PlayerRanks.MODERATOR || playerRanks == PlayerRanks.ADMINISTRATOR)
                team.setCanSeeFriendlyInvisibles(true);
        }
    }

    /**
     * Adds all entities to a scoreboard team.
     */
    private void setupAllPlayerEntities() {
        for (LivingEntity livingEntity : Bukkit.getWorlds().get(0).getLivingEntities()) {

            if (!(livingEntity instanceof Player)) continue;
            Player player = (Player) livingEntity;

            if (player.hasMetadata("NPC")) {
                addPlayer(player, PlayerRanks.NPC);
            } else {
                if (!SpigotCore.PLUGIN.getGlobalDataManager().hasGlobalPlayerData(player)) continue;
                PlayerAccount playerAccount = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player).getPlayerAccount();
                addPlayer(player, playerAccount.getRank());
            }

            updatePlayerHP(player);
        }
    }

    /**
     * Updates the HP under a players username.
     * <p>
     * TODO: This should also be called by EventHandlers to set the health.
     *
     * @param player The player HP we want to update.
     */
    private void updatePlayerHP(Player player) {
        objectivePlayerHP.getScore(player.getName()).setScore((int) player.getHealth());
    }

    /**
     * This will add a player to this scoreboard.
     *
     * @param player The player to add.
     * @param group  The User group they are in.
     */
    private void addPlayer(Player player, PlayerRanks group) {
        player.setScoreboard(scoreboard);
        Team tryTeam = scoreboard.getTeam(group.getScoreboardTeamName());

        if (tryTeam == null) return;

        tryTeam.addEntry(player.getName());
    }

    /**
     * This will remove the player from this scoreboard.
     *
     * @param player The player to remove.
     */
    private void removePlayer(Player player) {
        PlayerAccount playerAccount = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player).getPlayerAccount();

        if (playerAccount == null) return;

        Team tryTeam = scoreboard.getTeam(playerAccount.getRank().getScoreboardTeamName());

        tryTeam.removeEntry(player.getName());
    }

    @EventHandler
    public void onProfileLoad(GlobalProfileDataLoadEvent event) {
        addPlayer(event.getPlayer(), event.getGlobalPlayerData().getPlayerAccount().getRank());
        updatePlayerHP(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRankChange(PlayerRankChangeEvent event) {
        removePlayer(event.getPlayer());
        addPlayer(event.getPlayer(), event.getNewPlayerRank());
        updatePlayerHP(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        removePlayer(event.getPlayer());
    }
}
