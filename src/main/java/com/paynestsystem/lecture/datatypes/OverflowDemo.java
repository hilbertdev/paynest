package com.paynestsystem.lecture.datatypes;

/**
 * Demonstrates integer overflow behavior.
 *
 * <p>This class shows what happens when you add 1 to Integer.MAX_VALUE.
 * The value "wraps around" to Integer.MIN_VALUE (negative number).</p>
 */
public class OverflowDemo {

    private int maxValue = 2147483647;  // Integer.MAX_VALUE

    /**
     * Returns the maximum int value (2147483647).
     *
     * @return Integer.MAX_VALUE
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Returns the result of adding 1 to Integer.MAX_VALUE.
     *
     * <p>This causes overflow and wraps to -2147483648.</p>
     *
     * @return -2147483648 (Integer.MIN_VALUE)
     */
    public int getOverflowResult() {
        return maxValue + 1;
    }
}
