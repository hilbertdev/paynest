package com.paynestsystem.routing;

import com.paynestsystem.providers.PaymentProvider;

/**
 * Minimal audit hook for routing decisions (Capstone 3 — replace with structured logging).
 */
public class DecisionLogger {

    public void log(RouteDecision decision) {
        PaymentProvider selectedProvider = decision.getSelectedProvider();
        String providerName = selectedProvider != null
                ? selectedProvider.getClass().getSimpleName()
                : "none";

        System.out.println("[RouteDecision] reason=" + decision.getReason()
                + " provider=" + providerName
                + " rules=" + decision.getAppliedRules()
                + " fallback=" + decision.isFallbackUsed());
    }
}
