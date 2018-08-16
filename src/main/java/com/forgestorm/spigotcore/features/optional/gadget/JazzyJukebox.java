package com.forgestorm.spigotcore.features.optional.gadget;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JazzyJukebox implements FeatureOptional, LoadsConfig {

    static final String BASE_PATH = SpigotCore.PLUGIN.getDataFolder() + File.separator + "NoteBlockAPI" + File.separator;
    private final Map<Location, JukeboxGadget> jukeboxGadgetMap = new HashMap<>();

    @Override
    public void onFeatureEnable(boolean manualEnable) {
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        jukeboxGadgetMap.values().forEach(JukeboxGadget::onDisable);
        jukeboxGadgetMap.clear();
    }

    @Override
    public void loadConfiguration() {
        final String prefix = "Locations";
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.JAZZY_JUKEBOX.toString()));

        for (String entry : config.getConfigurationSection(prefix).getKeys(false)) {

            String worldName = config.getString(prefix + "." + entry + ".worldName");
            double x = config.getDouble(prefix + "." + entry + ".x");
            double y = config.getDouble(prefix + "." + entry + ".y");
            double z = config.getDouble(prefix + "." + entry + ".z");
            int distance = config.getInt(prefix + "." + entry + ".distance");
            List<String> songList = config.getStringList(prefix + "." + entry + ".list");

            Location jukeboxLocation = new Location(Bukkit.getWorld(worldName), x, y, z);
            JukeboxGadget jukeboxGadget = new JukeboxGadget(jukeboxLocation, distance, songList);
            jukeboxGadgetMap.put(jukeboxLocation, jukeboxGadget);

            jukeboxGadget.onEnable();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        jukeboxGadgetMap.values().forEach(jukeboxGadget -> jukeboxGadget.addPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        jukeboxGadgetMap.values().forEach(jukeboxGadget -> jukeboxGadget.removePlayer(event.getPlayer()));
    }

    @EventHandler
    public void onJukeboxInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.JUKEBOX) return;
        Location jukeboxLocation = event.getClickedBlock().getLocation();
        if (!jukeboxGadgetMap.containsKey(jukeboxLocation)) return;
        JukeboxGadget jukeboxGadget = jukeboxGadgetMap.get(jukeboxLocation);
        jukeboxGadget.turnOn();
    }
}