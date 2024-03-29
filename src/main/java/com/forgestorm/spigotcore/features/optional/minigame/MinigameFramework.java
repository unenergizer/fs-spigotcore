package com.forgestorm.spigotcore.features.optional.minigame;

import com.forgestorm.spigotcore.SpigotCore;
import com.forgestorm.spigotcore.features.FeatureOptionalCommand;
import com.forgestorm.spigotcore.features.InitCommands;
import com.forgestorm.spigotcore.features.optional.FeatureOptional;
import com.forgestorm.spigotcore.features.optional.minigame.commands.LobbyBungeeCommand;
import com.forgestorm.spigotcore.features.optional.minigame.commands.MinigameAdminCommands;
import com.forgestorm.spigotcore.features.optional.minigame.core.GameManager;
import lombok.Getter;

import java.util.ArrayList;
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
public class MinigameFramework implements FeatureOptional, InitCommands {

    private final List<String> configGameList = SpigotCore.PLUGIN.getConfig().getStringList("Games");

    @Override
    public void onFeatureEnable(boolean manualEnable) {
        GameManager.getInstance().setup(this);
    }

    @Override
    public void onFeatureDisable(boolean manualDisable) {
        GameManager.getInstance().onDisable();
    }

    @Override
    public List<FeatureOptionalCommand> registerAllCommands() {
        List<FeatureOptionalCommand> commands = new ArrayList<>();
        commands.add(new MinigameAdminCommands());
        commands.add(new LobbyBungeeCommand());
        return commands;
    }
}
