package com.paynestsystem.payment;

/**
 * Parent class for all payment methods in PayNest.
 *
 * The abstract class holds the behaviour every rail shares
 * (amount validation and the fixed processing steps), written once here.
 * Each rail subclasses it and fills in only what is different.
 *
 * Interfaces group the rails by what they can DO - see
 * {@link InstantSettlement}, {@link BankSettlement} and {@link Refundable}.
 * A rail extends exactly one parent (this class) but can join as many
 * capability groups as apply. That is the correct way to implement
 * polymorphism: inheritance for shared behaviour, interfaces for
 * grouping by functionality.
 */
public abstract class PaymentMethod {

    /**
     * Template method: the steps of a payment, fixed for every rail.
     * 1. Validate the amount (shared rule, written once).
     * 2. Let the rail do its own processing.
     * Marked final so no rail can skip the shared validation step.
     *
     * @param amount the amount to charge
     * @return true if payment succeeded, false otherwise
     */
    public final boolean processPayment(double amount) {
        if (!isValidAmount(amount)) {
            System.out.println("Rejected: amount must be greater than R0");
            return false;
        }
        return doProcess(amount);
    }

    /**
     * Shared validation, written once in the parent.
     * Students can extend here: override to add per-rail rules.
     */
    protected boolean isValidAmount(double amount) {
        return amount > 0;
    }

    /**
     * Each rail implements its own processing here.
     * Called only after the shared validation has passed.
     */
    protected abstract boolean doProcess(double amount);

    /**
     * The rail's display label, e.g. "CARD".
     *
     * @return the payment type label
     */
    public abstract String getPaymentType();
}
