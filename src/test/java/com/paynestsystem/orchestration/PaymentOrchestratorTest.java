package com.paynestsystem.orchestration;

import com.paynestsystem.payment.CardPayment;
import com.paynestsystem.payment.EftPayment;
import com.paynestsystem.payment.WalletPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for the PaymentOrchestrator class.
 */
class PaymentOrchestratorTest {

    private PaymentOrchestrator orchestrator;
    private PaymentProvider payFast;
    private PaymentProvider stripe;
    private PaymentProvider ozow;
    private PaymentProvider unavailableProvider;

    @BeforeEach
    void setUp() {
        orchestrator = new PaymentOrchestrator();

        payFast = new PaymentProvider(
                "PayFast", List.of("CARD", "EFT"), List.of(Region.ZA),
                true, 120, 0.029, 5.00, 0, 10000
        );

        stripe = new PaymentProvider(
                "Stripe", List.of("CARD", "WALLET"),
                List.of(Region.ZA, Region.EU, Region.US, Region.UK, Region.APAC),
                true, 200, 0.035, 10.00, 10000, 100000
        );

        ozow = new PaymentProvider(
                "Ozow", List.of("EFT"), List.of(Region.ZA),
                true, 80, 0.015, 2.00, 0, 50000
        );

        unavailableProvider = new PaymentProvider(
                "DownProvider", List.of("CARD", "EFT", "WALLET"),
                List.of(Region.ZA, Region.EU),
                false, 50, 0.01, 1.00, 0, 100000
        );

        orchestrator.registerProvider(payFast);
        orchestrator.registerProvider(stripe);
        orchestrator.registerProvider(ozow);
        orchestrator.registerProvider(unavailableProvider);
    }

    @Test
    void selectProvider_routesStandardCardPaymentToPayFast() {
        PaymentProvider selected = orchestrator.selectProvider("CARD", 5000, Region.ZA);

        assertNotNull(selected);
        assertEquals("PayFast", selected.getName());
    }

    @Test
    void selectProvider_routesHighValueCardPaymentToStripe() {
        PaymentProvider selected = orchestrator.selectProvider("CARD", 15000, Region.ZA);

        assertNotNull(selected);
        assertEquals("Stripe", selected.getName());
    }

    @Test
    void selectProvider_routesEftToLowestLatencyProvider() {
        PaymentProvider selected = orchestrator.selectProvider("EFT", 3000, Region.ZA);

        assertNotNull(selected);
        assertEquals("Ozow", selected.getName()); // 80ms < 120ms
    }

    @Test
    void selectProvider_filtersUnavailableProviders() {
        // DownProvider supports CARD in ZA with 50ms latency but is unavailable
        PaymentProvider selected = orchestrator.selectProvider("CARD", 5000, Region.ZA);

        assertNotNull(selected);
        assertNotEquals("DownProvider", selected.getName());
    }

    @Test
    void selectProvider_filtersByRegion() {
        PaymentProvider selected = orchestrator.selectProvider("CARD", 15000, Region.EU);

        assertNotNull(selected);
        assertEquals("Stripe", selected.getName()); // Only Stripe supports EU
    }

    @Test
    void selectProvider_returnsNull_whenNoProviderAvailable() {
        PaymentProvider selected = orchestrator.selectProvider("EFT", 1000, Region.APAC);

        assertNull(selected); // No EFT provider in APAC
    }

    @Test
    void selectProvider_filtersAmountOutOfRange() {
        // Amount of 200,000 exceeds all providers' max amount
        PaymentProvider selected = orchestrator.selectProvider("CARD", 200000, Region.ZA);

        assertNull(selected);
    }

    @Test
    void selectProvider_respectsMinAmountThreshold() {
        // Stripe's min is 10000, so 5000 should not match Stripe
        PaymentProvider selected = orchestrator.selectProvider("WALLET", 5000, Region.ZA);

        assertNull(selected); // Stripe is the only WALLET provider but min is 10000
    }

    @Test
    void registerProvider_addsProviderToList() {
        PaymentOrchestrator newOrchestrator = new PaymentOrchestrator();
        assertEquals(0, newOrchestrator.getProviders().size());

        newOrchestrator.registerProvider(payFast);
        assertEquals(1, newOrchestrator.getProviders().size());
    }

    @Test
    void routePayment_processesSuccessfully() {
        // This test verifies routePayment doesn't throw for valid input
        assertDoesNotThrow(() ->
                orchestrator.routePayment(new CardPayment(), 5000, Region.ZA)
        );
    }

    @Test
    void routePayment_handlesNoProviderGracefully() {
        // This test verifies routePayment doesn't throw when no provider is found
        assertDoesNotThrow(() ->
                orchestrator.routePayment(new EftPayment(), 1000, Region.APAC)
        );
    }
}
