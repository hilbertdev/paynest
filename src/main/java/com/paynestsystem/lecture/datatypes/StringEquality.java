package com.paynestsystem.lecture.datatypes;

/**
 * Demonstrates the difference between == and .equals() for Strings.
 *
 * <p>This class shows a common beginner mistake: using == to compare
 * String content. == checks if two references point to the same object,
 * while .equals() checks if the content is the same.</p>
 */
public class StringEquality {

    private String literalHello = "Hello";
    private String newHello = new String("Hello");

    /**
     * Checks if the two String variables are the same object using ==.
     *
     * <p>Even though both contain "Hello", they are different objects
     * in memory, so this returns false.</p>
     *
     * @return false (different objects)
     */
    public boolean checkReferenceEquality() {
        return literalHello == newHello;
    }

    /**
     * Checks if the two String variables have the same content using .equals().
     *
     * <p>Both contain "Hello", so this returns true.</p>
     *
     * @return true (same content)
     */
    public boolean checkContentEquality() {
        return literalHello.equals(newHello);
    }
}
