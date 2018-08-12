package com.forgestorm.spigotcore.features.optional.commands;

import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.features.InitCommands;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.util.text.ProgressBarString;
import com.forgestorm.spigotcore.util.text.Text;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayMidi implements FeatureOptional, InitCommands {

    private static final String basePath = SpigotCore.PLUGIN.getDataFolder() + File.separator + "NoteBlockAPI" + File.separator;
    private Player player;
    private SongPlayer songPlayer;
    private BukkitRunnable syncRunnable;

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        syncRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                showProgressBar();
            }
        };
        syncRunnable.runTaskTimer(SpigotCore.PLUGIN, 0, 1);
    }

    private void showProgressBar() {
        if (songPlayer == null) return;
        if (songPlayer.getSong() == null) return;
        if (player == null) return;

        short length = songPlayer.getSong().getLength();
        short progress = songPlayer.getTick();

        if (progress == -1) return;

        // Show song percentage
        int percentage = (progress * 100) / length;
        SpigotCore.PLUGIN.getTitleManager().sendActionbar(player, percentage + "% " + ProgressBarString.buildBar(percentage) + " " + progress + " / " + length);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        syncRunnable.cancel();
        songPlayer.setPlaying(false);
        songPlayer.destroy();
    }

    @Override
    public List<FeatureOptionalCommand> registerAllCommands() {
        List<FeatureOptionalCommand> commands = new ArrayList<>();
        commands.add(new PlayMidiCommand());
        return commands;
    }

    private void playSong(CommandSender commandSender, String songName) {
        File file = new File(basePath + songName.replace(".nbs", "") + ".nbs");

        if (!file.exists()) {
            commandSender.sendMessage(Text.color("&cFile &e" + songName + "&c could not be found."));
            commandSender.sendMessage(Text.color("&7" + file.getAbsolutePath()));
            return;
        }

        commandSender.sendMessage(Text.color("&aNow playing &e" + songName + "&a."));

        if (songPlayer != null) {
            songPlayer.setPlaying(false);
            songPlayer.destroy();
        }

        songPlayer = new RadioSongPlayer(NBSDecoder.parse(file));
        songPlayer.addPlayer((Player) commandSender);
        songPlayer.setAutoDestroy(true);
        songPlayer.setPlaying(true);

        player = (Player) commandSender;
    }

    @CommandAlias("play")
    private class PlayMidiCommand extends FeatureOptionalCommand {

        @Override
        public void setupCommand(PaperCommandManager paperCommandManager) {
            //noinspection ConstantConditions
            paperCommandManager.getCommandCompletions().registerCompletion("allSongs", c -> new ArrayList<>(Arrays.asList(new File(basePath).list())));
        }

        @Default
        public void onCmd(CommandSender commandSender) {
            commandSender.sendMessage(Text.color("&cPlease enter a songName name to play."));
        }

        @Subcommand("nbs")
        @CommandCompletion("@allSongs")
        public void playMidi(CommandSender commandSender, String... songName) {
            commandSender.sendMessage("");

            if (songName.length > 1) {
                StringBuilder name = new StringBuilder(songName[0]);
                for (int i = 1; i < songName.length; i++) {
                    name.append(" ").append(songName[i]);
                }
                playSong(commandSender, name.toString());
            } else {
                playSong(commandSender, songName[0]);
            }
        }

        @Subcommand("stop")
        public void stopMidi() {
            songPlayer.setPlaying(false);
        }
    }
}
