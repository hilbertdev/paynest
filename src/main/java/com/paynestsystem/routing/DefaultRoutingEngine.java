package com.paynestsystem.routing;

import com.paynestsystem.domain.Transaction;
import com.paynestsystem.providers.PaymentProvider;
import com.paynestsystem.rules.RoutingRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeleton routing engine (Capstone 3 — students implement policy).
 */
public class DefaultRoutingEngine implements RoutingEngine {

    private final List<RoutingRule> rules;
    private final List<PaymentProvider> providers;

    public DefaultRoutingEngine(List<RoutingRule> rules, List<PaymentProvider> providers) {
        this.rules = new ArrayList<>(rules);
        this.providers = new ArrayList<>(providers);
    }

    @Override
    public RouteDecision route(Transaction transaction) {
        // TODO: Apply rules in priority order (sort by RoutingRule.priority())
        for (RoutingRule rule : rules) {
            rule.matches(transaction);
        }

        // TODO: Select provider based on matching rules
        PaymentProvider placeholder = null;
        for (PaymentProvider provider : providers) {
            placeholder = provider;
            break;
        }

        // TODO: Implement fallback logic when no provider is suitable
        return new RouteDecision(
                placeholder,
                "TODO: routing not implemented",
                List.of(),
                false);
    }
}
