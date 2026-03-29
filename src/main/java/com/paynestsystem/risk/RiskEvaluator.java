package com.paynestsystem.risk;

import com.paynestsystem.domain.Transaction;

/**
 * Evaluates risk for a transaction (Capstone 3 — students implement scoring).
 */
public interface RiskEvaluator {

    /**
     * @param transaction the transaction to assess
     * @return assessed risk level
     */
    RiskLevel evaluate(Transaction transaction);
}
