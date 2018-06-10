package com.forgestorm.spigotcore.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlayerDuties {

    ADMIN(0, "&cAdmin", "Staff member that has the ability to edit player profiles. Watches all staff and players."),
    BUILDER(1, "&aBuilder", "Part of the creative team that dreams up and builds for the server."),
    MOD(2, "&9Moderator", "Staff member with ability to ban, warn, mute, and moderate game play."),
    MOD_TRIAL(3, "&9Trial Moderator", "Trial staff member. Has the basic ability to discipline players."),
    MOD_SUPER(4, "&9Super Moderator", "Head moderator. Responsible for keeping regular and trial moderators honest."),
    PROGRAMMER(5, "&6Programmer", "Maintains project code, adds new features and fixes bugs"),
    SERVER_TECH(6, "&eServer Technician", "Responsible for maintaining and servicing server hardware. Also maintains database."),
    WEBSITE_TECH(7, "&eWebsite Technician", "Responsible for maintaining the website software.");

    private final int id;
    private final String name;
    private final String description;

    public static PlayerDuties fromId(int id) {
        for (PlayerDuties duty : PlayerDuties.values()) {
            if (duty.getId() == id) return duty;
        }
        return null;
    }
}
