package com.forgestorm.spigotcore.features.required.database.global.player.data;

import lombok.Data;

@Data
public class GlobalPlayerData {
    private PlayerAccount playerAccount;
    private PlayerSettings playerSettings;
    private PlayerEconomy playerEconomy;
}
