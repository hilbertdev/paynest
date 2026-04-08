package com.paynestsystem.lecture.datatypes;

/**
 * Demonstrates that char values are stored as Unicode numbers.
 *
 * <p>This class shows that characters like 'A', 'a', and '0' have
 * corresponding integer values in the Unicode character set.</p>
 */
public class CharAndUnicode {

    /**
     * Returns the Unicode value for uppercase 'A'.
     *
     * @return 65 (Unicode for 'A')
     */
    public int getCodeForUppercaseA() {
        return 'A';
    }

    /**
     * Returns the Unicode value for lowercase 'a'.
     *
     * @return 97 (Unicode for 'a')
     */
    public int getCodeForLowercaseA() {
        return 'a';
    }

    /**
     * Returns the Unicode value for digit '0'.
     *
     * @return 48 (Unicode for '0')
     */
    public int getCodeForDigitZero() {
        return '0';
    }

    /**
     * Converts any character to its Unicode integer value.
     *
     * @param character the character to convert
     * @return the Unicode integer code for the character
     */
    public int charToInt(char character) {
        return character;
    }
}
