package com.paynestsystem.orchestration;

import java.util.List;

/**
 * Represents a payment provider that can process payments for PayNest.
 * Each provider has characteristics that influence routing decisions:
 * availability, latency, supported regions, fee structure, and amount thresholds.
 */
public class PaymentProvider {

    private final String name;
    private final List<String> supportedPaymentTypes;
    private final List<Region> supportedRegions;
    private final boolean available;
    private final int latencyMs;
    private final double feePercentage;
    private final double flatFee;
    private final double minAmount;
    private final double maxAmount;

    /**
     * Creates a new payment provider.
     *
     * @param name                  provider name (e.g. "PayFast", "Stripe")
     * @param supportedPaymentTypes payment types this provider handles (e.g. "CARD", "EFT")
     * @param supportedRegions      regions where this provider operates
     * @param available             whether the provider is currently available
     * @param latencyMs             average response time in milliseconds
     * @param feePercentage         percentage fee charged (e.g. 0.029 for 2.9%)
     * @param flatFee               flat fee per transaction (e.g. R5.00)
     * @param minAmount             minimum payment amount this provider accepts
     * @param maxAmount             maximum payment amount this provider accepts
     */
    public PaymentProvider(String name, List<String> supportedPaymentTypes,
                           List<Region> supportedRegions, boolean available,
                           int latencyMs, double feePercentage, double flatFee,
                           double minAmount, double maxAmount) {
        this.name = name;
        this.supportedPaymentTypes = supportedPaymentTypes;
        this.supportedRegions = supportedRegions;
        this.available = available;
        this.latencyMs = latencyMs;
        this.feePercentage = feePercentage;
        this.flatFee = flatFee;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    /**
     * Checks if this provider supports the given payment type.
     *
     * @param paymentType the payment type to check (e.g. "CARD")
     * @return true if the provider supports this type
     */
    public boolean supportsPaymentType(String paymentType) {
        return supportedPaymentTypes.contains(paymentType);
    }

    /**
     * Checks if this provider operates in the given region.
     *
     * @param region the region to check
     * @return true if the provider supports this region
     */
    public boolean supportsRegion(Region region) {
        return supportedRegions.contains(region);
    }

    public String getName() {
        return name;
    }

    public List<String> getSupportedPaymentTypes() {
        return supportedPaymentTypes;
    }

    public List<Region> getSupportedRegions() {
        return supportedRegions;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getLatencyMs() {
        return latencyMs;
    }

    public double getFeePercentage() {
        return feePercentage;
    }

    public double getFlatFee() {
        return flatFee;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }
}
