package com.forgestorm.spigotcore.features.optional.rpg;

import lombok.Getter;

@Getter
public enum ItemQuality {

    COMMON("&7Common", 100),
    UNCOMMON("&bUncommon", 30),
    RARE("&1Rare", 3),
    EPIC("&5Epic", 1),
    LEGENDARY("&dLegendary", .5),
    ANCIENT("&cAncient", .01);

    private String name;
    private double spawnRate;

    ItemQuality(String name, double spawnRate) {
        this.name = name;
        this.spawnRate = spawnRate;
    }
}
