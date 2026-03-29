package com.paynestsystem.routing;

import com.paynestsystem.domain.Transaction;

/**
 * Chooses a payment provider for a transaction using configured rules (Capstone 3).
 */
public interface RoutingEngine {

    /**
     * @param transaction the transaction to route
     * @return the routing decision (provider, audit trail, fallback flag)
     */
    RouteDecision route(Transaction transaction);
}
