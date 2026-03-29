package com.paynestsystem.rules;

import com.paynestsystem.domain.Transaction;

/**
 * Example rule keyed on amount (Capstone 3 — students define thresholds and behavior).
 */
public class AmountRoutingRule extends AbstractRoutingRule {

    public AmountRoutingRule(int priority) {
        super(priority);
    }

    @Override
    public boolean matches(Transaction transaction) {
        // TODO: Compare transaction.getAmount() against configured thresholds
        return false;
    }
}
