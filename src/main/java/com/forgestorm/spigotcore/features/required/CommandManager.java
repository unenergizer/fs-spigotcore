package com.forgestorm.spigotcore.features.required;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.forgestorm.spigotcore.SpigotCore;

public class CommandManager implements FeatureRequired {

    private PaperCommandManager paperCommandManager;

    @Override
    public void onServerStartup() {
        // Can only create new instance of PaperCommandManager after SpigotCore runs onEnable().
        // Otherwise we get startup errors.
        paperCommandManager = new PaperCommandManager(SpigotCore.PLUGIN);
    }

    @Override
    public void onServerShutdown() {
        paperCommandManager.unregisterCommands();
    }

    /**
     * Registers a command.
     *
     * @param baseCommand The command to register.
     */
    void registerCommand(BaseCommand baseCommand) {
        paperCommandManager.registerCommand(baseCommand);
    }

    /**
     * Unregisters a command.
     *
     * @param baseCommand The command to unregister.
     */
    void unregisterCommand(BaseCommand baseCommand) {
        paperCommandManager.unregisterCommand(baseCommand);
    }
}
