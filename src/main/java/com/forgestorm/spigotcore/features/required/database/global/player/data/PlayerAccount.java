package com.forgestorm.spigotcore.features.required.database.global.player.data;

import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.required.database.ProfileData;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class PlayerAccount extends ProfileData {

    private int databaseID;
    private UUID uuid;
    private String username;
    private String ip;
    private Timestamp firstJoinDate;
    private Timestamp lastJoinDate;
    private PlayerRanks rank;
    private boolean isBanned;
    private boolean isAdmin;
    private boolean isModerator;
    private int warningPoints;
}
