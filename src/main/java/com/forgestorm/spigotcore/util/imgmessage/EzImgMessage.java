package com.forgestorm.spigotcore.util.imgmessage;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.feature.FeatureOptional;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Temporary feature to demonstrate sending image icons across chat.
 */
public class EzImgMessage implements FeatureOptional, CommandExecutor {

    private boolean isEnabled = false;

    @Override
    public void onEnable(boolean manualEnable) {
        isEnabled = true;
        SpigotCore.PLUGIN.getCommand("chaticon").setExecutor(this);
    }

    @Override
    public void onDisable(boolean manualDisable) {
        isEnabled = false;
    }

    private void sendEzImgMessage(Player player, ChatIcons iconType, String... text) {
        final BufferedImage imageToSend;            //The BufferedImage to send
        final int height = 8;                        //The image height
        final char chor = ImageChar.BLOCK.getChar();//The character that the image is made of.
        String icon = iconType.toString().toLowerCase() + ".png"; //Image name from enum.
        File file = new File(FilePaths.CHAT_ICONS.toString() + icon); //Image path

        if (!file.exists()) return;
        try {
            imageToSend = ImageIO.read(file);
            new ImageMessage(imageToSend, height, chor).appendText(text).sendToPlayer(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!isEnabled) return false;
        Player player = (Player) sender;

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "show":
                case "display":
                    sender.sendMessage("");
                    new EzImgMessage().sendEzImgMessage(player, ChatIcons.valueOf(args[1].toUpperCase()), "Hi " + player.getName() + "!");
                    break;
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                int shown = 0;
                StringBuilder shownMessage = new StringBuilder();
                for (ChatIcons chatIcons : ChatIcons.values()) {
                    if (shown >= 3) {
                        player.sendMessage(shownMessage.toString());
                        shown = 0;
                        shownMessage = new StringBuilder();
                    }

                    shown++;
                    shownMessage.append(ChatColor.GREEN).append(chatIcons.name()).append(ChatColor.RESET).append(", ");
                }
            }
        }
        return false;
    }
}
