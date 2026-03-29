package com.paynestsystem.providers;

import com.paynestsystem.domain.Transaction;

/**
 * Placeholder provider A (Capstone 3 — replace with real API client, timeouts, retries).
 */
public class ProviderA extends BasePaymentProvider {

    @Override
    public boolean process(Transaction transaction) {
        // TODO: Integrate with provider A (auth, idempotency, error mapping)
        return false;
    }
}
