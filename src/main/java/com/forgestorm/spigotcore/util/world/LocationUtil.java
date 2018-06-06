package com.forgestorm.spigotcore.util.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    public static boolean doLocationsMatch(Location location1, Location location2) {
        return location1.getBlockX() == location2.getBlockX()
                && location1.getBlockZ() == location2.getBlockZ()
                && location1.getWorld().getName().equals(location2.getWorld().getName());
    }

    public static boolean isWorldLoaded(String worldName) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equals(worldName)) return true;
        }
        return false;
    }

    public static Location addToLocation(Location location, double x, double y, double z) {
        return new Location(location.getWorld(),location.getX() + x, location.getY() + y, location.getZ() + z);
    }
}
