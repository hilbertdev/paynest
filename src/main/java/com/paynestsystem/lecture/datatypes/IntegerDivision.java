package com.paynestsystem.lecture.datatypes;

/**
 * Demonstrates the difference between integer and floating-point division.
 *
 * <p>This class shows a common beginner surprise: dividing two integers
 * performs integer division (truncates the decimal), while dividing an
 * integer by a double performs floating-point division.</p>
 */
public class IntegerDivision {

    /**
     * Performs integer division: 7 / 2.
     *
     * <p>Both operands are integers, so the result is truncated to 3.</p>
     *
     * @return 3 (integer division result)
     */
    public int divideIntegers() {
        return 7 / 2;
    }

    /**
     * Performs floating-point division: 7 / 2.0.
     *
     * <p>The second operand is a double, so the result is 3.5.</p>
     *
     * @return 3.5 (floating-point division result)
     */
    public double divideWithDecimal() {
        return 7 / 2.0;
    }
}
