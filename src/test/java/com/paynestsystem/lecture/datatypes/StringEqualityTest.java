package com.paynestsystem.lecture.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringEqualityTest {

    private final StringEquality demo = new StringEquality();

    @Test
    void checkReferenceEquality_returnsFalse() {
        // == checks if same object in memory
        assertFalse(demo.checkReferenceEquality());
    }

    @Test
    void checkContentEquality_returnsTrue() {
        // .equals() checks if content is the same
        assertTrue(demo.checkContentEquality());
    }
}
