package com.forgestorm.spigotcore.features.optional.player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;

@CommandAlias("test")
public class TestCommand extends BaseCommand {

    @Override
    public void execute(CommandIssuer issuer, String commandLabel, String[] args) {
        issuer.sendMessage("command ran!");
    }

}
