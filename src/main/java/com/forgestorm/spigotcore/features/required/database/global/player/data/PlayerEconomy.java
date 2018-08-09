package com.forgestorm.spigotcore.features.required.database.global.player.data;

import com.forgestorm.spigotcore.features.required.database.ProfileData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerEconomy extends ProfileData {
    private int gems;

    public void addGems(int gems) {
        this.gems = this.gems + gems;
    }

    public void removeGems(int gems) {
        this.gems = this.gems - gems;
    }

    public int getBalance() {
        return gems;
    }

    public boolean testBalance(int cost) {
        return cost > gems;
    }
}
