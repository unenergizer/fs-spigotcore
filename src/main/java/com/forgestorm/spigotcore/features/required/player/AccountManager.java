package com.forgestorm.spigotcore.features.required.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.events.GlobalProfileDataLoadEvent;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerAccount;
import com.forgestorm.spigotcore.util.math.exp.PlayerExperience;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

@SuppressWarnings("WeakerAccess")
public class AccountManager extends FeatureRequired implements Listener {

    private final PlayerExperience playerExperience = new PlayerExperience();

    @Override
    protected void initFeatureStart() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);
    }

    @Override
    protected void initFeatureClose() {
        GlobalProfileDataLoadEvent.getHandlerList().unregister(this);
    }

    private PlayerAccount getAccountData(Player player) {
        return SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player).getPlayerAccount();
    }

    @EventHandler
    public void onGlobalProfileDataLoad(GlobalProfileDataLoadEvent event) {
        Player player = event.getPlayer();
        PlayerAccount playerAccount = getAccountData(player);

        // Very basic account setup
        player.setLevel(playerExperience.getLevel(playerAccount.getExperience()));
        player.setExp(playerExperience.getBarPercent(playerAccount.getExperience()));
    }

    /**
     * Sets a players experience. This will override any previous values.
     *
     * @param argument The amount of experience the player will receive.
     */
    public void setExperience(Player player, long argument) {
        if (argument < 1) argument = 1;

        PlayerAccount playerAccount = getAccountData(player);

        playerAccount.setExperience(argument);
//        // TODO: updateUserInterfaceText();

        // TODO: Update the players scoreboard.
//        plugin.getTarkanScoreboard().updateScoreboard(player);

        // Update player xp bar.
        player.setLevel(playerExperience.getLevel(playerAccount.getExperience()));
        player.setExp(playerExperience.getBarPercent(playerAccount.getExperience()));
    }

    /**
     * Adds experience to the players total current experience.
     *
     * @param player   The player getting experience.
     * @param argument The amount of experience to add.
     */
    public void addExperience(Player player, long argument) {
        int maxAllowedExp = playerExperience.getExperience(100);
        long previousExperience = getAccountData(player).getExperience();
        int previousLevel = playerExperience.getLevel(previousExperience);
        long experience = previousExperience + argument;
        int newLevel = playerExperience.getLevel(experience);

        //Prevent player from getting too much exp.
        if (experience > maxAllowedExp) {
            experience = maxAllowedExp;
        }

        //Player leveled up!
        if (newLevel > previousLevel) {
            //Show level up message.
            SpigotCore.PLUGIN.getTitleManager().sendTitles(player, ChatColor.GREEN + "Leveled UP!", ChatColor.GOLD + "You are now level " + newLevel);

            for (double i = 0; i < 2; i++) {
                Firework fw = player.getWorld().spawn(player.getLocation().subtract(0, -1, 0), Firework.class);
                FireworkMeta fm = fw.getFireworkMeta();
                fm.addEffect(FireworkEffect.builder()
                        .flicker(false)
                        .trail(false)
                        .with(FireworkEffect.Type.STAR)
                        .withColor(Color.YELLOW)
                        .withFade(Color.YELLOW)
                        .build());
                fw.setFireworkMeta(fm);
            }

            //Heal the player
            player.setHealth(20);

            //Send the player a message
            player.sendMessage(ChatColor.GREEN + "You have leveled up!");
            player.sendMessage(ChatColor.GREEN + "You are now level " + ChatColor.GOLD + newLevel + ChatColor.GREEN + ".");
            player.sendMessage(ChatColor.GREEN + "You have been healed!");

//            // TODO: Reset current level time.
//            setLevelTime(0);
//            setLastLevelTime(System.currentTimeMillis() / 1000);

            //Set the level.
            setLevel(player, newLevel);
        }

        setExperience(player, experience);
    }

    /**
     * This will remove experience from the players current total experience count.
     *
     * @param argument The amount of experience to remove.
     */
    public void removeExperience(Player player, long argument) {
        long previousExperience = getAccountData(player).getExperience();
        long experience = previousExperience - argument;

        if (experience < 1) {
            experience = 1;
        }

        setExperience(player, experience);
    }

    /**
     * Sets a players level. This will override any previous values.
     *
     * @param argument The level the player will receive.
     */
    public void setLevel(Player player, int argument) {
        int xp;

        if (argument > 100) {
            xp = playerExperience.getExperience(100);
        } else {
            xp = playerExperience.getExperience(argument);
        }

        setExperience(player, xp);
    }

    /**
     * Adds level(s) to the players current level.
     *
     * @param argument The amount of level(s) to add.
     */
    public void addLevel(Player player, int argument) {
        int currentLevel = player.getLevel();
        int desiredLevel = currentLevel + argument;
        long currentXP = getAccountData(player).getExperience();
        long expToAddLevels = 0;

        if (desiredLevel > 100) {
            desiredLevel = 100;
        }

        for (int level = currentLevel + 1; level <= desiredLevel; level++) {
            expToAddLevels += playerExperience.getExperience(level) - playerExperience.getExperience(level - 1);
        }

        setExperience(player, currentXP + expToAddLevels);
    }

    /**
     * This will remove level(s) from the players current level.
     *
     * @param argument The amount of level(s) to remove.
     */
    public void removeLevel(Player player, int argument) {
        int currentLevel = player.getLevel();
        int desiredLevel = player.getLevel() - argument;
        long currentXP = getAccountData(player).getExperience();
        long expToAddLevels = 0;

        if (desiredLevel < 0) {
            desiredLevel = 1;
        }

        for (int level = currentLevel; level > desiredLevel; level--) {
            expToAddLevels -= playerExperience.getExperience(level) - playerExperience.getExperience(level - 1);
        }

        setExperience(player, currentXP + expToAddLevels);
    }
}
