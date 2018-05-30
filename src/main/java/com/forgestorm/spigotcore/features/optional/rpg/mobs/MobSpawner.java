package com.forgestorm.spigotcore.features.optional.rpg.mobs;

import com.forgestorm.spigotcore.features.required.world.worldobject.BaseWorldObject;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class MobSpawner extends BaseWorldObject {

    private final Location spawnerLocation;
    private final List<SpawnerMobDetails> spawnerMobDetails = new ArrayList<>();

    MobSpawner(Location spawnerLocation) {
        this.spawnerLocation = spawnerLocation;
    }

    @Override
    public void spawnWorldObject() {
        for (SpawnerMobDetails spawnerMobDetails : spawnerMobDetails) {

            // Spawn mobs who have no respawn time
            if (spawnerMobDetails.respawnTimeLeft == spawnerMobDetails.mobType.getDefaultRespawnTime()) {
                spawnerMobDetails.spawnEntity();
            }
        }
    }

    @Override
    public void removeWorldObject() {
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
        private MobType mobType;
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
            spawnedEntity = spawnerLocation.getWorld().spawnEntity(spawnerLocation, mobType.getEntityType());
            spawnedEntity.setCustomNameVisible(true);
            spawnedEntity.setCustomName(ChatColor.GREEN + mobType.getMobName());
        }

        void removeEntity() {
            spawnedEntity.remove();
            spawnedEntity = null;
        }
    }
}