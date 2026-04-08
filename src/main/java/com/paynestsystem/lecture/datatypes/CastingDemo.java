package com.paynestsystem.lecture.datatypes;

/**
 * Demonstrates type casting from double to int.
 *
 * <p>This class shows that casting a double to an int truncates
 * (cuts off) the decimal portion. The original value is unchanged.</p>
 */
public class CastingDemo {

    private double originalValue = 3.75;

    /**
     * Returns the original double value (3.75).
     *
     * @return the original value before casting
     */
    public double getOriginalValue() {
        return originalValue;
    }

    /**
     * Returns the result of casting 3.75 to int.
     *
     * <p>The decimal portion is truncated, so 3.75 becomes 3.</p>
     *
     * @return 3 (the truncated integer value)
     */
    public int getCastResult() {
        return (int) originalValue;
    }
}
