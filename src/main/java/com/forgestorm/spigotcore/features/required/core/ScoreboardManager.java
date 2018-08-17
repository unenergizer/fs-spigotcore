package com.forgestorm.spigotcore.features.required.core;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.events.PlayerRankChangeEvent;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerAccount;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager extends FeatureRequired {

    private Scoreboard scoreboard;
    private Objective objectivePlayerList;
    private Objective objectivePlayerHP;

    @Override
    public void initFeatureStart() {
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
    public void initFeatureClose() {
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
     * This will add a team prefix/suffix combo to a LivingEntity. Currently mainly used
     * to put "Kit" and "Team" next to minigame framework mobs.
     *
     * @param livingEntity The living entity to add to the team.
     * @param teamName     The team the living entity should join.
     */
    public void addEntityToTeam(LivingEntity livingEntity, String teamName) {
        getTeam(teamName).addEntry(livingEntity.getUniqueId().toString());
    }

    /**
     * This will remove a team prefix/suffix combo from a LivingEntity. Currently mainly used
     * to remove "Kit" and "Team" from the minigame framework mobs.
     *
     * @param livingEntity The living entity to remove from the team.
     * @param teamName     The team the living entity should quit.
     */
    public void removeEntityFromTeam(LivingEntity livingEntity, String teamName) {
        getTeam(teamName).removeEntry(livingEntity.getUniqueId().toString());
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
    public void addPlayer(Player player, PlayerRanks group) {
        player.setScoreboard(scoreboard);
        Team tryTeam = scoreboard.getTeam(group.getScoreboardTeamName());

        if (tryTeam == null) return;

        tryTeam.addEntry(player.getName());
    }

    /**
     * This will add a player to the scoreboard.
     *
     * @param player   The player we want to add.
     * @param teamName The team we want the player to join.
     * @param prefix   The text to prefix the players name.
     * @param suffix   The text to suffix the players name.
     */
    public void addPlayer(Player player, String teamName, String prefix, String suffix) {
        player.setScoreboard(scoreboard);

        // Lets add the team to the scoreboard.
        addTeam(teamName, prefix, suffix);

        // Add the player to the team.
        getTeam(teamName).addEntry(player.getName());
    }

    /**
     * This will remove the player from this scoreboard.
     *
     * @param player The player to remove.
     */
    public void removePlayer(Player player) {
        PlayerAccount playerAccount = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player).getPlayerAccount();

        if (playerAccount == null) return;

        Team tryTeam = scoreboard.getTeam(playerAccount.getRank().getScoreboardTeamName());

        tryTeam.removeEntry(player.getName());
    }

    /**
     * This will create a new scoreboard team.
     *
     * @param teamName The name of the team. This is how the scoreboard knows
     *                 one team from another. This name is never displayed to
     *                 the player. So it may be best to not use spaces.
     * @param prefix   The prefix of the team name.
     * @param suffix   The suffix of the team name.
     */
    private void addTeam(String teamName, String prefix, String suffix) {
        String trimmedTeamName = Text.trimString(teamName, 16);

        // If the team already exists, skip adding it.
        if (teamExist(trimmedTeamName)) return;

        // Register the new team
        Team team = scoreboard.registerNewTeam(trimmedTeamName);

        // Set the prefix.
        if (prefix != null && !prefix.isEmpty()) {
            team.setPrefix(prefix);
        }

        // Set ths suffix.
        if (suffix != null && !suffix.isEmpty()) {
            team.setSuffix(suffix);
        }
    }

    /**
     * This will remove a team from the scoreboard.
     *
     * @param teamName The team to remove.
     */
    private void removeTeam(String teamName) {
        getTeam(teamName).unregister();
    }

    /**
     * Gets a team using the trimmed name.
     *
     * @param teamName The original team name.
     * @return The team with the trimmed team name.
     */
    private Team getTeam(String teamName) {
        return scoreboard.getTeam(Text.trimString(teamName, 16));
    }

    /**
     * This will check to see if a team exists.
     *
     * @param teamName The team we want to check for.
     * @return True if the team exists. False otherwise.
     */
    private boolean teamExist(String teamName) {
        boolean doesExist = false;
        for (Team team : scoreboard.getTeams()) if (team.getName().equals(teamName)) doesExist = true;
        return doesExist;
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
}
