package com.paynestsystem.payment;

/**
 * Interface for payment methods supported by PayNest.
 * Implementations process payments and report their type.
 */
public interface PaymentMethod {

    /**
     * Processes a payment for the given amount.
     *
     * @param amount the amount to charge
     * @return true if payment succeeded, false otherwise
     */
    boolean processPayment(double amount);

    /**
     * Returns the type of this payment method (e.g. "CARD", "EFT", "WALLET").
     *
     * @return the payment type name
     */
    PaymentType getPaymentType();
}
