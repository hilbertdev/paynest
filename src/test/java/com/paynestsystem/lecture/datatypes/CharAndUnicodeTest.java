package com.paynestsystem.lecture.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharAndUnicodeTest {

    private final CharAndUnicode demo = new CharAndUnicode();

    @Test
    void getCodeForUppercaseA_returns65() {
        assertEquals(65, demo.getCodeForUppercaseA());
    }

    @Test
    void getCodeForLowercaseA_returns97() {
        assertEquals(97, demo.getCodeForLowercaseA());
    }

    @Test
    void getCodeForDigitZero_returns48() {
        assertEquals(48, demo.getCodeForDigitZero());
    }

    @Test
    void charToInt_convertsCharacterToCode() {
        assertEquals(65, demo.charToInt('A'));
        assertEquals(66, demo.charToInt('B'));
        assertEquals(97, demo.charToInt('a'));
    }
}
