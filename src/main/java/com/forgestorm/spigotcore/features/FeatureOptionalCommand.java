package com.forgestorm.spigotcore.features;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.forgestorm.spigotcore.SpigotCore;
import lombok.Getter;

public abstract class FeatureOptionalCommand extends BaseCommand {

    @Getter
    private boolean isEnabled = false;

    /**
     * Allows for additional command setup.  Do not register the command here.
     * We can register contexts, replacements, command completions, etc.
     * <p>
     * SEE:
     * {@link co.aikar.commands.CommandContexts}
     * {@link co.aikar.commands.CommandReplacements}
     * {@link co.aikar.commands.CommandCompletions}
     *
     * @param paperCommandManager PaperCommandManager provides methods for additional setup.
     */
    public abstract void setupCommand(PaperCommandManager paperCommandManager);

    /**
     * Enables this command.
     */
    public void enableCommand() {
        SpigotCore.PLUGIN.getPaperCommandManager().registerCommand(this);
        isEnabled = true;
    }

    /**
     * Disables this command.
     */
    public void disableCommand() {
        isEnabled = false;
        SpigotCore.PLUGIN.getPaperCommandManager().unregisterCommand(this);
    }
}
