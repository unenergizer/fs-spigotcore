package com.forgestorm.spigotcore.features.optional.chat;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.constants.Messages;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerAccount;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.player.PlayerUtil;
import com.forgestorm.spigotcore.util.text.JsonMessageConverter;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatManager implements FeatureOptional, CommandExecutor {


    private final JsonMessageConverter messageConverter = new JsonMessageConverter();

    private final int uniqueCommandKey = RandomChance.randomInt(11111, 99999);

    @Override
    public void onFeatureEnable(boolean manualEnable) {
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        if (SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(event.getPlayer()) == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can not do that until your profile has loaded.");
            return;
        }

        PlayerRanks playerRanks = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(event.getPlayer()).getPlayerAccount().getRank();

//        for (Player player : Bukkit.getOnlinePlayers())
//            player.sendMessage(messageConverter.convert("[jmc|run=/profile " + playerRanks.getUsernamePrefix() + "][/jmc] &7" + event.getPlayer().getName() + "&8: " + playerRanks.getChatColor() + event.getMessage()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) return false;
        if (!label.equalsIgnoreCase("ChatManager")) return false;
        return Integer.parseInt(args[0]) == uniqueCommandKey;
    }

    private void getPlayerProfileInfo(CommandSender commandSender, Player profileToView) {

        if (!profileToView.isOnline()) {
            commandSender.sendMessage(Text.color("&cPlayer not online."));
            if (commandSender instanceof Player) CommonSounds.ACTION_FAILED.play((Player) commandSender);
            return;
        }

        PlayerAccount profileToViewAccount = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(profileToView).getPlayerAccount();

        // [jmc|cmsg=endoftime]&3The End of Time[/jmc]
        commandSender.sendMessage(Messages.PROFILE_LOOKUP.toString());
        commandSender.sendMessage("&7Player&8: " + profileToView.getName());
        commandSender.sendMessage("&7Rank&8: " + profileToViewAccount.getRank().getUsernamePrefix());
        commandSender.sendMessage("&7JoinDate&8: " + profileToViewAccount.getFirstJoinDate());

        if (commandSender instanceof Player) {
            PlayerAccount commandSenderAccount = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData((Player) commandSender).getPlayerAccount();

            if (commandSenderAccount.getRank() != PlayerRanks.ADMINISTRATOR || commandSenderAccount.getRank() != PlayerRanks.MODERATOR) {
                viewPrivateProfile(commandSender, profileToView);
                commandSender.sendMessage("");
//                commandSender.sendMessage(messageConverter.convert("&7Click&7: [jmc|run=/pm " + profileToView.getName() + "]&c<PM>[/jmc] &r| [jmc|run=/friend " + profileToView.getName() + "]&e<ADD-FRIEND>[/jmc] &r| [jmc|run=/party " + profileToView.getName() + "]&a<PARTY>[/jmc]", "ChatMessage", Integer.toString(uniqueCommandKey), ""));
            }
        } else {
            viewPrivateProfile(commandSender, profileToView);
        }
    }

    private void viewPrivateProfile(CommandSender commandSender, Player profileToView) {
        PlayerAccount profileToViewAccount = SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(profileToView).getPlayerAccount();

        commandSender.sendMessage("&7UUID&8: " + profileToView.getUniqueId().toString());
        commandSender.sendMessage("&7IP&8: " + profileToViewAccount.getIp());
        commandSender.sendMessage("&7Admin&8: " + profileToViewAccount.isAdmin());
        commandSender.sendMessage("&7Mod&8: " + profileToViewAccount.isModerator());
        commandSender.sendMessage("&7Banned&8: " + profileToViewAccount.isBanned());
        commandSender.sendMessage("&7WarningPoints&8: " + profileToViewAccount.getWarningPoints());
        commandSender.sendMessage("&7Ping&8: " + PlayerUtil.getPing(profileToView));
    }
}
