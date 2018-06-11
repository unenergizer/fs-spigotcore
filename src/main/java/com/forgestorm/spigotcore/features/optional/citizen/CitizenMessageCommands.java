package com.forgestorm.spigotcore.features.optional.citizen;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Subcommand;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.util.text.CenterChatText;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("cmsg")
public class CitizenMessageCommands extends FeatureOptionalCommand {

    private final CitizenManager citizenManager;

    CitizenMessageCommands(CitizenManager citizenManager) {
        this.citizenManager = citizenManager;
    }

    @Override
    public void setupCommand(PaperCommandManager paperCommandManager) {
        paperCommandManager.getCommandContexts().registerIssuerAwareContext(CommandSender.class, BukkitCommandExecutionContext::getSender);
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
    public void onCMSGCommand(Player player) {
        player.sendMessage(Text.color("&cNothing happened..."));
        CommonSounds.ACTION_FAILED.play(player);
    }

    @Subcommand("msg")
    @Private
    public void onSubCMD(Player player, String[] args) {
        Console.sendMessage(args[0]);
        citizenManager.getCitizenMessages().sendTopicMessage(player, args[1], args[2], CenterChatText.centerChatMessage("&7- - - - &m---------&r&7 - - - -"));
    }
}


