package com.paynestsystem.lecture.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegerDivisionTest {

    private final IntegerDivision demo = new IntegerDivision();

    @Test
    void divideIntegers_returns3() {
        // 7 / 2 with integers truncates to 3
        assertEquals(3, demo.divideIntegers());
    }

    @Test
    void divideWithDecimal_returns3_5() {
        // 7 / 2.0 promotes to double division
        assertEquals(3.5, demo.divideWithDecimal(), 0.001);
    }
}
