package com.paynestsystem.orchestration;

import com.paynestsystem.payment.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates payment routing by selecting the best payment provider based on:
 * <ul>
 *   <li>Payment type support (CARD, EFT, WALLET)</li>
 *   <li>Region support (where the customer is paying from)</li>
 *   <li>Provider availability</li>
 *   <li>Amount thresholds (high-value payments route to premium providers)</li>
 *   <li>Provider latency (prefer the fastest available provider)</li>
 * </ul>
 * After selecting a provider, the orchestrator calculates the total charge including fees.
 */
public class PaymentOrchestrator {

    private final List<PaymentProvider> providers;
    private final FeeCalculator feeCalculator;

    /**
     * Creates a new PaymentOrchestrator with no registered providers.
     */
    public PaymentOrchestrator() {
        this.providers = new ArrayList<>();
        this.feeCalculator = new FeeCalculator();
    }

    /**
     * Registers a payment provider with the orchestrator.
     *
     * @param provider the provider to register
     */
    public void registerProvider(PaymentProvider provider) {
        providers.add(provider);
    }

    /**
     * Selects the best payment provider for the given payment type, amount, and region.
     * Filtering steps:
     * 1. Must support the payment type
     * 2. Must support the customer's region
     * 3. Must be available
     * 4. Amount must fall within the provider's min/max thresholds
     * 5. Among remaining candidates, the provider with the lowest latency is selected
     *
     * @param paymentType the type of payment (e.g. "CARD", "EFT", "WALLET")
     * @param amount      the payment amount
     * @param region      the customer's region
     * @return the best matching provider, or null if no provider matches
     */
    public PaymentProvider selectProvider(String paymentType, double amount, Region region) {
        PaymentProvider best = null;

        for (PaymentProvider provider : providers) {
            if (!provider.supportsPaymentType(paymentType)) {
                continue;
            }
            if (!provider.supportsRegion(region)) {
                continue;
            }
            if (!provider.isAvailable()) {
                continue;
            }
            if (amount < provider.getMinAmount() || amount > provider.getMaxAmount()) {
                continue;
            }
            if (best == null || provider.getLatencyMs() < best.getLatencyMs()) {
                best = provider;
            }
        }

        return best;
    }

    /**
     * Routes a payment to the best provider and processes it.
     * Prints details about the selected provider, fees, and total charge.
     *
     * @param paymentMethod the payment method to use
     * @param amount        the payment amount
     * @param region        the customer's region
     */
    public void routePayment(PaymentMethod paymentMethod, double amount, Region region) {
        String paymentType = paymentMethod.getPaymentType();

        System.out.println("Routing payment: R" + String.format("%.2f", amount)
                + " via " + paymentType + " from " + region.getDisplayName());

        PaymentProvider provider = selectProvider(paymentType, amount, region);

        if (provider == null) {
            System.out.println("No available provider for " + paymentType
                    + " in " + region.getDisplayName() + " for amount R"
                    + String.format("%.2f", amount));
            return;
        }

        System.out.println("Selected provider: " + provider.getName()
                + " (latency: " + provider.getLatencyMs() + "ms)");

        double fee = feeCalculator.calculateFee(provider, amount);
        double totalCharge = feeCalculator.calculateTotalCharge(provider, amount);

        System.out.println("Fee: R" + String.format("%.2f", fee)
                + " (" + String.format("%.1f", provider.getFeePercentage() * 100) + "% + R"
                + String.format("%.2f", provider.getFlatFee()) + " flat)");
        System.out.println("Total charge: R" + String.format("%.2f", totalCharge));

        boolean success = paymentMethod.processPayment(amount);
        if (success) {
            System.out.println("Payment processed successfully via " + provider.getName());
        } else {
            System.out.println("Payment failed via " + provider.getName());
        }
    }

    /**
     * Returns the list of registered providers.
     *
     * @return the list of payment providers
     */
    public List<PaymentProvider> getProviders() {
        return providers;
    }
}
