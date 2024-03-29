package com.forgestorm.spigotcore.bungeecord;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.Messages;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.features.InitCommands;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BungeeCord extends FeatureRequired implements InitCommands, PluginMessageListener {

    private final Logger log = Logger.getLogger("Minecraft");

    @Override
    protected void initFeatureStart() {
        Messenger messenger = Bukkit.getServer().getMessenger();

        messenger.registerIncomingPluginChannel(SpigotCore.PLUGIN, "BungeeCord", this);
        messenger.registerOutgoingPluginChannel(SpigotCore.PLUGIN, "BungeeCord");
    }

    @Override
    protected void initFeatureClose() {
        Messenger messenger = Bukkit.getServer().getMessenger();

        messenger.unregisterIncomingPluginChannel(SpigotCore.PLUGIN, "BungeeCord", this);
        messenger.unregisterOutgoingPluginChannel(SpigotCore.PLUGIN, "BungeeCord");
    }

    @Override
    public List<FeatureOptionalCommand> registerAllCommands() {
        List<FeatureOptionalCommand> commands = new ArrayList<>();
        commands.add(new ServerCommand());
        return commands;
    }

    public void connectToBungeeServer(Player player, String server) {
        //Send connection message.
        player.sendMessage("");
        player.sendMessage(Messages.BUNGEECORD_CONNECT_SERVER.toString().replace("%s", server));

        try {
            Messenger messenger = Bukkit.getMessenger();
            if (!messenger.isOutgoingChannelRegistered(SpigotCore.PLUGIN, "BungeeCord")) {
                messenger.registerOutgoingPluginChannel(SpigotCore.PLUGIN, "BungeeCord");
            }

            if (server.length() == 0) {
                player.sendMessage("&cThe server name was empty!");
                return;
            }

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArray);

            out.writeUTF("Connect");
            out.writeUTF(server);

            player.sendPluginMessage(SpigotCore.PLUGIN, "BungeeCord", byteArray.toByteArray());

        } catch (Exception ex) {
            ex.printStackTrace();
            log.warning("Could not handle BungeeCord command from " + player.getName() + ": tried to connect to \"" + server + "\".");
        }
    }

    public void getPlayerCount(String serverName) {
        try {

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArray);
            Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

            out.writeUTF("PlayerCount");
            out.writeUTF(serverName);

            player.sendPluginMessage(SpigotCore.PLUGIN, "BungeeCord", byteArray.toByteArray());

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (subChannel.equals("PlayerCount")) {
            String server = in.readUTF(); // Name of server, as given in the arguments
            int playerCount = in.readInt();

            // TODO: Update menu with number of players on each server...
//			switch(server) {
//			case "mg-full-01":
//				((GameSelectionMenu) plugin.getGameSelectionMenu()).setArcadePlayers(playerCount);
//				break;
//			case "creative":
//				((GameSelectionMenu) plugin.getGameSelectionMenu()).setCreativePlayers(playerCount);
//				break;
//			}
        }
    }

    @CommandAlias("server")
    private class ServerCommand extends FeatureOptionalCommand {

        @Override
        public void setupCommand(PaperCommandManager paperCommandManager) {
        }

        @Default
        public void onCmd(Player player, String serverName) {
            connectToBungeeServer(player, serverName);
        }
    }
}
