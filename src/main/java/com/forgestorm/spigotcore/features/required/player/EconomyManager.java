package com.forgestorm.spigotcore.features.required.player;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import com.forgestorm.spigotcore.features.required.database.global.player.data.PlayerEconomy;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class EconomyManager extends FeatureRequired implements Listener {

    @Override
    protected void initFeatureStart() {

    }

    @Override
    protected void initFeatureClose() {

    }

    public void addGems(Player player, int amount) {
        getEconomyData(player).setGems(getEconomyData(player).getGems() + amount);
    }

    public void removeGems(Player player, int amount) {
        getEconomyData(player).setGems(getEconomyData(player).getGems() - amount);
    }

    public int getBalance(Player player) {
        return getEconomyData(player).getGems();
    }

    public boolean testBalance(Player player, int cost) {
        return cost > getEconomyData(player).getGems();
    }

    private PlayerEconomy getEconomyData(Player player) {
        return SpigotCore.PLUGIN.getGlobalDataManager().getGlobalPlayerData(player).getPlayerEconomy();
    }

}
