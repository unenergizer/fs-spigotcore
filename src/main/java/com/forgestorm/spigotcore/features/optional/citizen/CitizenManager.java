package com.forgestorm.spigotcore.features.optional.citizen;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.features.InitCommands;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.ShutdownTask;
import com.forgestorm.spigotcore.features.required.featuretoggle.FeatureToggleManager;
import com.forgestorm.spigotcore.util.display.Hologram;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.scheduler.ResetTimer;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.util.text.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.io.File;
import java.util.*;

public class CitizenManager implements FeatureOptional, InitCommands, ShutdownTask, LoadsConfig, Listener {

    private final Map<String, BasicCitizen> basicCitizenMap = new HashMap<>();
    private final CitizenMessages citizenMessages = new CitizenMessages();

    private ResetTimer resetTimer;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        resetTimer = new ResetTimer();
        resetTimer.runTaskTimerAsynchronously(SpigotCore.PLUGIN, 0, 20);

        // Do additional citizen setup after server start
        Console.sendMessage("[CitizenManager] Init delayed task for citizen setup.");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SpigotCore.PLUGIN, this::setupCitizen, FeatureToggleManager.FEATURE_TASK_START_DELAY);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        PlayerInteractEntityEvent.getHandlerList().unregister(this);

        resetTimer.cancel();
        resetTimer = null;

        for (BasicCitizen basicCitizen : basicCitizenMap.values()) {
            basicCitizen.disableHologram();
        }
    }

    @Override
    public List<FeatureOptionalCommand> registerAllCommands() {
        List<FeatureOptionalCommand> commands = new ArrayList<>();
        commands.add(new CitizenMessageCommands(citizenMessages));
        return commands;
    }

    @Override
    public void onServerShutdown() {
        resetTimer.cancel();
        resetTimer = null;

        for (BasicCitizen basicCitizen : basicCitizenMap.values()) {
            basicCitizen.remove();
        }

        basicCitizenMap.clear();
    }

    @Override
    public void loadConfiguration() {
        File file = new File(FilePaths.CITIZENS.toString());
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        Set<String> keys = config.getConfigurationSection("Citizens").getKeys(false);

        for (String citizenName : keys) {
            String section = "Citizens." + citizenName + ".";

            Console.sendMessage("Setting up: " + citizenName);

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
        Console.sendMessage("&e[CitizenManager] Begin setting up NPCs.");
        for (LivingEntity livingEntity : Bukkit.getWorlds().get(0).getLivingEntities()) {

            if (!livingEntity.hasMetadata("NPC")) continue;
            if (!(livingEntity instanceof Player)) continue;

            Player npc = (Player) livingEntity;

            // Basic vars
            npc.setMaxHealth(1000);
            npc.setHealth(1000);

            // Center on block
            int x = npc.getLocation().getBlockX();
            int z = npc.getLocation().getBlockZ();
            npc.teleport(new Location(npc.getWorld(), x + .5, npc.getLocation().getY(), z + .5));

            // Create Basic Citizen
            if (!basicCitizenMap.containsKey(npc.getDisplayName())) continue;
            BasicCitizen basicCitizen = basicCitizenMap.get(npc.getDisplayName());
            basicCitizen.addHologram(npc);
        }
        Console.sendMessage("&e[CitizenManager] Finished setting up NPCs.");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (resetTimer.containsPlayer(player)) return;
        if (!(event.getRightClicked() instanceof Player)) return;
        if (!event.getRightClicked().hasMetadata("NPC")) return;

        resetTimer.addPlayer(player, 2);

        Player npc = (Player) event.getRightClicked();

        if (!basicCitizenMap.containsKey(npc.getName())) return;

        String npcName = npc.getDisplayName();
        Location npcLocation = npc.getLocation();
        BasicCitizen basicCitizen = basicCitizenMap.get(npcName);


        // Toggle citizen interact event
        CitizenToggleEvent citizenToggleEvent = new CitizenToggleEvent(player, npc, basicCitizen.citizenType);
        Bukkit.getPluginManager().callEvent(citizenToggleEvent);

        // Send a clickable message
        if (!citizenMessages.initCitizenMessage(player, ((Player) event.getRightClicked()).getDisplayName())) {

            // Tries to send a clickable message first. Otherwise, send a random saying to the player
            String npcMessage = basicCitizen.getRandomChatMessage();
            if (npcMessage != null) {
                player.sendMessage(Text.color("&7[&9NPC&7] " + npcName + "&8: &r" + npcMessage));
            }
        }

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
    private class BasicCitizen {

        private final CitizenType citizenType;
        private final List<String> chatMessages;

        private Hologram hologram;

        BasicCitizen(CitizenType citizenType, List<String> chatMessages) {
            this.citizenType = citizenType;
            this.chatMessages = chatMessages;
        }

        String getRandomChatMessage() {
            if (chatMessages.isEmpty()) return null;
            int rand = RandomChance.randomInt(0, chatMessages.size() - 1);
            return chatMessages.get(rand);
        }

        void addHologram(Player npc) {
            if (citizenType == CitizenType.NONE) return;

            List<String> hologramText = new ArrayList<>();
            hologramText.add(citizenType.getTitle());
            hologramText.add("&7&lRIGHT&r&7-&lCLICK");

            Location hologramLocation = npc.getLocation().add(0, 1.65, 0);

            hologram = new Hologram(hologramText, hologramLocation);
            hologram.spawnHologram();
        }

        void disableHologram() {
            if (hologram != null) hologram.despawnHologram();
        }

        void remove() {
            chatMessages.clear();
            if (hologram != null) hologram.remove();
        }
    }
}
