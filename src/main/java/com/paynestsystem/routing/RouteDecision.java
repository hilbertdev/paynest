package com.paynestsystem.routing;

import com.paynestsystem.providers.PaymentProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of a routing decision: which provider was chosen and why (Capstone 3 skeleton).
 */
public class RouteDecision {

    private final PaymentProvider selectedProvider;
    private final String reason;
    private final List<String> appliedRules;
    private final boolean fallbackUsed;

    public RouteDecision(
            PaymentProvider selectedProvider,
            String reason,
            List<String> appliedRules,
            boolean fallbackUsed) {
        this.selectedProvider = selectedProvider;
        this.reason = reason;
        this.appliedRules = appliedRules != null
                ? new ArrayList<>(appliedRules)
                : new ArrayList<>();
        this.fallbackUsed = fallbackUsed;
    }

    public PaymentProvider getSelectedProvider() {
        return selectedProvider;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getAppliedRules() {
        return Collections.unmodifiableList(appliedRules);
    }

    public boolean isFallbackUsed() {
        return fallbackUsed;
    }
}
