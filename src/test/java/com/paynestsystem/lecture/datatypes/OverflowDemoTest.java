package com.paynestsystem.lecture.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OverflowDemoTest {

    private final OverflowDemo demo = new OverflowDemo();

    @Test
    void getMaxValue_returns2147483647() {
        assertEquals(2147483647, demo.getMaxValue());
    }

    @Test
    void getOverflowResult_wrapsToNegative() {
        // Adding 1 to Integer.MAX_VALUE overflows to Integer.MIN_VALUE
        assertEquals(-2147483648, demo.getOverflowResult());
    }
}
