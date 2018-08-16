package com.forgestorm.spigotcore.util.scoreboard;

public class ScoreboardManager {
}
//public class ScoreboardManager extends FeatureRequired {
//
//    private Scoreboard scoreboard;
//
//    @Override
//    protected void initFeatureStart() {
//        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
//
//        // This will enable all the default scoreboard teams.
//        for (PlayerRanks playerRanks : PlayerRanks.values()) {
//            // Register the new Team.
//            Team team = scoreboard.registerNewTeam(playerRanks.getScoreboardTeamName());
//            team.setCanSeeFriendlyInvisibles(true);
//
//            // If the prefix does not exist, skip the part below.
//            if (playerRanks.getUsernamePrefix().isEmpty()) continue;
//
//            // Add the prefix.
//            team.setPrefix(playerRanks.getUsernamePrefix());
//        }
//    }
//
//    @Override
//    protected void initFeatureClose() {
//        // TODO: Remove all players
//
//        // Remove all ranks/teams
//        for (Team team : scoreboard.getTeams()) {
//            team.unregister();
//        }
//    }
//
//    /**
//     * This will create a new scoreboard team.
//     *
//     * @param teamName The name of the team. This is how the scoreboard knows
//     *                 one team from another. This name is never displayed to
//     *                 the player. So it may be best to not use spaces.
//     * @param prefix   The prefix of the team name.
//     * @param suffix   The suffix of the team name.
//     */
//    private void addTeam(String teamName, String prefix, String suffix) {
//        String trimmedTeamName = Text.trimString(teamName, 16);
//
//        // If the team already exists, skip adding it.
//        if (teamExist(trimmedTeamName)) return;
//
//        // Register the new team
//        Team team = scoreboard.registerNewTeam(trimmedTeamName);
//
//        // Set the prefix.
//        if (prefix != null && !prefix.isEmpty()) {
//            team.setPrefix(prefix);
//        }
//
//        // Set ths suffix.
//        if (suffix != null && !suffix.isEmpty()) {
//            team.setSuffix(suffix);
//        }
//    }
//
//    /**
//     * This will remove a team from the scoreboard.
//     *
//     * @param teamName The team to remove.
//     */
//    private void removeTeam(String teamName) {
//        getTeam(teamName).unregister();
//    }
//
//    /**
//     * This will add a team prefix/suffix combo to a LivingEntity. Currently mainly used
//     * to put "Kit" and "Team" next to minigame framework mobs.
//     *
//     * @param livingEntity The living entity to add to the team.
//     * @param teamName     The team the living entity should join.
//     */
//    public void addEntityToTeam(LivingEntity livingEntity, String teamName) {
//        getTeam(teamName).addEntry(livingEntity.getUniqueId().toString());
//    }
//
//    /**
//     * This will remove a team prefix/suffix combo from a LivingEntity. Currently mainly used
//     * to remove "Kit" and "Team" from the minigame framework mobs.
//     *
//     * @param livingEntity The living entity to remove from the team.
//     * @param teamName     The team the living entity should quit.
//     */
//    public void removeEntityFromTeam(LivingEntity livingEntity, String teamName) {
//        getTeam(teamName).removeEntry(livingEntity.getUniqueId().toString());
//    }
//
//    /**
//     * This will add an objective to the players scoreboard. This can
//     * be used to show the players level, hit points, and other things.
//     *
//     * @param objectiveName The name of the objective.
//     * @param criteria      The objective criteria. Typically we call this "dummy."
//     * @param displaySlot   The display slot to place the objective.
//     * @param displayName   The name of the criteria.
//     */
//    public void addObjective(String objectiveName, String criteria, DisplaySlot displaySlot, String displayName) {
//        Objective objective = scoreboard.registerNewObjective(objectiveName, criteria);
//        objective.setDisplaySlot(displaySlot);
//
//        if (displayName == null) return;
//        objective.setDisplayName(Text.color(displayName));
//    }
//
//    /**
//     * This will remove an objective from the scoreboard manager.
//     *
//     * @param objectiveName The name of the objective we want to remove.
//     */
//    public void removeObjective(String objectiveName) {
//        scoreboard.getObjective(objectiveName).unregister();
//    }
//
//    /**
//     * This will set a score for the players objective.
//     *
//     * @param name   The name of the objective to modify.
//     * @param player The player to modify the score for.
//     * @param value  The new value of the score to be set.
//     */
//    public void setObjectiveScore(String name, Player player, int value) {
//        scoreboard.getObjective(name).getScore(player.getName()).setScore(value);
//    }
//
//    /**
//     * This will add a player to the scoreboard with PlayerRanks properties.
//     *
//     * @param player      The player we want to add to the scoreboard.
//     * @param playerRanks The usergroup we want to assign the player to.
//     */
//    public void addPlayer(Player player, PlayerRanks playerRanks) {
//        addPlayer(player, playerRanks.getScoreboardTeamName(), playerRanks.getUsernamePrefix(), null);
//    }
//
//    /**
//     * This will add a player to the scoreboard.
//     *
//     * @param player   The player we want to add.
//     * @param teamName The team we want the player to join.
//     * @param prefix   The text to prefix the players name.
//     * @param suffix   The text to suffix the players name.
//     */
//    public void addPlayer(Player player, String teamName, String prefix, String suffix) {
//        // Give the player the current scoreboard.
//        player.setScoreboard(scoreboard);
//
//        // Lets add the team to the scoreboard.
//        addTeam(teamName, prefix, suffix);
//
//        // Add the player to the team.
//        getTeam(teamName).addEntry(player.getName());
//    }
//
//    /**
//     * This will remove a player from the current team. This
//     * will also unregister any unused teams from the score-
//     * board.
//     *
//     * @param player The player to remove from the team.
//     */
//    public void removePlayer(Player player) {
//        String entry = player.getName();
//        Team currentTeam = null;
//
//        // Find player team.
//        for (Team team : scoreboard.getTeams()) if (team.hasEntry(entry)) currentTeam = team;
//
//        // If no team is found, do not continue.
//        if (currentTeam == null) return;
//
//        // Remove player from the team.
//        currentTeam.removeEntry(entry);
//
//        ////////////////////////////////////////////////
//        // Begin checking to see if we can remove this
//        // team from the scoreboard.
//        ////////////////////////////////////////////////
//
//        // Check if the team has players.
//        if (currentTeam.getSize() >= 1) return;
//
//        // Check if the team is a default preset.
//        for (PlayerRanks playerRanks : PlayerRanks.values()) {
//            if (playerRanks.getScoreboardTeamName().equals(currentTeam.getName())) return;
//        }
//
//        // If the team has no players and is not a default
//        // preset, then lets remove the team from the
//        // scoreboard.
//        removeTeam(currentTeam.getName());
//    }
//
//    /**
//     * Gets a team using the trimmed name.
//     *
//     * @param teamName The original team name.
//     * @return The team with the trimmed team name.
//     */
//    private Team getTeam(String teamName) {
//        return scoreboard.getTeam(Text.trimString(teamName, 16));
//    }
//
//    /**
//     * This will check to see if a team exists.
//     *
//     * @param teamName The team we want to check for.
//     * @return True if the team exists. False otherwise.
//     */
//    private boolean teamExist(String teamName) {
//        boolean doesExist = false;
//        for (Team team : scoreboard.getTeams()) if (team.getName().equals(teamName)) doesExist = true;
//        return doesExist;
//    }
//}
