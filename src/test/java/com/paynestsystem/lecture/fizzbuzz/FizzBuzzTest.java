package com.paynestsystem.lecture.fizzbuzz;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the FizzBuzz coding exercise.
 *
 * <p>These tests define the expected behavior of the FizzBuzz.convert() method.
 * Students should implement the method to make all tests pass.</p>
 */
class FizzBuzzTest {

    private final FizzBuzz fizzBuzz = new FizzBuzz();

    @Test
    @DisplayName("Number 1 returns '1'")
    void convert_number1_returns1() {
        assertEquals("1", fizzBuzz.convert(1));
    }

    @Test
    @DisplayName("Number 2 returns '2'")
    void convert_number2_returns2() {
        assertEquals("2", fizzBuzz.convert(2));
    }

    @Test
    @DisplayName("Number 3 returns 'Fizz'")
    void convert_number3_returnsFizz() {
        assertEquals("Fizz", fizzBuzz.convert(3));
    }

    @Test
    @DisplayName("Number 5 returns 'Buzz'")
    void convert_number5_returnsBuzz() {
        assertEquals("Buzz", fizzBuzz.convert(5));
    }

    @Test
    @DisplayName("Number 6 returns 'Fizz' (multiple of 3)")
    void convert_number6_returnsFizz() {
        assertEquals("Fizz", fizzBuzz.convert(6));
    }

    @Test
    @DisplayName("Number 10 returns 'Buzz' (multiple of 5)")
    void convert_number10_returnsBuzz() {
        assertEquals("Buzz", fizzBuzz.convert(10));
    }

    @Test
    @DisplayName("Number 15 returns 'FizzBuzz' (multiple of 3 and 5)")
    void convert_number15_returnsFizzBuzz() {
        assertEquals("FizzBuzz", fizzBuzz.convert(15));
    }

    @Test
    @DisplayName("Number 30 returns 'FizzBuzz' (multiple of 3 and 5)")
    void convert_number30_returnsFizzBuzz() {
        assertEquals("FizzBuzz", fizzBuzz.convert(30));
    }

    @Test
    @DisplayName("Number 99 returns 'Fizz' (multiple of 3)")
    void convert_number99_returnsFizz() {
        assertEquals("Fizz", fizzBuzz.convert(99));
    }

    @Test
    @DisplayName("Number 100 returns 'Buzz' (multiple of 5)")
    void convert_number100_returnsBuzz() {
        assertEquals("Buzz", fizzBuzz.convert(100));
    }
}
