package com.paynestsystem.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Placeholder for routing configuration (Capstone 3 — load from env/file later).
 */
public class RoutingConfig {

    private List<String> providerPriority;
    private Map<String, Double> thresholds;

    public RoutingConfig() {
        this.thresholds = new HashMap<>();
    }

    public List<String> getProviderPriority() {
        return providerPriority;
    }

    public void setProviderPriority(List<String> providerPriority) {
        this.providerPriority = providerPriority;
    }

    public Map<String, Double> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Map<String, Double> thresholds) {
        this.thresholds = thresholds != null ? new HashMap<>(thresholds) : new HashMap<>();
    }
}
