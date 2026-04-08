package com.paynestsystem.lecture.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CastingDemoTest {

    private final CastingDemo demo = new CastingDemo();

    @Test
    void getOriginalValue_returns3_75() {
        assertEquals(3.75, demo.getOriginalValue(), 0.001);
    }

    @Test
    void getCastResult_returns3() {
        // Casting 3.75 to int truncates the decimal
        assertEquals(3, demo.getCastResult());
    }
}
