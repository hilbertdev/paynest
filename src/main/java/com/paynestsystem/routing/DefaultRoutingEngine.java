package com.paynestsystem.routing;

import com.paynestsystem.domain.Transaction;
import com.paynestsystem.providers.PaymentProvider;
import com.paynestsystem.rules.RoutingRule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.paynestsystem.common.ValidationUtils.requireNonNull;

/**
 * Skeleton routing engine (Capstone 3 — students implement policy).
 */
public class DefaultRoutingEngine implements RoutingEngine {

    private final List<RoutingRule> rules;
    private final List<PaymentProvider> providers;

    public DefaultRoutingEngine(List<RoutingRule> rules, List<PaymentProvider> providers) {
        this.rules = new ArrayList<>(requireNonNull(rules, "rules"));
        this.rules.sort(Comparator.comparingInt(RoutingRule::priority));
        this.providers = new ArrayList<>(requireNonNull(providers, "providers"));
    }

    @Override
    public RouteDecision route(Transaction transaction) {
        requireNonNull(transaction, "transaction");

        List<String> appliedRules = new ArrayList<>();

        // TODO: Apply rules in priority order (sort by RoutingRule.priority())
        for (RoutingRule rule : rules) {
            if (rule.matches(transaction)) {
                appliedRules.add(rule.getClass().getSimpleName());
            }
        }

        // TODO: Select provider based on matching rules
        PaymentProvider placeholder = selectFirstAvailableProvider();

        // TODO: Implement fallback logic when no provider is suitable
        return new RouteDecision(
                placeholder,
                "TODO: routing not implemented",
                appliedRules,
                false);
    }

    private PaymentProvider selectFirstAvailableProvider() {
        for (PaymentProvider provider : providers) {
            if (provider.isAvailable()) {
                return provider;
            }
        }
        return null;
    }
}
