package com.forgestorm.spigotcore.features.required.discord;

import com.forgestorm.spigotcore.constants.FilePaths;
import com.forgestorm.spigotcore.features.LoadsConfig;
import com.forgestorm.spigotcore.features.required.FeatureRequired;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.security.auth.login.LoginException;
import java.io.File;

public class DiscordManager extends FeatureRequired implements LoadsConfig {

    @Getter
    private JDA javaDiscordAPI;
    private String token;
    @Getter
    private String prefix;

    @Override
    protected void initFeatureStart() {
        loadConfiguration();

        // Create connection to Discord Bog
        try {
            javaDiscordAPI = new JDABuilder(AccountType.BOT).setToken(token).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initFeatureClose() {
        javaDiscordAPI.shutdown();
    }


    @Override
    public void loadConfiguration() {
        Configuration config = YamlConfiguration.loadConfiguration(new File(FilePaths.DISCORD_MANAGER.toString()));
        token = config.getString("DiscordManager.token");
        prefix = config.getString("DiscordManager.prefix");
    }

    public void addEventListener(Object object) {
        javaDiscordAPI.addEventListener(object);
    }

    public void removeEventListener(Object object) {
        javaDiscordAPI.removeEventListener(object);
    }
}
