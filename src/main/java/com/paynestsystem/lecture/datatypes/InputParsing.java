package com.paynestsystem.lecture.datatypes;

/**
 * Demonstrates parsing strings to numeric types.
 *
 * <p>This class shows how to convert String input to int and double values,
 * and what happens when the input cannot be parsed (NumberFormatException).</p>
 */
public class InputParsing {

    /**
     * Parses a string to an integer age value.
     *
     * <p>Valid input: "21" returns 21</p>
     * <p>Invalid input: "abc" throws NumberFormatException</p>
     *
     * @param input the string to parse
     * @return the parsed integer value
     * @throws NumberFormatException if the input is not a valid integer
     */
    public int parseAge(String input) {
        return Integer.parseInt(input);
    }

    /**
     * Parses a string to a double height value.
     *
     * <p>Valid input: "1.75" returns 1.75</p>
     * <p>Invalid input: "tall" throws NumberFormatException</p>
     *
     * @param input the string to parse
     * @return the parsed double value
     * @throws NumberFormatException if the input is not a valid double
     */
    public double parseHeight(String input) {
        return Double.parseDouble(input);
    }
}
