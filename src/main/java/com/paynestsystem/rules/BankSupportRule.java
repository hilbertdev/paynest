package com.paynestsystem.rules;

import com.paynestsystem.domain.Transaction;

/**
 * Example rule based on bank / rail support (Capstone 3 — students maintain allow-lists).
 */
public class BankSupportRule extends AbstractRoutingRule {

    public BankSupportRule(int priority) {
        super(priority);
    }

    @Override
    public boolean matches(Transaction transaction) {
        // TODO: Check transaction.getBank() against supported banks per provider
        return false;
    }
}
