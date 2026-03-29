package com.paynestsystem.risk;

import com.paynestsystem.domain.Transaction;

/**
 * Minimal risk evaluator (Capstone 3 — students add rules and scoring).
 */
public class BasicRiskEvaluator implements RiskEvaluator {

    @Override
    public RiskLevel evaluate(Transaction transaction) {
        // TODO: Evaluate based on transaction amount
        // TODO: Consider transaction frequency (requires history / session — design it)
        return RiskLevel.LOW;
    }
}
