package com.paynestsystem.payment;

/**
 * Capability group: rails that can send money back to the customer.
 * Card and wallet support refunds. Students can decide: should EFT be refundable?
 */
public interface Refundable {

    /**
     * Refunds the given amount to the customer.
     *
     * @param amount the amount to refund
     * @return true if the refund succeeded
     */
    boolean refund(double amount);
}
