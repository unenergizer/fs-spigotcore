package com.forgestorm.spigotcore.features.optional.rpg.mobs;

import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import de.tr7zw.itemnbtapi.NBTEntity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class MobSpawner extends BaseWorldObject {

    private final List<SpawnerMobDetails> spawnerMobDetails = new ArrayList<>();

    public MobSpawner(Location location) {
        super(location);
    }

    @Override
    public void spawnWorldObject() {
        for (SpawnerMobDetails spawnerMobDetails : spawnerMobDetails) {

            // Spawn mobs who have no respawn scheduler
            if (spawnerMobDetails.respawnTimeLeft == spawnerMobDetails.mobType.getDefaultRespawnTime()) {
                spawnerMobDetails.spawnEntity();
            }
        }
    }

    @Override
    public void despawnWorldObject() {
        for (SpawnerMobDetails spawnerMobDetails : spawnerMobDetails) {
            if (spawnerMobDetails.spawnedEntity != null) {
                spawnerMobDetails.removeEntity();
            }
        }
    }

    void addMob(MobType mobType) {
        spawnerMobDetails.add(new SpawnerMobDetails(mobType));
    }

    class SpawnerMobDetails {
        private final MobType mobType;
        private int respawnTimeLeft;
        private Entity spawnedEntity;

        SpawnerMobDetails(MobType mobType) {
            this.mobType = mobType;
            this.respawnTimeLeft = mobType.getDefaultRespawnTime();
        }

        public void resetSpawnTime() {
            respawnTimeLeft = mobType.getDefaultRespawnTime();
        }

        void spawnEntity() {
            spawnedEntity = location.getWorld().spawnEntity(location, mobType.getEntityType());
            spawnedEntity.setCustomNameVisible(true);
            spawnedEntity.setCustomName(ChatColor.GREEN + mobType.getMobName());

            NBTEntity nbtent = new NBTEntity(spawnedEntity);
            nbtent.setString("nbtName", mobType.getMobName());
        }

        void removeEntity() {
            spawnedEntity.remove();
            spawnedEntity = null;
        }
    }
}
