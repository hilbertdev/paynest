package com.paynestsystem.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoutingConfigTest {

    @Test
    void setters_copyInputCollectionsDefensively() {
        RoutingConfig config = new RoutingConfig();
        List<String> priorities = new ArrayList<>(List.of("ProviderA", "ProviderB"));
        Map<String, Double> thresholds = new HashMap<>(Map.of("high", 5000.0));

        config.setProviderPriority(priorities);
        config.setThresholds(thresholds);

        priorities.add("ProviderC");
        thresholds.put("medium", 1000.0);

        assertEquals(List.of("ProviderA", "ProviderB"), config.getProviderPriority());
        assertEquals(Map.of("high", 5000.0), config.getThresholds());
    }

    @Test
    void getters_returnReadOnlyCollections() {
        RoutingConfig config = new RoutingConfig();
        config.setProviderPriority(List.of("ProviderA"));
        config.setThresholds(Map.of("high", 5000.0));

        assertThrows(UnsupportedOperationException.class, () -> config.getProviderPriority().add("ProviderB"));
        assertThrows(UnsupportedOperationException.class, () -> config.getThresholds().put("low", 100.0));
    }
}
