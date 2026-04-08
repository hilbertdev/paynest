package com.paynestsystem.lecture.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputParsingTest {

    private final InputParsing demo = new InputParsing();

    @Test
    void parseAge_validInput_returnsParsedValue() {
        assertEquals(21, demo.parseAge("21"));
        assertEquals(0, demo.parseAge("0"));
        assertEquals(100, demo.parseAge("100"));
    }

    @Test
    void parseAge_invalidInput_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> demo.parseAge("abc"));
        assertThrows(NumberFormatException.class, () -> demo.parseAge("21.5"));
        assertThrows(NumberFormatException.class, () -> demo.parseAge(""));
    }

    @Test
    void parseHeight_validInput_returnsParsedValue() {
        assertEquals(1.75, demo.parseHeight("1.75"), 0.001);
        assertEquals(0.0, demo.parseHeight("0.0"), 0.001);
        assertEquals(180.5, demo.parseHeight("180.5"), 0.001);
    }

    @Test
    void parseHeight_invalidInput_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> demo.parseHeight("tall"));
        assertThrows(NumberFormatException.class, () -> demo.parseHeight(""));
        assertThrows(NumberFormatException.class, () -> demo.parseHeight("1.75m"));
    }
}
