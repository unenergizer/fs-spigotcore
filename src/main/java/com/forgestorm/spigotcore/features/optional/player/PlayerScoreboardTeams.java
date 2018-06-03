package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.events.PlayerRankChangeEvent;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.events.ProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerAccount;
import com.forgestorm.spigotcore.util.text.Console;
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

        new BukkitRunnable() {
            @Override
            public void run() {
                enableScoreboard();
                setupTeams();
            }
        }.runTaskLater(SpigotCore.PLUGIN, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                setupAllPlayerEntities();
            }
        }.runTaskLater(SpigotCore.PLUGIN, 20 * 5);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        ProfileDataLoadEvent.getHandlerList().unregister(this);
        PlayerRankChangeEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerKickEvent.getHandlerList().unregister(this);

        disableScoreboard();
    }

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
        objectivePlayerHP.unregister();

        scoreboard = null;
    }

    /**
     * This will setup all of the teams the player can join. This is used to show
     * tags next to their names.
     */
    private void setupTeams() {
        for (PlayerRanks playerRanks : PlayerRanks.values()) {
            Team team = scoreboard.registerNewTeam(playerRanks.getScoreboardTeamName());

            team.setPrefix(playerRanks.getUsernamePrefix());

//            Console.sendMessage("NewTeam: " + team.getName());
//            Console.sendMessage("TeamPrefix: " + team.getPrefix());

            if (playerRanks == PlayerRanks.MODERATOR || playerRanks == PlayerRanks.ADMINISTRATOR)
                team.setCanSeeFriendlyInvisibles(true);
        }
    }

    private void setupAllPlayerEntities() {
        for (LivingEntity livingEntity : Bukkit.getWorlds().get(0).getLivingEntities()) {

            if (!(livingEntity instanceof Player)) continue;
            Player player = (Player) livingEntity;

            if (player.hasMetadata("NPC")) {
                addPlayer(player, PlayerRanks.NPC);
            } else {
                PlayerAccount playerAccount = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player).getPlayerAccount();
                if (playerAccount != null) addPlayer(player, playerAccount.getRank());
            }

//            Console.sendMessage("Player: " + player.getDisplayName() + ", Rank: " + scoreboard.getEntryTeam(player.getName()).getPrefix());

            updatePlayerHP(player);
        }
    }

    /**
     * Updates the HP under a players username.
     *
     * @param player The player HP we want to update.
     */
    private void updatePlayerHP(Player player) {
        updatePlayerHP(player, (int) player.getHealth());
    }

    /**
     * Updates the HP under a players username.
     *
     * @param player The player HP we want to update.
     * @param health The health to set in the scoreboard.
     */
    public void updatePlayerHP(Player player, int health) {
        objectivePlayerHP.getScore(player.getName()).setScore(health);
    }


    /**
     * This will add a player to this scoreboard.
     *
     * @param player The player to add.
     * @param group  The User group they are in.
     * @return True if they were added, false otherwise.
     */
    private boolean addPlayer(Player player, PlayerRanks group) {
        player.setScoreboard(scoreboard);
        Team tryTeam = scoreboard.getTeam(group.getScoreboardTeamName());

        if (tryTeam == null) return false;

        tryTeam.addEntry(player.getName());
        return true;
    }

    /**
     * This will remove the player from this scoreboard.
     *
     * @param player The player to remove.
     * @return True if removed, false otherwise.
     */
    public boolean removePlayer(Player player) {
        PlayerAccount playerAccount = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player).getPlayerAccount();

        if (playerAccount == null) return false;

        Team tryTeam = scoreboard.getTeam(playerAccount.getRank().getScoreboardTeamName());

        if (tryTeam == null) return false;

        tryTeam.removeEntry(player.getName());
        return true;
    }

    @EventHandler
    public void onProfileLoad(ProfileDataLoadEvent event) {
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
