package com.forgestorm.spigotcore.features.optional.realm;

import com.forgestorm.spigotcore.features.required.database.ProfileData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;


@Setter
@Getter
public class RealmProfileData extends ProfileData {
    private boolean hasRealm;
    private String realmTitle;
    private int realmTier;
    private String realmInsideDoorLocation;

    public void setRealmInsideDoorLocation(String realmInsideDoorLocation) {
        this.realmInsideDoorLocation = realmInsideDoorLocation;
    }

    public void setRealmInsideDoorLocation(Location location) {
        realmInsideDoorLocation = location.getBlockX() + "/" + location.getBlockY() + "/" + location.getBlockZ();
    }

    Location getRealmInsideDoorLocation(World world) {
        String[] splitLocation = realmInsideDoorLocation.split("/");
        return new Location(world, Integer.parseInt(splitLocation[0]), Integer.parseInt(splitLocation[1]), Integer.parseInt(splitLocation[2]));
    }
}
