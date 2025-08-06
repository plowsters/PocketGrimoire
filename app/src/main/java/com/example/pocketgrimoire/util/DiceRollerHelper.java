package com.example.pocketgrimoire.util;

import java.util.Random;

/**
 * Helper class for dice rolling logic.
 * Makes it easier to test and reuse.
 */
public class DiceRollerHelper {

    private static final Random random = new Random();

    /**
     * Rolls a dice with the given number of sides.
     * @param sides number of sides (e.g., 6 for D6)
     * @return result from 1 to sides
     */
    public static int rollDice(int sides) {
        if (sides <= 0) throw new IllegalArgumentException("Dice must have at least 1 side");
        return random.nextInt(sides) + 1;
    }

    /**
     * Rolls multiple dice and returns the sum.
     * @param sides number of sides
     * @param count number of dice to roll
     * @return sum of results
     */
    public static int rollMultipleDice(int sides, int count) {
        if (count <= 0) throw new IllegalArgumentException("Must roll at least 1 die");

        int total = 0;
        for (int i = 0; i < count; i++) {
            total += rollDice(sides);
        }
        return total;
    }
}