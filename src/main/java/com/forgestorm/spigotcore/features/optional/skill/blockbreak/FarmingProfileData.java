package com.forgestorm.spigotcore.features.optional.skill.blockbreak;

import com.forgestorm.spigotcore.features.required.database.ProfileData;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class FarmingProfileData extends ProfileData {

    private long farmingExp;
    private int cropsHarvested;
    private int cropsFailed;
}
