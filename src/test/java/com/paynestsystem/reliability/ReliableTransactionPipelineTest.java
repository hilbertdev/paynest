package com.paynestsystem.reliability;

import com.paynestsystem.domain.Transaction;
import com.paynestsystem.domain.TransactionRecord;
import com.paynestsystem.domain.TransactionStatus;
import com.paynestsystem.persistence.InMemoryIdempotencyRegistry;
import com.paynestsystem.persistence.InMemoryTransactionRecordStore;
import com.paynestsystem.persistence.TransactionRecordStore;
import com.paynestsystem.providers.ProviderA;
import com.paynestsystem.providers.ProviderB;
import com.paynestsystem.routing.DecisionLogger;
import com.paynestsystem.routing.DefaultRoutingEngine;
import com.paynestsystem.risk.BasicRiskEvaluator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void overlappingCallsWithSameKey_shareSingleRecord() throws Exception {
        Transaction tx = new Transaction(50.0, "DemoBank", Instant.now());
        DelayedFirstSaveStore store = new DelayedFirstSaveStore();
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                new InMemoryIdempotencyRegistry(),
                store,
                new DefaultRoutingEngine(List.of(), List.of(new ProviderA(), new ProviderB())),
                new BasicRiskEvaluator(),
                new DecisionLogger());
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<PipelineResult> first = executor.submit(() -> pipeline.process(tx, "idem-race"));
            assertTrue(store.awaitFirstSaveStarted());
            Future<PipelineResult> second = executor.submit(() -> pipeline.process(tx, "idem-race"));

            PipelineResult firstResult = first.get(2, TimeUnit.SECONDS);
            PipelineResult secondResult = second.get(2, TimeUnit.SECONDS);

            assertFalse(firstResult.isDuplicateRequest());
            assertTrue(secondResult.isDuplicateRequest());
            assertEquals(firstResult.getRecord().getId(), secondResult.getRecord().getId());
            assertEquals(1, store.uniqueRecordCount());
        } finally {
            executor.shutdownNow();
        }
    }

    private static class DelayedFirstSaveStore implements TransactionRecordStore {

        private final Map<String, TransactionRecord> records = new ConcurrentHashMap<>();
        private final CountDownLatch firstSaveStarted = new CountDownLatch(1);
        private final AtomicBoolean delayedFirstSave = new AtomicBoolean(false);

        @Override
        public void save(TransactionRecord record) {
            records.put(record.getId(), record);
            if (delayedFirstSave.compareAndSet(false, true)) {
                firstSaveStarted.countDown();
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        @Override
        public Optional<TransactionRecord> findById(String id) {
            return Optional.ofNullable(records.get(id));
        }

        boolean awaitFirstSaveStarted() throws InterruptedException {
            return firstSaveStarted.await(1, TimeUnit.SECONDS);
        }

        int uniqueRecordCount() {
            return records.size();
        }
    }
}
