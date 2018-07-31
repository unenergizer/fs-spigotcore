package com.forgestorm.spigotcore.util.display;

import com.forgestorm.spigotcore.util.text.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Used to create single and multiline holograms.
 *
 * @author unenergizer
 */
public class Hologram {

    private ArrayList<ArmorStand> armorStands = new ArrayList<>();
    private List<String> multilineText;
    @Getter
    private Location location;
    private boolean isSpawned = false;

    /**
     * Creates a new instance of a Hologram that contains a single line of text.
     *
     * @param singleLineText The text that will be displayed on the hologram.
     * @param location       The location the hologram will be spawned.
     */
    public Hologram(String singleLineText, Location location) {
        this.multilineText = new ArrayList<>(Arrays.asList(singleLineText));
        this.location = location;
    }

    /**
     * Creates a new instance of a Hologram that contains a single line of text.
     *
     * @param singleLineText The text that will be displayed on the hologram.
     * @param location       The location the hologram will be spawned.
     */
    public Hologram(Location location, String ... singleLineText) {
        this.multilineText = new ArrayList<>(Arrays.asList(singleLineText));
        this.location = location;
    }

    /**
     * Creates a new instance of a Hologram that contains multiline text.
     *
     * @param multilineText The lines of text that will be displayed on the hologram.
     * @param location      The location the hologram will be spawned.
     */
    public Hologram(List<String> multilineText, Location location) {
        this.multilineText = multilineText;
        this.location = location;
    }

    /**
     * Changes the Hologram display message.
     *
     * @param displayMessage The new message to show.
     */
    public void changeText(String displayMessage) {
        changeText(displayMessage, 0);
    }

    /**
     * Changes the Hologram display message.
     *
     * @param displayMessage The new message to show.
     * @param lineToChange   The line we want to change.
     */
    public synchronized void changeText(String displayMessage, int lineToChange) {
        armorStands.get(lineToChange).setCustomName(displayMessage);
        multilineText.set(lineToChange, displayMessage);
    }

    /**
     * Spawns a hologram with single or multiline text at a given location.
     */
    public void spawnHologram() {
        if (isSpawned) return;
        isSpawned = true;

        double pixelsMovedDown = 0;

        for (String string : multilineText) {
            Location adjustedLocation = new Location(location.getWorld(), location.getX(), location.getY() - pixelsMovedDown, location.getZ());
            armorStands.add(createArmorStand(string, adjustedLocation));

            pixelsMovedDown += .3;
        }
    }

    /**
     * This will despawn a hologram.
     */
    public void despawnHologram() {
        if (!isSpawned) return;
        isSpawned = false;
        armorStands.forEach(Entity::remove);
        armorStands.clear();
    }

    /**
     * Completely removes and nullifies this hologram.
     * After this happens, this hologram can no longer be used!
     */
    public void remove() {
        despawnHologram();
        if (multilineText != null) {
            multilineText.clear();
            multilineText = null;
        }
        location = null;
        armorStands = null;
    }

    /**
     * This will spawn an armor stand with a hologram type setup.
     *
     * @param name     The singleLineText to display over the hologram.
     * @param location The location to spawn the hologram.
     * @return Returns the generated entity.
     */
    private ArmorStand createArmorStand(String name, Location location) {
        if (!location.getChunk().isLoaded())
            throw new RuntimeException("Tried to spawn a hologram in an unloaded chunk.");

        String worldName = location.getWorld().getName();
        ArmorStand stand = (ArmorStand) Bukkit.getWorld(worldName).spawnEntity(location, EntityType.ARMOR_STAND);

        // Setup armor stand
        stand.setSmall(true);
        stand.setBasePlate(false);
        stand.setCanPickupItems(false);
        stand.setCollidable(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setVisible(false);
        stand.setRemoveWhenFarAway(false);

        // Set the armor stands name
        stand.setCustomName(Text.color(name));
        stand.setCustomNameVisible(true);

        return stand;
    }
}
