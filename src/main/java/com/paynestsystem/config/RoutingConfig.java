package com.paynestsystem.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.paynestsystem.common.ValidationUtils.requireNonNull;

/**
 * Placeholder for routing configuration (Capstone 3 — load from env/file later).
 */
public class RoutingConfig {

    private List<String> providerPriority = List.of();
    private Map<String, Double> thresholds;

    public RoutingConfig() {
        this.thresholds = new HashMap<>();
    }

    public List<String> getProviderPriority() {
        return providerPriority;
    }

    public void setProviderPriority(List<String> providerPriority) {
        this.providerPriority = List.copyOf(requireNonNull(providerPriority, "providerPriority"));
    }

    public Map<String, Double> getThresholds() {
        return Map.copyOf(thresholds);
    }

    public void setThresholds(Map<String, Double> thresholds) {
        this.thresholds = thresholds != null ? new LinkedHashMap<>(thresholds) : new HashMap<>();
    }
}
