package com.forgestorm.spigotcore.features.optional.realm;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import com.forgestorm.spigotcore.util.display.Hologram;
import com.forgestorm.spigotcore.util.text.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a doorway that players will use to enter a player realm world.
 */
public class RealmDoorway extends BaseWorldObject {

    private final String doorOwnerName;
    private Hologram realmDoorTitle;
    private Location topPortalBlock;
    private Location bottomPortalBlock;

    RealmDoorway(Location location, Player doorOwner) {
        super(location);
        this.doorOwnerName = doorOwner.getName();
    }

    void enable() {

        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();

        topPortalBlock = new Location(location.getWorld(), x, y + 2, z);
        bottomPortalBlock = new Location(location.getWorld(), x, y + 1, z);

        List<String> hologramText = new ArrayList<>();
        hologramText.add(Text.color("&l" + doorOwnerName));
        hologramText.add(RealmAlignment.HOSTILE.getAlignment());

        realmDoorTitle = new Hologram(hologramText, new Location(location.getWorld(), x + .5, y + 2.2, z + .5));

        SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(location, this);
    }

    void disable() {
        SpigotCore.PLUGIN.getWorldObjectManager().removeWorldObject(this);
        realmDoorTitle.remove();
    }

    /**
     * Used to change the alignment text of a portal.
     *
     * @param realmAlignment The alignment of the portal.
     */
    void setRealmAlignment(RealmAlignment realmAlignment) {
        realmDoorTitle.changeText(realmAlignment.getAlignment(), 1);
    }

    @Override
    public void spawnWorldObject() {
        realmDoorTitle.spawnHologram();
        topPortalBlock.getBlock().setType(Material.PORTAL);
        bottomPortalBlock.getBlock().setType(Material.PORTAL);
    }

    @Override
    public void despawnWorldObject() {
        realmDoorTitle.despawnHologram();
        topPortalBlock.getBlock().setType(Material.AIR);
        bottomPortalBlock.getBlock().setType(Material.AIR);
    }
}
