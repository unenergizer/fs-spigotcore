package com.forgestorm.spigotcore.constants;

import lombok.AllArgsConstructor;

import java.io.File;

@AllArgsConstructor
public enum WorldDirectories {
    RPG("rpg"),
    MINIGAME("minigame"),
    REALMS("player_realms");

    private String folderName;

    public String getWorldDirectory() {
        return ".." + File.separator + "worlds" + File.separator + this.folderName;
    }
}
