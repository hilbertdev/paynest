package com.paynestsystem.lecture.datatypes;

/**
 * Demonstrates basic Java primitive variable declarations and values.
 *
 * <p>This class exposes simple primitive values that beginners can inspect
 * to understand the four basic primitive types: int, double, char, and boolean.</p>
 */
public class PrimitiveVariables {

    private int age = 21;
    private double price = 12.50;
    private char grade = 'A';
    private boolean isLoggedIn = false;

    /**
     * Returns the age value (21).
     *
     * @return the int value 21
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the price value (12.50).
     *
     * @return the double value 12.50
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the grade value ('A').
     *
     * @return the char value 'A'
     */
    public char getGrade() {
        return grade;
    }

    /**
     * Returns the logged in status (false).
     *
     * @return the boolean value false
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
