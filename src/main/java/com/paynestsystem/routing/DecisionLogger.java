package com.paynestsystem.routing;

/**
 * Minimal audit hook for routing decisions (Capstone 3 — replace with structured logging).
 */
public class DecisionLogger {

    public void log(RouteDecision decision) {
        System.out.println("[RouteDecision] reason=" + decision.getReason()
                + " fallback=" + decision.isFallbackUsed());
    }
}
