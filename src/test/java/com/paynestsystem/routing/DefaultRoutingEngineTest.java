package com.paynestsystem.routing;

import com.paynestsystem.domain.Transaction;
import com.paynestsystem.providers.PaymentProvider;
import com.paynestsystem.rules.RoutingRule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultRoutingEngineTest {

    @Test
    void route_sortsRulesByPriorityAndChoosesFirstAvailableProvider() {
        TrackingRule lowPriorityRule = new TrackingRule("LowPriorityRule", 20, true);
        TrackingRule highPriorityRule = new TrackingRule("HighPriorityRule", 5, true);
        PaymentProvider unavailableProvider = new TestProvider(false);
        PaymentProvider availableProvider = new TestProvider(true);

        DefaultRoutingEngine engine = new DefaultRoutingEngine(
                List.of(lowPriorityRule, highPriorityRule),
                List.of(unavailableProvider, availableProvider));

        RouteDecision decision = engine.route(new Transaction(150.0, "ExampleBank", Instant.now()));

        assertEquals(List.of("HighPriorityRule", "LowPriorityRule"), TrackingRule.invocationOrder);
        assertEquals(List.of("TrackingRule", "TrackingRule"), decision.getAppliedRules());
        assertSame(availableProvider, decision.getSelectedProvider());
    }

    private static final class TrackingRule implements RoutingRule {

        private static final List<String> invocationOrder = new java.util.ArrayList<>();

        private final String name;
        private final int priority;
        private final boolean matches;

        private TrackingRule(String name, int priority, boolean matches) {
            invocationOrder.clear();
            this.name = name;
            this.priority = priority;
            this.matches = matches;
        }

        @Override
        public boolean matches(Transaction transaction) {
            invocationOrder.add(name);
            return matches;
        }

        @Override
        public int priority() {
            return priority;
        }
    }

    private static final class TestProvider implements PaymentProvider {

        private final boolean available;

        private TestProvider(boolean available) {
            this.available = available;
        }

        @Override
        public boolean process(Transaction transaction) {
            return false;
        }

        @Override
        public boolean isAvailable() {
            return available;
        }
    }
}
