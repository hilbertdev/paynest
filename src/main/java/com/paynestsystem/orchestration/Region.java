package com.paynestsystem.orchestration;

/**
 * Represents geographic regions supported by PayNest.
 * Used by the orchestration layer to route payments to region-appropriate providers.
 */
public enum Region {
    ZA("South Africa"),
    EU("Europe"),
    US("United States"),
    UK("United Kingdom"),
    APAC("Asia-Pacific");

    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
