package com.paynestsystem.reliability;

import com.paynestsystem.domain.Transaction;
import com.paynestsystem.domain.TransactionRecord;
import com.paynestsystem.domain.TransactionStatus;
import com.paynestsystem.persistence.IdempotencyRegistry;
import com.paynestsystem.persistence.InMemoryIdempotencyRegistry;
import com.paynestsystem.persistence.InMemoryTransactionRecordStore;
import com.paynestsystem.providers.PaymentProvider;
import com.paynestsystem.providers.ProviderA;
import com.paynestsystem.providers.ProviderB;
import com.paynestsystem.routing.DecisionLogger;
import com.paynestsystem.routing.DefaultRoutingEngine;
import com.paynestsystem.routing.RouteDecision;
import com.paynestsystem.routing.RoutingEngine;
import com.paynestsystem.risk.BasicRiskEvaluator;
import com.paynestsystem.risk.RiskLevel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
    void concurrentDuplicateWaitsForFirstAttemptToBindAndFinish() throws Exception {
        Transaction tx = new Transaction(50.0, "DemoBank", Instant.now());
        BlockingFirstSaveStore store = new BlockingFirstSaveStore();
        CountingRoutingEngine routingEngine = new CountingRoutingEngine();
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                new InMemoryIdempotencyRegistry(),
                store,
                routingEngine,
                transaction -> RiskLevel.LOW,
                new DecisionLogger());
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<PipelineResult> first = executor.submit(() -> pipeline.process(tx, "idem-concurrent"));
            assertTrue(store.awaitFirstPendingSave());

            CountDownLatch secondStarted = new CountDownLatch(1);
            Future<PipelineResult> second = executor.submit(() -> {
                secondStarted.countDown();
                return pipeline.process(tx, "idem-concurrent");
            });
            assertTrue(secondStarted.await(1, TimeUnit.SECONDS));
            assertThrows(TimeoutException.class, () -> second.get(100, TimeUnit.MILLISECONDS));

            store.releaseFirstPendingSave();
            PipelineResult firstResult = first.get(1, TimeUnit.SECONDS);
            PipelineResult secondResult = second.get(1, TimeUnit.SECONDS);

            assertFalse(firstResult.isDuplicateRequest());
            assertTrue(secondResult.isDuplicateRequest());
            assertEquals(firstResult.getRecord().getId(), secondResult.getRecord().getId());
            assertEquals(TransactionStatus.ROUTED, secondResult.getRecord().getStatus());
            assertEquals(1, routingEngine.getRouteCalls());
            assertEquals(1, store.getPendingSaveCalls());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void process_failsClosedWhenIdempotencyRecordIsMissing() {
        Transaction tx = new Transaction(50.0, "DemoBank", Instant.now());
        IdempotencyRegistry registry = new InMemoryIdempotencyRegistry();
        registry.bind("idem-missing", "missing-record");
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                registry,
                new InMemoryTransactionRecordStore(),
                new DefaultRoutingEngine(List.of(), List.of(new ProviderA())),
                new BasicRiskEvaluator(),
                new DecisionLogger());

        assertThrows(IllegalStateException.class, () -> pipeline.process(tx, "idem-missing"));
    }

    @Test
    void process_failsClosedWhenExistingRecordIsPending() {
        Transaction tx = new Transaction(50.0, "DemoBank", Instant.now());
        IdempotencyRegistry registry = new InMemoryIdempotencyRegistry();
        InMemoryTransactionRecordStore store = new InMemoryTransactionRecordStore();
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
        Transaction tx = new Transaction(50.0, "DemoBank", Instant.now());
        IdempotencyRegistry registry = new InMemoryIdempotencyRegistry();
        InMemoryTransactionRecordStore store = new InMemoryTransactionRecordStore();
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

    private static final class BlockingFirstSaveStore extends InMemoryTransactionRecordStore {

        private final CountDownLatch firstPendingSaveStarted = new CountDownLatch(1);
        private final CountDownLatch releaseFirstPendingSave = new CountDownLatch(1);
        private final AtomicInteger pendingSaveCalls = new AtomicInteger();

        @Override
        public void save(TransactionRecord record) {
            if (record.getStatus() == TransactionStatus.PENDING
                    && pendingSaveCalls.incrementAndGet() == 1) {
                firstPendingSaveStarted.countDown();
                try {
                    assertTrue(releaseFirstPendingSave.await(1, TimeUnit.SECONDS));
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(exception);
                }
            }
            super.save(record);
        }

        boolean awaitFirstPendingSave() throws InterruptedException {
            return firstPendingSaveStarted.await(1, TimeUnit.SECONDS);
        }

        void releaseFirstPendingSave() {
            releaseFirstPendingSave.countDown();
        }

        int getPendingSaveCalls() {
            return pendingSaveCalls.get();
        }
    }

    private static final class CountingRoutingEngine implements RoutingEngine {

        private final AtomicInteger routeCalls = new AtomicInteger();

        @Override
        public RouteDecision route(Transaction transaction) {
            routeCalls.incrementAndGet();
            return new RouteDecision(new AlwaysAvailableProvider(), "counted route", List.of(), false);
        }

        int getRouteCalls() {
            return routeCalls.get();
        }
    }

    private static final class AlwaysAvailableProvider implements PaymentProvider {

        @Override
        public boolean process(Transaction transaction) {
            return true;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}
