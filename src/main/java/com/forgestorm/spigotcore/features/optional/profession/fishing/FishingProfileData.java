package com.forgestorm.spigotcore.features.optional.profession.fishing;

import com.forgestorm.spigotcore.features.required.database.ProfileData;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class FishingProfileData extends ProfileData {

    private long fishingExp;
    private int fishCaught;
    private int fishLost;
}
