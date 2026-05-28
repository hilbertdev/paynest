package com.paynestsystem.reliability;

import com.paynestsystem.domain.Transaction;
import com.paynestsystem.domain.TransactionRecord;
import com.paynestsystem.domain.TransactionStatus;
import com.paynestsystem.persistence.InMemoryIdempotencyRegistry;
import com.paynestsystem.persistence.InMemoryTransactionRecordStore;
import com.paynestsystem.providers.ProviderA;
import com.paynestsystem.providers.ProviderB;
import com.paynestsystem.routing.DecisionLogger;
import com.paynestsystem.routing.DefaultRoutingEngine;
import com.paynestsystem.routing.RouteDecision;
import com.paynestsystem.routing.RoutingEngine;
import com.paynestsystem.risk.BasicRiskEvaluator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReliableTransactionPipelineTest {

    @Test
    void secondCallWithSameKey_isDuplicate() {
        Transaction tx = new Transaction(50.0, "DemoBank", Instant.now());
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                new InMemoryIdempotencyRegistry(),
                new InMemoryTransactionRecordStore(),
                new DefaultRoutingEngine(List.of(), List.of(new ProviderA(), new ProviderB())),
                new BasicRiskEvaluator(),
                new DecisionLogger());

        PipelineResult first = pipeline.process(tx, "idem-1");
        PipelineResult second = pipeline.process(tx, "idem-1");

        assertTrue(second.isDuplicateRequest());
        assertEquals(first.getRecord().getId(), second.getRecord().getId());
        assertEquals(TransactionStatus.ROUTED, second.getRecord().getStatus());
    }

    @Test
    void firstCall_processesAndStoresRecord() {
        Transaction tx = new Transaction(10.0, "DemoBank", Instant.now());
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                new InMemoryIdempotencyRegistry(),
                new InMemoryTransactionRecordStore(),
                new DefaultRoutingEngine(List.of(), List.of(new ProviderA())),
                new BasicRiskEvaluator(),
                new DecisionLogger());

        PipelineResult result = pipeline.process(tx, "idem-new");

        assertFalse(result.isDuplicateRequest());
        assertEquals(TransactionStatus.ROUTED, result.getRecord().getStatus());
    }

    @Test
    void concurrentCallsWithSameKey_processOnlyOnce() throws Exception {
        Transaction tx = new Transaction(75.0, "DemoBank", Instant.now());
        AtomicInteger routeCalls = new AtomicInteger();
        RoutingEngine routingEngine = transaction -> {
            routeCalls.incrementAndGet();
            return new RouteDecision(new ProviderA(), "counted route", List.of(), false);
        };
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                new InMemoryIdempotencyRegistry(),
                new InMemoryTransactionRecordStore(),
                routingEngine,
                new BasicRiskEvaluator(),
                new DecisionLogger());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<PipelineResult> task = () -> {
            ready.countDown();
            assertTrue(start.await(5, TimeUnit.SECONDS));
            return pipeline.process(tx, "idem-race");
        };

        try {
            Future<PipelineResult> firstFuture = executor.submit(task);
            Future<PipelineResult> secondFuture = executor.submit(task);

            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            PipelineResult first = firstFuture.get(5, TimeUnit.SECONDS);
            PipelineResult second = secondFuture.get(5, TimeUnit.SECONDS);

            assertEquals(1, routeCalls.get());
            assertEquals(first.getRecord().getId(), second.getRecord().getId());
            assertTrue(first.isDuplicateRequest() || second.isDuplicateRequest());
            assertFalse(first.isDuplicateRequest() && second.isDuplicateRequest());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void process_failsClosedWhenIdempotencyRecordIsMissing() {
        InMemoryIdempotencyRegistry registry = new InMemoryIdempotencyRegistry();
        registry.bind("idem-missing", "missing-record");
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                registry,
                new InMemoryTransactionRecordStore(),
                new DefaultRoutingEngine(List.of(), List.of(new ProviderA())),
                new BasicRiskEvaluator(),
                new DecisionLogger());

        assertThrows(IllegalStateException.class,
                () -> pipeline.process(new Transaction(10.0, "DemoBank", Instant.now()), "idem-missing"));
    }

    @Test
    void process_failsClosedWhenExistingRecordIsPending() {
        InMemoryIdempotencyRegistry registry = new InMemoryIdempotencyRegistry();
        InMemoryTransactionRecordStore store = new InMemoryTransactionRecordStore();
        Transaction tx = new Transaction(10.0, "DemoBank", Instant.now());
        TransactionRecord record = new TransactionRecord(
                "rec-pending",
                "idem-pending",
                tx,
                TransactionStatus.PENDING,
                Instant.now(),
                Instant.now());
        store.save(record);
        registry.bind("idem-pending", record.getId());
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                registry,
                store,
                new DefaultRoutingEngine(List.of(), List.of(new ProviderA())),
                new BasicRiskEvaluator(),
                new DecisionLogger());

        assertThrows(IllegalStateException.class, () -> pipeline.process(tx, "idem-pending"));
    }

    @Test
    void process_persistsFailedRecordWhenProcessingThrows() {
        InMemoryIdempotencyRegistry registry = new InMemoryIdempotencyRegistry();
        InMemoryTransactionRecordStore store = new InMemoryTransactionRecordStore();
        Transaction tx = new Transaction(10.0, "DemoBank", Instant.now());
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                registry,
                store,
                new DefaultRoutingEngine(List.of(), List.of(new ProviderA())),
                transaction -> {
                    throw new IllegalStateException("risk unavailable");
                },
                new DecisionLogger());

        assertThrows(IllegalStateException.class, () -> pipeline.process(tx, "idem-fail"));
        String recordId = registry.lookup("idem-fail").orElseThrow();
        assertEquals(TransactionStatus.FAILED, store.findById(recordId).orElseThrow().getStatus());

        PipelineResult retry = pipeline.process(tx, "idem-fail");

        assertTrue(retry.isDuplicateRequest());
        assertEquals(recordId, retry.getRecord().getId());
        assertEquals(TransactionStatus.FAILED, retry.getRecord().getStatus());
    }
}
