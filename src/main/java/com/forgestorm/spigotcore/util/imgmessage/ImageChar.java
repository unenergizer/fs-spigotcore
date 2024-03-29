package com.forgestorm.spigotcore.util.imgmessage;

/**
 * User: bobacadodl
 * Date: 1/25/14
 * Time: 11:03 PM
 * https://github.com/bobacadodl/ImageMessage
 */
@SuppressWarnings("SpellCheckingInspection")
public enum ImageChar {
    BLOCK('\u2588'),
    DARK_SHADE('\u2593'),
    MEDIUM_SHADE('\u2592'),
    LIGHT_SHADE('\u2591');
    private final char c;

    ImageChar(char c) {
        this.c = c;
    }

    public char getChar() {
        return c;
    }
}
