package com.forgestorm.spigotcore.features.optional.player;

import com.forgestorm.spigotcore.features.required.database.ProfileData;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class PersistentInventoryData extends ProfileData {

    private String inventoryBase64;
}
