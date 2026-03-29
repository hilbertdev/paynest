package com.paynestsystem.risk;

import com.paynestsystem.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasicRiskEvaluatorTest {

    @Test
    void evaluate_returnsLow_byDefault() {
        RiskEvaluator evaluator = new BasicRiskEvaluator();
        Transaction tx = new Transaction(100.0, "ExampleBank", Instant.now());

        assertEquals(RiskLevel.LOW, evaluator.evaluate(tx));
    }
}
