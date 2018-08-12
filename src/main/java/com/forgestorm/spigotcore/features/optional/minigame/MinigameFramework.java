package com.forgestorm.spigotcore.features.optional.minigame;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import com.forgestorm.spigotcore.util.text.Console;
import lombok.Getter;

import java.util.List;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 6/2/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */

@Getter
public class MinigameFramework implements FeatureOptional {

    private final List<String> configGameList = SpigotCore.PLUGIN.getConfig().getStringList("Games");

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        Console.sendMessage("STARTING UP MinigameFramework");

        GameManager.getInstance().setup(this);

        registerCommands();
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {

        // Disable the core manager
        GameManager.getInstance().onDisable();
    }

    private void registerCommands() {
        // TODO: CHANGE TO NEW COMMAND SYSTEM
//        SpigotCore.PLUGIN.getCommand("mgadmin").setExecutor(new Admin(this));
//        SpigotCore.PLUGIN.getCommand("lobby").setExecutor(new Lobby(this));
    }
}
