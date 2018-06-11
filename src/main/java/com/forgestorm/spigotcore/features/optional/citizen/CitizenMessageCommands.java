package com.forgestorm.spigotcore.features.optional.citizen;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.PreCommand;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import org.bukkit.entity.Player;


@CommandAlias("cmsg")
class CitizenMessageCommands extends FeatureOptionalCommand {

    private final CitizenMessages citizenMessages;

    CitizenMessageCommands(CitizenMessages citizenMessages) {
        this.citizenMessages = citizenMessages;
    }

    @Override
    public void setupCommand(PaperCommandManager paperCommandManager) {

    }

    @PreCommand
    public boolean onPreCommand(Player player, String commandKey, String npcName, String topicKey) {
        if (Integer.parseInt(commandKey) != citizenMessages.getUniqueCommandKey()) return true;
        if (npcName.equals("")) return true;
        return topicKey.equals("");
    }

    /**
     * Listens for the /cmsg command to be ran. This command is ran when a player clicks on a citizen message.
     * We skip instanceof checks to make sure the sender is a player for speed reasons. The console should
     * never send these commands as the console can't do anything with clickable messages.
     * <p>
     * Command breakdown:
     * label == /cmsg
     * Arg[0] == uniqueCommandKey plugin key
     * Arg[1] == npcName
     * Arg[2] == topicKey
     */
    @Default
    public void onCommand(Player player, String commandKey, String npcName, String topicKey) {
        citizenMessages.sendTopicMessage(player, npcName, topicKey, CenterChatText.centerChatMessage("&7- - - - &m---------&r&7 - - - -"));
    }
}

