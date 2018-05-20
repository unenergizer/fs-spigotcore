package com.forgestorm.spigotcore.constants;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Common sounds for players.
 */
public enum CommonSounds {

    ACTION_SUCCESS(Sound.ENTITY_PLAYER_LEVELUP, 1, .8f),
    ACTION_FAILED(Sound.BLOCK_NOTE_BASS, 1F, .5F);

    private Sound sound;
    private float volume;
    private float pitch;

    CommonSounds(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
