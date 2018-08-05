package com.forgestorm.spigotcore.features.optional.profession.blockbreak;

import com.forgestorm.spigotcore.features.required.database.ProfileData;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class WoodCuttingProfileData extends ProfileData {

    private long woodCuttingExp;
    private int logsHarvested;
    private int logsFailed;
}
