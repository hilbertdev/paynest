package com.paynestsystem.orchestration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for the PaymentProvider class.
 */
class PaymentProviderTest {

    @Test
    void supportsPaymentType_returnsTrue_whenTypeIsSupported() {
        PaymentProvider provider = new PaymentProvider(
                "TestProvider", List.of("CARD", "EFT"), List.of(Region.ZA),
                true, 100, 0.029, 5.00, 0, 100000
        );

        assertTrue(provider.supportsPaymentType("CARD"));
        assertTrue(provider.supportsPaymentType("EFT"));
    }

    @Test
    void supportsPaymentType_returnsFalse_whenTypeIsNotSupported() {
        PaymentProvider provider = new PaymentProvider(
                "TestProvider", List.of("CARD"), List.of(Region.ZA),
                true, 100, 0.029, 5.00, 0, 100000
        );

        assertFalse(provider.supportsPaymentType("WALLET"));
    }

    @Test
    void supportsRegion_returnsTrue_whenRegionIsSupported() {
        PaymentProvider provider = new PaymentProvider(
                "TestProvider", List.of("CARD"), List.of(Region.ZA, Region.EU),
                true, 100, 0.029, 5.00, 0, 100000
        );

        assertTrue(provider.supportsRegion(Region.ZA));
        assertTrue(provider.supportsRegion(Region.EU));
    }

    @Test
    void supportsRegion_returnsFalse_whenRegionIsNotSupported() {
        PaymentProvider provider = new PaymentProvider(
                "TestProvider", List.of("CARD"), List.of(Region.ZA),
                true, 100, 0.029, 5.00, 0, 100000
        );

        assertFalse(provider.supportsRegion(Region.US));
    }

    @Test
    void isAvailable_returnsConfiguredValue() {
        PaymentProvider available = new PaymentProvider(
                "Available", List.of("CARD"), List.of(Region.ZA),
                true, 100, 0.029, 5.00, 0, 100000
        );
        PaymentProvider unavailable = new PaymentProvider(
                "Unavailable", List.of("CARD"), List.of(Region.ZA),
                false, 100, 0.029, 5.00, 0, 100000
        );

        assertTrue(available.isAvailable());
        assertFalse(unavailable.isAvailable());
    }
}
