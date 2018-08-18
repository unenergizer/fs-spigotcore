package com.forgestorm.spigotcore.features.optional.discord;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.text.Text;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class DiscordMessageRelay extends ListenerAdapter implements FeatureOptional {

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        SpigotCore.PLUGIN.getDiscordManager().addEventListener(this);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        SpigotCore.PLUGIN.getDiscordManager().removeEventListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Text.color("&7[&eDiscord&7] " + event.getAuthor().getName() + "&8: &r" + event.getMessage().getContentRaw()));
        }
    }

    @EventHandler
    public void onSpigotChat(AsyncPlayerChatEvent event) {
        List<TextChannel> channel = SpigotCore.PLUGIN.getDiscordManager().getJda().getTextChannelsByName("staff-text", true);

        for (TextChannel textChannel : channel) {
            textChannel.sendMessage("[Minecraft] " + event.getPlayer().getName() + ": " + event.getMessage()).queue();
        }
    }
}
