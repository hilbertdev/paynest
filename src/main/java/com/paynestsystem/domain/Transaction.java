package com.paynestsystem.domain;

import java.time.Instant;

import static com.paynestsystem.common.ValidationUtils.requireNonBlank;
import static com.paynestsystem.common.ValidationUtils.requireNonNegative;
import static com.paynestsystem.common.ValidationUtils.requireNonNull;

/**
 * Represents a payment transaction for routing and risk evaluation (Capstone 3).
 */
public class Transaction {

    private final double amount;
    private final String bank;
    private final Instant timestamp;

    public Transaction(double amount, String bank, Instant timestamp) {
        this.amount = requireNonNegative(amount, "amount");
        this.bank = requireNonBlank(bank, "bank");
        this.timestamp = requireNonNull(timestamp, "timestamp");
    }

    public double getAmount() {
        return amount;
    }

    public String getBank() {
        return bank;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
