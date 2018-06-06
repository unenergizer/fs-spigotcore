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

    private Hologram realmDoorTitle;
    private Location topPortalBlock;
    private Location bottomPortalBlock;

    void enable(Player player, Location doorLocation) {

        int x = (int) doorLocation.getX();
        int y = (int) doorLocation.getY();
        int z = (int) doorLocation.getZ();

        topPortalBlock = new Location(doorLocation.getWorld(), x, y + 2, z);
        bottomPortalBlock = new Location(doorLocation.getWorld(), x, y + 1, z);

        List<String> hologramText = new ArrayList<>();
        hologramText.add(Text.color("&l" + player.getName()));
        hologramText.add(RealmAlignment.HOSTILE.getAlignment());

        realmDoorTitle = new Hologram(hologramText, new Location(doorLocation.getWorld(), x + .5, y + 2.2, z + .5));

        SpigotCore.PLUGIN.getWorldObjectManager().addWorldObject(doorLocation, this);
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