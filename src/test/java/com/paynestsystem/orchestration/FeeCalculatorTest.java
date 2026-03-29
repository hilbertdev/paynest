package com.paynestsystem.orchestration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for the FeeCalculator class.
 */
class FeeCalculatorTest {

    @Test
    void calculateFee_appliesPercentageAndFlatFee() {
        PaymentProvider provider = new PaymentProvider(
                "TestProvider", List.of("CARD"), List.of(Region.ZA),
                true, 100, 0.029, 5.00, 0, 100000
        );
        FeeCalculator calculator = new FeeCalculator();

        double fee = calculator.calculateFee(provider, 1000);

        // 1000 * 0.029 + 5.00 = 34.00
        assertEquals(34.00, fee, 0.01);
    }

    @Test
    void calculateTotalCharge_includesAmountAndFee() {
        PaymentProvider provider = new PaymentProvider(
                "TestProvider", List.of("CARD"), List.of(Region.ZA),
                true, 100, 0.029, 5.00, 0, 100000
        );
        FeeCalculator calculator = new FeeCalculator();

        double total = calculator.calculateTotalCharge(provider, 1000);

        // 1000 + 34.00 = 1034.00
        assertEquals(1034.00, total, 0.01);
    }

    @Test
    void calculateFee_zeroAmount_returnsOnlyFlatFee() {
        PaymentProvider provider = new PaymentProvider(
                "TestProvider", List.of("CARD"), List.of(Region.ZA),
                true, 100, 0.029, 5.00, 0, 100000
        );
        FeeCalculator calculator = new FeeCalculator();

        double fee = calculator.calculateFee(provider, 0);

        assertEquals(5.00, fee, 0.01);
    }

    @Test
    void calculateFee_noFlatFee_returnsOnlyPercentage() {
        PaymentProvider provider = new PaymentProvider(
                "TestProvider", List.of("CARD"), List.of(Region.ZA),
                true, 100, 0.035, 0.00, 0, 100000
        );
        FeeCalculator calculator = new FeeCalculator();

        double fee = calculator.calculateFee(provider, 10000);

        // 10000 * 0.035 = 350.00
        assertEquals(350.00, fee, 0.01);
    }
}
