package com.forgestorm.spigotcore.features.required.world.loader;


import lombok.Data;

import java.io.File;

@Data
public class WorldData {
    private final String worldName;
    private final File sourceDirectory;
    private final boolean removeSourceDirectory;
    private final boolean loadingWorld;
}
