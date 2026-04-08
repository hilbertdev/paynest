package com.paynestsystem.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TypesDemoTest {

    @Test
    void firstNonNull_returnsFirst_whenBothPresent() {
        assertEquals("A", TypesDemo.firstNonNull("A", "B"));
    }

    @Test
    void firstNonNull_returnsSecond_whenFirstNull() {
        assertEquals("B", TypesDemo.firstNonNull(null, "B"));
    }

    @Test
    void firstNonNull_returnsNull_whenBothNull() {
        assertNull(TypesDemo.firstNonNull(null, null));
    }

    @Test
    void demoInstanceOfPatternMatching_handlesAllAccountTypes() {
        Money balance = new Money(10000, CurrencyCode.ZAR);
        Money limit = new Money(50000, CurrencyCode.ZAR);

        // Verify no exceptions are thrown for each account type
        TypesDemo.demoInstanceOfPatternMatching(new SavingsAccount("Test", balance, 0.05));
        TypesDemo.demoInstanceOfPatternMatching(new ChequeAccount("Test", balance, limit));
        TypesDemo.demoInstanceOfPatternMatching(new CreditAccount("Test", balance, limit));
    }

    @Test
    void runAllDemos_completesWithoutException() {
        // Verify the full demo runs end-to-end without errors
        TypesDemo.runAllDemos();
    }
}
