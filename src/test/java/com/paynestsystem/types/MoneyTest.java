package com.paynestsystem.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void toMajorUnit_convertsCorrectly() {
        Money money = new Money(12550, CurrencyCode.ZAR);
        assertEquals(125.50, money.toMajorUnit(), 0.001);
    }

    @Test
    void add_sameCurrency_returnsSum() {
        Money a = new Money(1000, CurrencyCode.ZAR);
        Money b = new Money(2000, CurrencyCode.ZAR);
        Money sum = a.add(b);
        assertEquals(3000, sum.amountInCents());
        assertEquals(CurrencyCode.ZAR, sum.currency());
    }

    @Test
    void add_differentCurrency_throwsException() {
        Money zar = new Money(1000, CurrencyCode.ZAR);
        Money usd = new Money(1000, CurrencyCode.USD);
        assertThrows(IllegalArgumentException.class, () -> zar.add(usd));
    }

    @Test
    void constructor_negativeAmount_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(-1, CurrencyCode.ZAR));
    }

    @Test
    void constructor_nullCurrency_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(100, null));
    }

    @Test
    void formatted_displaysSymbolAndAmount() {
        Money money = new Money(12550, CurrencyCode.ZAR);
        assertEquals("R125.50", money.formatted());
    }

    @Test
    void record_equalsAndHashCode_work() {
        Money a = new Money(5000, CurrencyCode.USD);
        Money b = new Money(5000, CurrencyCode.USD);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
