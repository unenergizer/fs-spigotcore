package com.forgestorm.spigotcore.features.optional.realm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RealmTier {

    TIER_0(0, 16),
    TIER_1(1, 32),
    TIER_2(2, 64),
    TIER_3(3, 128),
    TIER_4(4, 256);

    private final int tier;
    private final int size;

    public static RealmTier valueOfTier(int tier) {
        return RealmTier.valueOf("TIER_" + tier);
    }
}
