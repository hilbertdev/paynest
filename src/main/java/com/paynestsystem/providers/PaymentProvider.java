package com.paynestsystem.providers;

import com.paynestsystem.domain.Transaction;

/**
 * External payment rail / PSP abstraction (Capstone 3 — students implement integration).
 */
public interface PaymentProvider {

    /**
     * Attempts to process the transaction through this provider.
     *
     * @param transaction the transaction to process
     * @return true if processing succeeded (placeholder contract)
     */
    boolean process(Transaction transaction);

    /**
     * @return true if this provider is currently available for routing
     */
    boolean isAvailable();
}
