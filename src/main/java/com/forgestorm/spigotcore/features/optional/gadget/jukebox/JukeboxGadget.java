package com.forgestorm.spigotcore.features.optional.gadget.jukebox;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.world.worldobject.AsyncWorldObjectTick;
import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import com.forgestorm.spigotcore.util.display.Hologram;
import com.forgestorm.spigotcore.util.item.ItemBuilder;
import com.forgestorm.spigotcore.util.math.RandomChance;
import com.forgestorm.spigotcore.util.text.ProgressBarString;
import com.forgestorm.spigotcore.util.text.Text;
import com.forgestorm.spigotcore.util.world.LocationUtil;
import com.xxmicloxx.NoteBlockAPI.event.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.List;

import static com.forgestorm.spigotcore.util.math.RandomChance.randomInt;

@Getter
class JukeboxGadget implements Listener {

    public static final String CLICK_TO_PLAY = Text.color("&7<RIGHT-CLICK>");
    private final Location jukeboxLocation;
    private final int distance;
    private final List<String> songList;
    private Song song;
    private PositionSongPlayer songPlayer;
    private Location hologramLocation;
    private int lastSong = 0;
    private boolean playing = false;
    private Item disc;
    private JukeboxHologram jukeboxHologram;

    JukeboxGadget(Location jukeboxLocation, int distance, List<String> songList) {
        this.jukeboxLocation = jukeboxLocation;
        this.distance = distance;
        this.songList = songList;
    }

    public void onEnable() {
        hologramLocation = LocationUtil.addToLocation(jukeboxLocation, .5, 1.2, .5);
        jukeboxHologram = new JukeboxHologram(hologramLocation, this);

        SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(hologramLocation, jukeboxHologram);

        Bukkit.getServer().getPluginManager().registerEvents(this, SpigotCore.PLUGIN);

        turnOn();
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);

        if (disc != null && disc.isOnGround()) disc.remove();

        turnOff();
        jukeboxHologram.remove();

        SpigotCore.PLUGIN.getWorldObjectManager().removeWorldObject(jukeboxHologram);
    }

    @EventHandler
    public void onSongEnd(SongEndEvent event) {
        if (event.getSongPlayer().getSong().getPath().equals(song.getPath())) {
            if (disc != null && disc.isOnGround()) disc.remove();
            playing = false;
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.getItem() == null) return;
        if (disc == null) return;
        if (disc.getUniqueId() != event.getItem().getUniqueId()) return;
        event.setCancelled(true);
    }

    private void spawnDisc() {
        if (disc != null && disc.isOnGround()) disc.remove();
        int rand = randomInt(3, 12); // Pick random disc color
        ItemStack itemStack = new ItemBuilder(Material.valueOf("RECORD_" + rand)).setTitle(song.getTitle()).build(true);
        disc = hologramLocation.getWorld().dropItemNaturally(hologramLocation, itemStack);
        disc.setCustomName(song.getPath().getName().replace(".nbs", ""));
        disc.setCustomNameVisible(true);
        disc.setVelocity(new Vector()); // Prevent from flying off randomly
        new BukkitRunnable() {
            @Override
            public void run() {
                disc.setVelocity(new Vector()); // Prevent from flying off randomly
                disc.teleport(hologramLocation);
            }
        }.runTaskLater(SpigotCore.PLUGIN, 1);
    }

    private int getNextSong() {
        int randomSong = RandomChance.randomInt(1, songList.size());
        if (randomSong == lastSong) randomSong = randomSong + 1;
        if (randomSong > songList.size()) randomSong = 0;
        lastSong = randomSong;
        return randomSong;
    }

    void turnOn() {
        if (playing) return;
        playing = true;

        // Play random song
        setSong(songList.get(getNextSong() - 1));
        spawnDisc();
    }

    private void turnOff() {
        playing = false;

        removeAllPlayers();

        songPlayer.setPlaying(false);
        songPlayer.destroy();
    }

    private void play() {
        songPlayer = new PositionSongPlayer(song);
        songPlayer.setTargetLocation(jukeboxLocation);
        songPlayer.setDistance(distance);
        songPlayer.setAutoDestroy(true);

        addAllPlayers();

        songPlayer.setPlaying(true);
    }

    private void setSong(String fileName) {
        this.song = NBSDecoder.parse(new File(JazzyJukebox.BASE_PATH + fileName + ".nbs"));
        play();
    }

    private void addAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(this::addPlayer);
    }

    private void removeAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(this::removePlayer);
    }

    public void addPlayer(Player player) {
        songPlayer.addPlayer(player);
    }

    public void removePlayer(Player player) {
        songPlayer.removePlayer(player);
    }

    private String progressBar() {
        if (song == null) return CLICK_TO_PLAY;
        if (songPlayer == null) return CLICK_TO_PLAY;
        if (!songPlayer.isPlaying()) return CLICK_TO_PLAY;
        short length = songPlayer.getSong().getLength();
        short progress = songPlayer.getTick();
        int percentage = (progress * 100) / length;
        return percentage + "% " + ProgressBarString.buildBar(percentage) + " " + progress + " / " + length;
    }

    class JukeboxHologram extends BaseWorldObject implements AsyncWorldObjectTick {

        private final JukeboxGadget jukeboxGadget;
        private Hologram hologram;
        private int lastTick = 0;

        JukeboxHologram(Location jukeboxLocation, JukeboxGadget jukeboxGadget) {
            super(jukeboxLocation);
            this.jukeboxGadget = jukeboxGadget;
            this.hologram = new Hologram(jukeboxLocation, "&c&lJazzy Jukebox", "&lPlay Music", CLICK_TO_PLAY);
        }

        public Location getLocation() {
            return hologram.getLocation();
        }

        void remove() {
            hologram.remove();
            hologram = null;
        }

        @Override
        public void spawnWorldObject() {
            if (hologram != null) hologram.spawnHologram();
        }

        @Override
        public void despawnWorldObject() {
            if (hologram != null) hologram.despawnHologram();
        }

        @Override
        public void onAsyncTick() {
            if (hologram != null) hologram.changeText(jukeboxGadget.progressBar(), 2);

            if (lastTick >= 10 && jukeboxGadget.playing) {
                int rand1 = randomInt(1, 100) / 100;
                int rand2 = randomInt(1, 100) / 100;
                int rand3 = randomInt(1, 3);

                //noinspection deprecation
                jukeboxGadget.getJukeboxLocation().getWorld().playEffect(LocationUtil.addToLocation(jukeboxLocation, rand1 + .5, rand3, rand2 + .5), Effect.NOTE, 1, 20);
                lastTick = 0;
            }

            lastTick++;
        }
    }
}
