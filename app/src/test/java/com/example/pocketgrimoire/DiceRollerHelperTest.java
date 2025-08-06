
package com.example.pocketgrimoire;

import com.example.pocketgrimoire.util.DiceRollerHelper;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class DiceRollerHelperTest {

    @Test
    public void rollDice_returnsValueWithinRange() {
        int result = DiceRollerHelper.rollDice(6);
        assertTrue("Result should be between 1 and 6", result >= 1 && result <= 6);
    }

    @Test
    public void rollMultipleDice_returnsSumWithinRange() {
        int result = DiceRollerHelper.rollMultipleDice(6, 3);
        // 3 dice, each 1–6 → total should be 3–18
        assertTrue("Result should be between 3 and 18", result >= 3 && result <= 18);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rollDice_throwsExceptionForInvalidSides() {
        DiceRollerHelper.rollDice(0); // Should throw
    }
}