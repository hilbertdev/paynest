package com.paynestsystem.providers;

import com.paynestsystem.domain.Transaction;

/**
 * Placeholder provider B (Capstone 3 — replace with real API client, timeouts, retries).
 */
public class ProviderB extends BasePaymentProvider {

    @Override
    public boolean process(Transaction transaction) {
        // TODO: Integrate with provider B (auth, idempotency, error mapping)
        return false;
    }
}
