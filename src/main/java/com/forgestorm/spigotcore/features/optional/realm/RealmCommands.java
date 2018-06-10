package com.forgestorm.spigotcore.features.optional.realm;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import com.forgestorm.spigotcore.constants.CommonSounds;
import com.forgestorm.spigotcore.constants.Messages;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.util.player.PlayerUtil;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("realm")
public class RealmCommands extends FeatureOptionalCommand {

    private final ChatColor cmd = ChatColor.DARK_AQUA;
    private final ChatColor white = ChatColor.WHITE;
    private final ChatColor txt = ChatColor.GRAY;

    private final RealmManager realmManager;

    RealmCommands(RealmManager realmManager) {
        this.realmManager = realmManager;
    }

    @PreCommand
    public boolean onPreCommand(Player player) {
        if (!realmManager.hasRealm(player)) {
            player.sendMessage(Text.color("&cYou must open your realm before you can do this."));
            CommonSounds.ACTION_FAILED.play(player);
            return true;
        }
        return false;
    }

    @Default
    public void onRealmCommand(CommandSender sender) {
        showCommandList(sender);
    }

    private void showCommandList(CommandSender commandSender) {
        commandSender.sendMessage(Messages.BAR_REALM.toString());
        commandSender.sendMessage("");
        commandSender.sendMessage(cmd + "/realm" + white + " - " + txt + "Opens the realm menu.");
        commandSender.sendMessage(cmd + "/realm shop" + white + " - " + txt + "Opens the realm shop menu.");
        commandSender.sendMessage(cmd + "/realm upgrade" + white + " - " + txt + "Upgrade realm size and build region.");
        commandSender.sendMessage(cmd + "/realm title <message>" + white + " - " + txt + "Set realm join message.");
        commandSender.sendMessage(cmd + "/realm invite <commandSender>" + white + " - " + txt + "Invite friends to build.");
        commandSender.sendMessage(cmd + "/realm uninvite <commandSender>" + white + " - " + txt + "Stop friends from building.");
        commandSender.sendMessage(cmd + "/realm help" + white + " - " + txt + "Shows realm commands.");
        commandSender.sendMessage(cmd + "/realm reset" + white + " - " + txt + "Resets realm. Warning! Cannot be undone.");
        commandSender.sendMessage(Messages.BAR_BOTTOM.toString());
    }

    @Override
    public void setupCommand(PaperCommandManager paperCommandManager) {
        paperCommandManager.getCommandContexts().registerIssuerAwareContext(CommandSender.class, BukkitCommandExecutionContext::getSender);
    }

    @Subcommand("help|h")
    public void getRealmHelp(CommandSender commandSender) {
        showCommandList(commandSender);
    }

    @Subcommand("reset")
    @Description("DANGER: Destroys an entire realm and starts you with a fresh new one.")
    public void getRealmReset(Player player) {
        player.sendMessage("getRealmReset....");
    }

    @Subcommand("shop|s")
    public void getRealmShop(Player player) {
        player.sendMessage("getRealmShop....");
    }

    @Subcommand("upgrade|u")
    public void getRealmUpgrade(Player player) {
        player.sendMessage("getRealmUpgrade....");
    }

    @Subcommand("invite|inv|add")
    @CommandCompletion("@players")
    @Syntax("<player> &e- Adds a player to realm build list.")
    public void getRealmBuildInvite(Player player, String playerName) {
        Player builder = PlayerUtil.findPlayer(player, playerName);
        if (builder == null) return;
        realmManager.getRealm(player).addBuilder(builder);
    }

    @Subcommand("uninvite|uninv|remove")
    @CommandCompletion("@players")
    @Syntax("<player> &e- Removes a player to realm build list.")
    public void getRealmBuildRemove(Player player, String playerName) {
        Player builder = PlayerUtil.findPlayer(player, playerName);
        if (builder == null) return;
        realmManager.getRealm(player).removeBuilder(builder);
    }

    @Subcommand("title|t")
    @Syntax("<RealmJoinMessage> &e- Sets your realms join message.")
    public void getRealmTitleChange(Player player, String title) {
        if (title.equals("")) {
            player.sendMessage(Text.color("&cYou must enter a valid title."));
            CommonSounds.ACTION_FAILED.play(player);
            return;
        }
        player.sendMessage(Text.color("&7New Title&8: &r") + title);
        realmManager.getProfileData(player).setRealmTitle(title);
        realmManager.getRealm(player).setTitle(title);
    }
}
