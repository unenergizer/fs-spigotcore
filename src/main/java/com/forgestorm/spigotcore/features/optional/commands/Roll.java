package com.forgestorm.spigotcore.features.optional.commands;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.constants.Messages;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.features.InitCommands;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Roll implements FeatureOptional, InitCommands {

    @Override
    public void onFeatureEnable(boolean manualEnable) {

    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {

    }

    @Override
    public List<FeatureOptionalCommand> registerAllCommands() {
        List<FeatureOptionalCommand> commands = new ArrayList<>();
        commands.add(new RollCommand());
        return commands;
    }

    private void roll(Player player, int diceSize) {
        String name = player.getName();
        String num = Integer.toString(diceSize);
        int roll = onDiceRoll(diceSize);

        String message = Messages.ROLL.toString().replace("%player%", name).replace("%s", Integer.toString(roll)).replace("%f", num);
        player.sendMessage(message);

        //Get players near by that will receive this message.
        List<Entity> localPlayers = player.getNearbyEntities(20, 20, 20);
        int messagesReceived = 0;

        //Loop through the list of entities.
        for (Entity entity : localPlayers) {
            //The entity that is near by the player
            //If the entity is a player, then send them the message.
            if (entity instanceof Player) {

                //Finally display the local message to near by players.
                entity.sendMessage(message);
                messagesReceived++;
            }
        }

        //If no one is around to hear their message, let the player know.
        if (messagesReceived == 0) {
            player.sendMessage("");
            player.sendMessage(Messages.ROLL_UNHEARD.toString());
        }
    }

    /**
     * Displays a virtual dice roll in the players chat console.
     *
     * @param diceSize How big the dice should be.
     * @return The number the dice landed on after it was rolled.
     */
    private int onDiceRoll(int diceSize) {
        if (diceSize < 10000) {
            return (int) (Math.random() * diceSize) + 1;
        } else {
            return (int) (Math.random() * 10000) + 1;
        }
    }

    @CommandAlias("roll")
    private class RollCommand extends FeatureOptionalCommand {

        @Override
        public void setupCommand(PaperCommandManager paperCommandManager) {
            paperCommandManager.getCommandContexts().registerIssuerAwareContext(CommandSender.class, BukkitCommandExecutionContext::getSender);
        }

        @Default
        public void onRollNoArg(Player player) {
            roll(player, 100);
        }

        @Subcommand("diceSize|d|size|s")
        @Syntax("<diceSize> &e- The maximum number for this roll.")
        public void onRollWithArg(Player player, int diceSize) {
            if (diceSize < 2) {
                player.sendMessage(Text.color("DiceSize must be greater than 1."));
                CommonSounds.ACTION_FAILED.play(player);
                return;
            }
            roll(player, diceSize);
        }
    }
}
