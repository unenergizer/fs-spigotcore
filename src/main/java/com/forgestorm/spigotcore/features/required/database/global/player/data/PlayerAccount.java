package com.forgestorm.spigotcore.features.required.database.global.player.data;

import com.forgestorm.spigotcore.constants.PlayerDuties;
import com.forgestorm.spigotcore.constants.PlayerRanks;
import com.forgestorm.spigotcore.features.required.database.ProfileData;
import com.forgestorm.spigotcore.util.text.Console;
import lombok.Getter;
import lombok.Setter;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private List<PlayerDuties> duties;
    private PlayerRanks rank;
    private boolean isBanned;
    private boolean isAdmin;
    private boolean isModerator;
    private int warningPoints;

    public String getDuties() {
        if (duties == null) return null;
        String duty = "";
        for (PlayerDuties playerDuties : duties) {
            duty = duty + playerDuties.getId() + ",";
        }
        return duty;
    }

    public void setDuties(String dutyIDs) {
        if (dutyIDs.equals("")) return;
        duties = new ArrayList<>();
        String[] splitDutyIDs = dutyIDs.split(",");
        for (String id : splitDutyIDs) {
            duties.add(PlayerDuties.fromId(Integer.parseInt(id)));
            Console.sendMessage(PlayerDuties.fromId(Integer.parseInt(id)).getName());
        }
    }
}
