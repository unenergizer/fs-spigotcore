package com.forgestorm.spigotcore.features.optional.realm;

import com.forgestorm.spigotcore.util.text.Text;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RealmAlignment {
    FRIENDLY("&aFriendly"),
    HOSTILE("&cHostile");

    private final String alignment;

    public String getAlignment() {
        return Text.color(alignment);
    }
}
