package com.forgestorm.spigotcore.features.optional.minigame.core.score;

import com.forgestorm.spigotcore.features.optional.minigame.MinigameFramework;
import com.forgestorm.spigotcore.features.optional.minigame.core.games.sheersheep.statlisteners.PickupWool;
import com.forgestorm.spigotcore.features.optional.minigame.core.score.statlisteners.FirstKill;
import com.forgestorm.spigotcore.features.optional.minigame.core.score.statlisteners.StatListener;

import java.lang.reflect.InvocationTargetException;

/*********************************************************************************
 *
 * OWNER: Robert Andrew Brown & Joseph Rugh
 * PROGRAMMER: Robert Andrew Brown & Joseph Rugh
 * PROJECT: forgestorm-minigame-framework
 * DATE: 6/22/2017
 * _______________________________________________________________________________
 *
 * Copyright © 2017 ForgeStorm.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code and/or source may be 
 * reproduced, distributed, or transmitted in any form or by any means, 
 * including photocopying, recording, or other electronic or mechanical methods, 
 * without the prior written permission of the owner.
 */

@SuppressWarnings("unused")
public enum StatType {

    FIRST_KILL(FirstKill.class),
    PICKUP_WOOL(PickupWool.class);

    private final Class<? extends StatListener> listener;

    StatType(Class<? extends StatListener> listener) {
        this.listener = listener;
    }

    public StatListener registerListener(MinigameFramework plugin) {
        try {
            return listener.getConstructor(MinigameFramework.class).newInstance(plugin);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
