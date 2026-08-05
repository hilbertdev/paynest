package com.paynestsystem.lecture.fizzbuzz;

/**
 * FizzBuzz Coding Exercise
 *
 * <p>This class contains the FizzBuzz problem for students to implement.
 * The rules are:</p>
 *
 * <ul>
 *   <li>For multiples of 3, return "Fizz"</li>
 *   <li>For multiples of 5, return "Buzz"</li>
 *   <li>For multiples of both 3 and 5, return "FizzBuzz"</li>
 *   <li>For all other numbers, return the number as a String</li>
 * </ul>
 *
 * <p><strong>Exercise:</strong> Implement the {@link #convert(int)} method
 * to make all tests pass.</p>
 */
public class FizzBuzz {

    /**
     * Converts a number to its FizzBuzz representation.
     *
     * <p>TODO: Implement this method following the FizzBuzz rules:</p>
     * <ul>
     *   <li>If number is divisible by 3 and 5, return "FizzBuzz"</li>
     *   <li>If number is divisible by 3, return "Fizz"</li>
     *   <li>If number is divisible by 5, return "Buzz"</li>
     *   <li>Otherwise, return the number as a String</li>
     * </ul>
     *
     * @param number the number to convert
     * @return the FizzBuzz string representation
     */
    public String convert(int number) {
        // TODO: Implement the FizzBuzz logic here
        // Hint: Use the modulo operator (%) to check divisibility
        // Hint: Check for divisibility by both 3 and 5 first!

        throw new UnsupportedOperationException("Not implemented yet - this is your exercise!");
    }

    /**
     * Prints the FizzBuzz sequence from 1 to n.
     *
     * <p>TODO (Bonus): After implementing convert(), implement this method
     * to print each value in the sequence on its own line.</p>
     *
     * @param n the upper limit of the sequence (inclusive)
     */
    public void printSequence(int n) {
        // TODO: Implement this bonus exercise
        // Hint: Use a loop from 1 to n and call convert() for each number

        throw new UnsupportedOperationException("Not implemented yet - bonus exercise!");
    }
}
