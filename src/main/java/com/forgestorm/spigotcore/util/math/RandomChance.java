package com.forgestorm.spigotcore.util.math;

import java.util.Random;

public class RandomChance {

    /**
     * Roll to see if the computer can beat your percent given.
     *
     * @param percent The percent you want to test. 100 will always be true.
     * @return Returns true if the computer generates a number higher than the
     * one you supplied.
     */
    public static boolean testChance(int percent) {
        Random random = new Random();
        int success = random.nextInt(100) + 1;

        return percent >= success;
    }

    /**
     * Generates a random integer between the min value and the max value.
     *
     * @param min the minimal value
     * @param max the maximum value
     * @return a random value between the min value and the max value
     */
    public static int randomInt(int min, int max) {
        Random rand = new Random();

        return rand.nextInt((max - min) + 1) + min;
    }
}
