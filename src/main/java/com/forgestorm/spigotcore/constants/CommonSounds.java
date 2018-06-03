package com.forgestorm.spigotcore.constants;

import lombok.AllArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Common sounds for players.
 */
@AllArgsConstructor
public enum CommonSounds {

    ACTION_SUCCESS(Sound.ENTITY_PLAYER_LEVELUP, 1, .8f),
    ACTION_FAILED(Sound.BLOCK_NOTE_BASS, 1F, .5F);

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public void play(Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
