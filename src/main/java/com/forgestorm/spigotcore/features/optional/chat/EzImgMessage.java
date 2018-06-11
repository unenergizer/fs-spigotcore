package com.forgestorm.spigotcore.features.optional.chat;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.features.InitCommands;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.imgmessage.ChatIcons;
import com.forgestorm.spigotcore.util.imgmessage.ImageChar;
import com.forgestorm.spigotcore.util.imgmessage.ImageMessage;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Temporary features to demonstrate sending image icons across chat.
 */
public class EzImgMessage implements FeatureOptional, InitCommands {

    @Override
    public void onFeatureEnable(boolean manualEnable) {

    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {

    }

    @Override
    public List<FeatureOptionalCommand> registerAllCommands() {
        List<FeatureOptionalCommand> commands = new ArrayList<>();
        commands.add(new ChatIconCommand());
        return commands;
    }

    private void sendEzImgMessage(CommandSender commandSender, ChatIcons iconType, String... text) {
        final BufferedImage imageToSend;            //The BufferedImage to send
        final int height = 8;                        //The image height
        final char chor = ImageChar.BLOCK.getChar();//The character that the image is made of.
        String icon = iconType.toString().toLowerCase() + ".png"; //Image name from enum.
        File file = new File(FilePaths.CHAT_ICONS.toString() + icon); //Image path

        if (!file.exists()) return;
        try {
            imageToSend = ImageIO.read(file);
            new ImageMessage(imageToSend, height, chor).appendText(text).sendToPlayer(commandSender);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @CommandAlias("chaticon|ci")
    private class ChatIconCommand extends FeatureOptionalCommand {

        @Override
        public void setupCommand(PaperCommandManager paperCommandManager) {

            paperCommandManager.getCommandCompletions().registerCompletion("allIcons", c -> {
                List<String> iconList = new ArrayList<>();
                for (ChatIcons icon : ChatIcons.values()) {
                    iconList.add(icon.toString());
                }
                return iconList;
            });
        }

        @Default
        public void onCmd(CommandSender commandSender) {
            commandSender.sendMessage(Text.color("&cPlease enter a icon to show."));
        }

        @Subcommand("show|view|display")
        @CommandCompletion("@allIcons")
        public void showIcons(CommandSender commandSender, String chatIcon) {
            commandSender.sendMessage("");
            new EzImgMessage().sendEzImgMessage(commandSender, ChatIcons.valueOf(chatIcon), "Hi! Some text here.");
        }

        @Subcommand("list")
        public void listIcons(CommandSender commandSender) {
            int shown = 0;
            StringBuilder shownMessage = new StringBuilder();
            for (ChatIcons chatIcons : ChatIcons.values()) {
                if (shown >= 3) {
                    commandSender.sendMessage(shownMessage.toString());
                    shown = 0;
                    shownMessage = new StringBuilder();
                }

                shown++;
                shownMessage.append(ChatColor.GREEN).append(chatIcons.name()).append(ChatColor.RESET).append(", ");
            }
        }
    }
}
