package com.paynestsystem.payment;

/**
 * Capability group: rails that go through the banking system and settle later.
 * EFT is the example here: it takes 1-2 business days and comes back
 * with a bank reference the customer can quote.
 */
public interface BankSettlement {

    /**
     * When the money lands.
     *
     * @return a human-readable settlement time
     */
    default String settlementTime() {
        return "1-2 business days";
    }

    /**
     * The bank's reference for this payment.
     *
     * @return the bank reference
     */
    String getBankReference();
}
