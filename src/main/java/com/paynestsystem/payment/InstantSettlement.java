package com.paynestsystem.payment;

/**
 * Capability group: rails that settle immediately.
 * Card and wallet payments land right away; EFT does not.
 * Grouping by interface lets code ask "does this rail settle instantly?"
 * without caring which rail it is:
 * {@code if (method instanceof InstantSettlement) ...}
 */
public interface InstantSettlement {

    /**
     * When the money lands.
     *
     * @return a human-readable settlement time
     */
    default String settlementTime() {
        return "immediate";
    }
}
