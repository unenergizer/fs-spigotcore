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

    public static Location getCenterLocation(Location loc1, Location loc2) {

        //Gets the smallest and largest value in the X and Z plane and
        //puts it in minimum and maximum variables.
        double minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        double minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        double maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        double maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        //Gets the center of the two locations.
        double x = (minX + maxX) / 2;
        double z = (minZ + maxZ) / 2;

        World world = loc1.getWorld();

        return new Location(world, x + .5, loc1.getY() + 1, z + .5);
    }
}
