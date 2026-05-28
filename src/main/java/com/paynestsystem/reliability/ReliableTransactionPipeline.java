package com.paynestsystem.reliability;

import com.paynestsystem.domain.Transaction;
import com.paynestsystem.domain.TransactionRecord;
import com.paynestsystem.domain.TransactionStatus;
import com.paynestsystem.persistence.IdempotencyRegistry;
import com.paynestsystem.persistence.TransactionRecordStore;
import com.paynestsystem.routing.DecisionLogger;
import com.paynestsystem.routing.RouteDecision;
import com.paynestsystem.routing.RoutingEngine;
import com.paynestsystem.risk.RiskEvaluator;
import com.paynestsystem.risk.RiskLevel;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Orchestrates idempotency, routing, risk, and persistence (Capstone 4 skeleton — students complete retries,
 * failure taxonomy, and durable stores).
 */
public class ReliableTransactionPipeline {

    private final IdempotencyRegistry idempotencyRegistry;
    private final TransactionRecordStore transactionRecordStore;
    private final RoutingEngine routingEngine;
    private final RiskEvaluator riskEvaluator;
    private final DecisionLogger decisionLogger;
    private final ConcurrentMap<String, Object> idempotencyLocks = new ConcurrentHashMap<>();

    public ReliableTransactionPipeline(
            IdempotencyRegistry idempotencyRegistry,
            TransactionRecordStore transactionRecordStore,
            RoutingEngine routingEngine,
            RiskEvaluator riskEvaluator,
            DecisionLogger decisionLogger) {
        this.idempotencyRegistry = Objects.requireNonNull(idempotencyRegistry);
        this.transactionRecordStore = Objects.requireNonNull(transactionRecordStore);
        this.routingEngine = Objects.requireNonNull(routingEngine);
        this.riskEvaluator = Objects.requireNonNull(riskEvaluator);
        this.decisionLogger = Objects.requireNonNull(decisionLogger);
    }

    /**
     * Processes a transaction once per idempotency key; repeats return the stored record (at-least-once delivery
     * simplified to duplicate detection).
     */
    public PipelineResult process(Transaction transaction, String idempotencyKey) {
        Objects.requireNonNull(transaction);
        Objects.requireNonNull(idempotencyKey);

        Object lock = idempotencyLocks.computeIfAbsent(idempotencyKey, key -> new Object());
        synchronized (lock) {
            try {
                return processLocked(transaction, idempotencyKey);
            } finally {
                idempotencyLocks.remove(idempotencyKey, lock);
            }
        }
    }

    private PipelineResult processLocked(Transaction transaction, String idempotencyKey) {
        Optional<String> existingId = idempotencyRegistry.lookup(idempotencyKey);
        if (existingId.isPresent()) {
            Optional<TransactionRecord> prior = transactionRecordStore.findById(existingId.get());
            if (prior.isPresent()) {
                TransactionRecord record = prior.get();
                if (record.getStatus() == TransactionStatus.PENDING) {
                    throw new IllegalStateException("Idempotency key is bound to an incomplete transaction record");
                }
                return new PipelineResult(record, true);
            }
            throw new IllegalStateException("Idempotency key is bound to a missing transaction record");
        }

        Instant now = Instant.now();
        String id = UUID.randomUUID().toString();
        TransactionRecord record = new TransactionRecord(
                id,
                idempotencyKey,
                transaction,
                TransactionStatus.PENDING,
                now,
                now);
        transactionRecordStore.save(record);
        idempotencyRegistry.bind(idempotencyKey, id);

        try {
            // TODO: transactional boundary between store + registry for production systems
            RouteDecision decision = routingEngine.route(transaction);
            decisionLogger.log(decision);

            RiskLevel risk = riskEvaluator.evaluate(transaction);
            record.setAssessedRisk(risk);
            record.setRoutingSummary(decision.getReason());

            // TODO: invoke PaymentProvider.process when routing selects a provider; map failures to FAILED + retry policy
            record.setStatus(TransactionStatus.ROUTED);
            transactionRecordStore.save(record);
        } catch (RuntimeException ex) {
            record.setStatus(TransactionStatus.FAILED);
            transactionRecordStore.save(record);
            throw ex;
        }

        return new PipelineResult(record, false);
    }
}
