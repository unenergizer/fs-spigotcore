package com.forgestorm.spigotcore.features.optional.citizen;

import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.util.text.JsonMessageConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class CitizenMessages implements LoadsConfig {

    /**
     * Converts messages from Config to clickable messages by a NPC.
     */
    private final JsonMessageConverter messageConverter = new JsonMessageConverter();

    /**
     * Key: Npc identifier, Value: Npc specific topics and respective message groups.
     * See {@link NpcTopics} for specifics.
     */
    private final Map<String, NpcTopics> npcTopicsMap = new HashMap<>();

    /**
     * Used to make sure when the command is ran to send a message, that the command
     * is being ran by the plugin. Thus preventing players being able to guess what
     * a message command is.
     */
    private final int uniqueCommandKey = RandomChance.randomInt(11111, 99999);

    CitizenMessages() {
        loadConfiguration();
    }

    @Override
    public void loadConfiguration() {
        final String pathStart = "TopicMessages";

        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.CITIZENS_MESSAGES.toString()));
        Set<String> npcKeySet = config.getConfigurationSection(pathStart).getKeys(false);

        // Loops through all NPCs
        for (String npcName : npcKeySet) {

            Set<String> topicKeySet = config.getConfigurationSection(pathStart + "." + npcName).getKeys(false);
            NpcTopics npcTopics = new NpcTopics();

            // Loop through all topic keys and get messages.
            for (String topicKey : topicKeySet) {
                final String path = pathStart + "." + npcName + "." + topicKey;

                npcTopics.addMessageTopic(topicKey, config.getStringList(path));
            }

            npcTopicsMap.put(npcName, npcTopics);
        }
    }

    /**
     * Sends the first NPC message to a player.
     *
     * @param player  The player to send messages to.
     * @param npcName The name of the NPC to get messages from.
     * @return True if the NPC exists in message map, false otherwise.
     */
    boolean initCitizenMessage(Player player, String npcName) {
        if (!npcTopicsMap.containsKey(npcName)) return false;

        sendTopicMessage(player, npcName, "init", CenterChatText.centerChatMessage("&7&o[&c&o!&7&o] Click Colored Messages Below to Interact [&c&o!&7&o]"));
        return true;
    }

    /**
     * Sends the player a BaseComponent message with clickable actions.
     *
     * @param player        The player to send the message to.
     * @param npcName       The name of the npc.
     * @param topicKey      The topic to get messages for.
     * @param messageHeader The message we will show above the NPCs chat messages.
     */
    public void sendTopicMessage(Player player, String npcName, String topicKey, String messageHeader) {
        NpcTopics npcTopics = npcTopicsMap.get(npcName);
        List<String> topicsMessages = npcTopics.getMessages(topicKey);

        player.sendMessage(""); // Blank line to make messages easier to read
        player.sendMessage(messageHeader);
        player.sendMessage(""); // Blank line to make messages easier to read
        for (String message : topicsMessages) {
            player.spigot().sendMessage(messageConverter.convert("&7[&9NPC&7] " + npcName + "&8: &r" + message, "cmsg", Integer.toString(uniqueCommandKey), npcName));
        }
    }

    /**
     * Basic data class that holds specific conversation topics for NPC.
     */
    @AllArgsConstructor
    class NpcTopics {

        /**
         * Key: unique identifier, Value: message(s). These are not parsed and considered raw.
         */
        private final Map<String, List<String>> npcSpecificMessages = new HashMap<>();

        /**
         * Adds topicKey and messages to a npc specific group of messages.
         *
         * @param topicKey The key that uniquely identifies the supplied group of messages.
         * @param messages A list of messages specific to the topicKey provided.
         */
        void addMessageTopic(String topicKey, List<String> messages) {
            if (npcSpecificMessages.containsKey(topicKey))
                throw new RuntimeException("Attempted to add duplicate topic keys. Topic keys must be unique.");
            npcSpecificMessages.put(topicKey, messages);
        }

        /**
         * Gets a list of messages based on the topic key provided.
         *
         * @param topicKey The key used to identify a list of messages.
         * @return The list of messages for said topic.
         */
        List<String> getMessages(String topicKey) {
            if (!npcSpecificMessages.containsKey(topicKey)) {
                Console.sendMessage("[CitizenMessages] attempted to get messages with key " + topicKey + " but no topics exist with this key.");
                return null;
            }
            return npcSpecificMessages.get(topicKey);
        }
    }
}
