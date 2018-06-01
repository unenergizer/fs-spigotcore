package com.forgestorm.spigotcore.features.optional.player;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import com.forgestorm.spigotcore.features.ForgeStormCommand;
import org.bukkit.command.CommandSender;

@CommandAlias("test")
public class TestCommand extends ForgeStormCommand {

    @CommandAlias("test|testing")
    public void execute(CommandSender sender) {
        sender.sendMessage("command ran!");
    }

    @Override
    public void setupCommand(PaperCommandManager paperCommandManager) {
        paperCommandManager.getCommandContexts().registerIssuerAwareContext(CommandSender.class, BukkitCommandExecutionContext::getSender);
    }
}
