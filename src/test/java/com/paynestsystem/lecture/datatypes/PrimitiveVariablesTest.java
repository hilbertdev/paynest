package com.paynestsystem.lecture.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrimitiveVariablesTest {

    private final PrimitiveVariables demo = new PrimitiveVariables();

    @Test
    void getAge_returns21() {
        assertEquals(21, demo.getAge());
    }

    @Test
    void getPrice_returns12_50() {
        assertEquals(12.50, demo.getPrice(), 0.001);
    }

    @Test
    void getGrade_returnsA() {
        assertEquals('A', demo.getGrade());
    }

    @Test
    void isLoggedIn_returnsFalse() {
        assertFalse(demo.isLoggedIn());
    }
}
