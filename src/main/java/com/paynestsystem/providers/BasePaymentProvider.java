package com.paynestsystem.providers;

import com.paynestsystem.domain.Transaction;

/**
 * Shared placeholder for payment providers (Capstone 3 — extend with real behavior).
 */
public abstract class BasePaymentProvider implements PaymentProvider {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public abstract boolean process(Transaction transaction);
}
