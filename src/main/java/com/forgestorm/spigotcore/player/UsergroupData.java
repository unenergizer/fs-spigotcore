package com.forgestorm.spigotcore.player;

import com.forgestorm.spigotcore.database.DatabaseTemplate;
import com.forgestorm.spigotcore.util.text.Console;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.sql.Connection;

@Setter
@Getter
public class UsergroupData extends DatabaseTemplate {

    private String someMessage = "meh - ";
    private boolean someBool;

    @Override
    public void loadDatabaseData(Connection connection) {
        Console.sendMessage(ChatColor.LIGHT_PURPLE + "UsergroupData load");
        someBool = true;
        someMessage = someMessage + "loaded it - ";
        Console.sendMessage(someMessage);
    }

    @Override
    public void saveDatabaseData(Connection connection) {
        Console.sendMessage(ChatColor.LIGHT_PURPLE + "UsergroupData save");
        someMessage = someMessage + "and saved it :) ";
        Console.sendMessage(someMessage);
    }
}