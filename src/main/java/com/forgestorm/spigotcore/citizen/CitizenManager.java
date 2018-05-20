package com.forgestorm.spigotcore.citizen;

import com.forgestorm.spigotcore.FeatureOptional;
import com.forgestorm.spigotcore.LoadsConfig;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.util.display.Hologram;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.text.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CitizenManager implements FeatureOptional, LoadsConfig, Listener {

    private final Map<String, BasicCitizen> basicCitizenMap = new HashMap<>();
    private final ResetTimer resetTimer = new ResetTimer();

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        resetTimer.runTaskTimerAsynchronously(SpigotCore.PLUGIN, 0, 1);

        // Do additional citizen setup after server start
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpigotCore.PLUGIN, this::setupCitizen, SpigotCore.FEATURE_TASK_START_DELAY);
    }

    @Override
    public void onDisable() {
        PlayerInteractEntityEvent.getHandlerList().unregister(this);

        // Clean up all citizens!
        for (BasicCitizen basicCitizen : basicCitizenMap.values()) {
            basicCitizen.removeHologram();
            basicCitizen.chatMessages.clear();
        }

        basicCitizenMap.clear();
        resetTimer.cancel();
    }

    @Override
    public void loadConfiguration() {
        File file = new File(FilePaths.CITIZENS.toString());
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        Set<String> keys = config.getConfigurationSection("Citizens").getKeys(false);

        for (String citizenName : keys) {
            String section = "Citizens." + citizenName + ".";

            BasicCitizen basicCitizen = new BasicCitizen(
                    CitizenType.valueOf(config.getString(section + "type")),
                    config.getStringList(section + "chat"));

            basicCitizenMap.put(citizenName, basicCitizen);
        }
    }

    /**
     * This will apply HP to a NPC.
     * The HP is displayed under the NPCs name.
     */
    private void setupCitizen() {

        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {

            if (!(entity instanceof Player)) return;
            if (!entity.hasMetadata("NPC")) return;

            Player npc = (Player) entity;
            BasicCitizen basicCitizen = basicCitizenMap.get(npc.getDisplayName());

            basicCitizen.addTitleHologram(npc);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (resetTimer.containsPlayer(player)) return;
        if (!(event.getRightClicked() instanceof Player)) return;
        if (!event.getRightClicked().hasMetadata("NPC")) return;

        resetTimer.addPlayer(player);

        Player npc = (Player) event.getRightClicked();
        String npcName = npc.getDisplayName();
        Location npcLocation = npc.getLocation();
        BasicCitizen basicCitizen = basicCitizenMap.get(npcName);

        // Toggle citizen interact event
        CitizenToggleEvent citizenToggleEvent = new CitizenToggleEvent(player, npc, basicCitizen.citizenType);
        Bukkit.getPluginManager().callEvent(citizenToggleEvent);

        // Send a random saying to the player
        String npcMessage = basicCitizen.getRandomChatMessage();
        if (npcMessage != null) player.sendMessage(Text.color("&7[&9NPC&7] " + npcName + "&8: &r" + npcMessage));


        // Play citizen sound
        int rand = RandomChance.randomInt(1, 100);
        if (rand < 25) {
            player.playSound(npcLocation, Sound.ENTITY_VILLAGER_TRADING, .8f, .8f);
        } else if (rand < 50) {
            player.playSound(npcLocation, Sound.ENTITY_VILLAGER_AMBIENT, .8f, .8f);
        } else if (rand < 75) {
            player.playSound(npcLocation, Sound.ENTITY_VILLAGER_NO, .8f, .8f);
        } else {
            player.playSound(npcLocation, Sound.ENTITY_VILLAGER_YES, .8f, .8f);
        }

        // Show particle effect
        for (int i = 0; i <= 5; i++) {
            Bukkit.getWorlds().get(0).spigot().playEffect(npcLocation.add(0, 2, 0), Effect.HAPPY_VILLAGER);
        }
    }

    @Getter
    @AllArgsConstructor
    private class BasicCitizen {
        private final CitizenType citizenType;
        private final List<String> chatMessages;

        private final Hologram hologram = new Hologram();

        String getRandomChatMessage() {
            if (chatMessages.isEmpty()) return null;
            int rand = RandomChance.randomInt(0, chatMessages.size() - 1);
            return chatMessages.get(rand);
        }

        void addTitleHologram(Player npc) {
            if (citizenType == CitizenType.NONE) return;

            List<String> hologramText = new ArrayList<>();
            hologramText.add(citizenType.getTitle());
            hologramText.add(ChatColor.BOLD + "RIGHT-CLICK");

            Location hologramLocation = npc.getLocation().add(0, 1.65, 0);

            hologram.createHologram(hologramText, hologramLocation);
        }

        void removeHologram() {
            if (citizenType == CitizenType.NONE) return;
            hologram.removeHologram();
        }
    }

    /**
     * This reset timer prevents duplicate Right-Clicks on
     * citizens.
     */
    private class ResetTimer extends BukkitRunnable {

        private final Map<Player, Integer> countDowns = new ConcurrentHashMap<>();

        @Override
        public void run() {

            for (Player player : countDowns.keySet()) {

                int count = countDowns.get(player);

                if (count <= 0) {
                    countDowns.remove(player);
                } else {
                    countDowns.replace(player, --count);
                }
            }
        }

        void addPlayer(Player player) {
            countDowns.put(player, 2);
        }

        boolean containsPlayer(Player player) {
            return countDowns.containsKey(player);
        }
    }
}
