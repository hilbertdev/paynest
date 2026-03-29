package com.paynestsystem.rules;

import com.paynestsystem.domain.Transaction;

/**
 * Fallback when no primary rule matches (Capstone 3 — students define when this applies).
 */
public class FallbackRule extends AbstractRoutingRule {

    public FallbackRule(int priority) {
        super(priority);
    }

    @Override
    public boolean matches(Transaction transaction) {
        // TODO: Return true when fallback routing should apply
        return false;
    }
}
