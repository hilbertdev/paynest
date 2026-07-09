package com.paynestsystem.orchestration;

/**
 * Calculates the fees and total charge for a payment processed by a given provider.
 * Each provider has a percentage fee and a flat fee that are applied to the payment amount.
 */
public class FeeCalculator {

    /**
     * Calculates the fee for a payment amount using the given provider's fee structure.
     * Fee = (amount * feePercentage) + flatFee
     *
     * @param provider the payment provider whose fee structure to use
     * @param amount   the base payment amount
     * @return the total fee charged by the provider
     */
    public double calculateFee(PaymentProvider provider, double amount) {
        return (amount * provider.getFeePercentage()) + provider.getFlatFee();
    }

    /**
     * Calculates the total amount the customer will be charged,
     * including the base amount and the provider's fee.
     *
     * @param provider the payment provider whose fee structure to use
     * @param amount   the base payment amount
     * @return the total charge (amount + fee)
     */
    public double calculateTotalCharge(PaymentProvider provider, double amount) {
        return amount + calculateFee(provider, amount);
    }
}
