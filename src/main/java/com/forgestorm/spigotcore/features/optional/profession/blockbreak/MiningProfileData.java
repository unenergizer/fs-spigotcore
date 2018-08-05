package com.forgestorm.spigotcore.features.optional.profession.blockbreak;

import com.forgestorm.spigotcore.features.required.database.ProfileData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;


@Setter
@Getter
public class MiningProfileData extends ProfileData {

    private long miningExp;
    private int oresMined;
    private int oresFailed;
}
