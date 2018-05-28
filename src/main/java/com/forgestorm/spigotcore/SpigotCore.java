package com.forgestorm.spigotcore;

import com.forgestorm.spigotcore.citizen.CitizenManager;
import com.forgestorm.spigotcore.database.DatabaseManager;
import com.forgestorm.spigotcore.database.ProfileManager;
import com.forgestorm.spigotcore.feature.FeatureManager;
import com.forgestorm.spigotcore.feature.FeatureOptional;
import com.forgestorm.spigotcore.player.*;
import com.forgestorm.spigotcore.rpg.mobs.MobManager;
import com.forgestorm.spigotcore.util.imgmessage.EzImgMessage;
import com.forgestorm.spigotcore.util.text.Console;
import com.forgestorm.spigotcore.world.ServerSpawn;
import com.forgestorm.spigotcore.world.WorldHologram;
import com.forgestorm.spigotcore.world.WorldSettings;
import com.forgestorm.spigotcore.world.blockregen.BlockRegenerationManager;
import com.forgestorm.spigotcore.world.blockregen.HiddenPaths;
import com.forgestorm.spigotcore.world.lantern.Lantern;
import com.forgestorm.spigotcore.world.loot.ChestLoot;
import com.forgestorm.spigotcore.world.loot.DragonEggLoot;
import com.forgestorm.spigotcore.world.loot.NewChestLoot;
import com.forgestorm.spigotcore.world.worldobject.WorldObjectManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SpigotCore extends JavaPlugin {

    public static SpigotCore PLUGIN;

    private final DatabaseManager databaseManager = new DatabaseManager();
    private final ProfileManager profileManager = new ProfileManager();
    private final BlockRegenerationManager blockRegenerationManager = new BlockRegenerationManager();
    private final WorldObjectManager worldObjectManager = new WorldObjectManager();
    private final FeatureManager featureManager = new FeatureManager();

    @Override
    public void onEnable() {
        Console.sendMessage(ChatColor.GOLD + "---------[ FS SpigotCore Initializing ]---------");
        PLUGIN = this;

        // Enable required features first
        databaseManager.onServerStartup();
        profileManager.onServerStartup();
        blockRegenerationManager.onServerStartup();
        worldObjectManager.onServerStartup();
        featureManager.onServerStartup();

        // Enable optional features last
        initOptionalFeatures();
    }

    @Override
    public void onDisable() {
        // First disable all optional features
        featureManager.onServerShutdown();

        // Finally disable all required features
        blockRegenerationManager.onServerShutdown();
        worldObjectManager.onServerShutdown();
        profileManager.onServerShutdown();
        databaseManager.onServerShutdown();
    }

    private void initOptionalFeatures() {
        List<FeatureOptional> features = new ArrayList<>();

        features.add(new ServerSpawn());
        features.add(new SimpleChat());
        features.add(new UsergroupChat());
        features.add(new LobbyPlayer());
        features.add(new PlayerGreeting());
        features.add(new PlayerListText());
        features.add(new PlayerBossBar());
        features.add(new CitizenManager());
        features.add(new WorldSettings());
        features.add(new NewChestLoot());
        features.add(new ChestLoot());
        features.add(new DragonEggLoot());
        features.add(new HiddenPaths());
        features.add(new MobManager());
        features.add(new DoubleJump());
        features.add(new Lantern());
        features.add(new WorldHologram());
        features.add(new EzImgMessage());

        // Finally, add all features
        featureManager.addFeatures(features);
    }
}