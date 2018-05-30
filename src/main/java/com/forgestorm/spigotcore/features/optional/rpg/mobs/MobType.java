package com.forgestorm.spigotcore.features.optional.rpg.mobs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.EntityType;

@AllArgsConstructor
@Data
class MobType {
    private String mobName;
    private int health;
    private EntityType entityType;
    private int defaultRespawnTime;
}
