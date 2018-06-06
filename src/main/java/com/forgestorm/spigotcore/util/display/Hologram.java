package com.forgestorm.spigotcore.util.display;

import com.forgestorm.spigotcore.util.text.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to create single and multiline holograms.
 *
 * @author unenergizer
 */
public class Hologram {

    private ArrayList<ArmorStand> armorStands = new ArrayList<>();
    private String singleLineText;
    private List<String> multilineText;
    @Getter
    private Location location;
    private boolean isSpawned = false;
    private boolean isMultilineHologram = false;

    /**
     * Creates a new instance of a Hologram that contains a single line of text.
     *
     * @param singleLineText The text that will be displayed on the hologram.
     * @param location       The location the hologram will be spawned.
     */
    public Hologram(String singleLineText, Location location) {
        this.singleLineText = singleLineText;
        this.location = location;
        isMultilineHologram = false;
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
        isMultilineHologram = true;
    }

    /**
     * Changes the Hologram display message.
     *
     * @param displayMessage The new message to show.
     */
    public void changeText(String displayMessage) {
        if (isMultilineHologram)
            throw new RuntimeException("Tried to change multiline hologram text but used single line method. User multiline version.");
        singleLineText = displayMessage;
    }

    /**
     * Changes the Hologram display message.
     *
     * @param displayMessage The new message to show.
     * @param lineToChange   The line we want to change.
     */
    public void changeText(String displayMessage, int lineToChange) {
        if (!isMultilineHologram)
            throw new RuntimeException("Tried to change singe line hologram text but used multiline method. User single line version.");
        armorStands.get(lineToChange).setCustomName(displayMessage);
    }

    /**
     * Spawns a hologram with single or multiline text at a given location.
     */
    public void spawnHologram() {
        if (isSpawned) return;
        isSpawned = true;

        double spotsMovedDown = 0;

        if (singleLineText != null) {
            // Single line hologram
            armorStands.add(createArmorStand(singleLineText, location));
        } else {
            // Multiline hologram
            for (String string : multilineText) {
                Location adjustedLocation = new Location(location.getWorld(), location.getX(), location.getY() - spotsMovedDown, location.getZ());
                armorStands.add(createArmorStand(string, adjustedLocation));

                spotsMovedDown += .3;
            }
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
        singleLineText = null;
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
