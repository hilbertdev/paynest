package com.paynestsystem.rules;

/**
 * Optional base for routing rules — shared priority wiring only (Capstone 3 skeleton).
 */
public abstract class AbstractRoutingRule implements RoutingRule {

    protected final int priority;

    protected AbstractRoutingRule(int priority) {
        this.priority = priority;
    }

    @Override
    public int priority() {
        return priority;
    }
}
