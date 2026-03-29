package com.paynestsystem.rules;

import com.paynestsystem.domain.Transaction;

/**
 * A rule that may influence which payment provider is selected (Capstone 3).
 */
public interface RoutingRule {

    /**
     * @param transaction the transaction being routed
     * @return true if this rule applies to the transaction
     */
    boolean matches(Transaction transaction);

    /**
     * Lower numbers typically mean higher priority (students define policy).
     *
     * @return priority value for ordering rules
     */
    int priority();
}
