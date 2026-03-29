package com.paynestsystem.domain;

import java.time.Instant;

/**
 * Represents a payment transaction for routing and risk evaluation (Capstone 3).
 */
public class Transaction {

    private final double amount;
    private final String bank;
    private final Instant timestamp;

    public Transaction(double amount, String bank, Instant timestamp) {
        this.amount = amount;
        this.bank = bank;
        this.timestamp = timestamp;
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
