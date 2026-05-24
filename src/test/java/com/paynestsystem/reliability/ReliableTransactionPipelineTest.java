package com.paynestsystem.reliability;

import com.paynestsystem.domain.Transaction;
import com.paynestsystem.domain.TransactionStatus;
import com.paynestsystem.persistence.InMemoryIdempotencyRegistry;
import com.paynestsystem.persistence.InMemoryTransactionRecordStore;
import com.paynestsystem.providers.ProviderA;
import com.paynestsystem.providers.ProviderB;
import com.paynestsystem.routing.DecisionLogger;
import com.paynestsystem.routing.DefaultRoutingEngine;
import com.paynestsystem.risk.BasicRiskEvaluator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
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
    void concurrentCallsWithSameKey_returnSameRecord() throws Exception {
        Transaction tx = new Transaction(75.0, "DemoBank", Instant.now());
        AtomicInteger routedCount = new AtomicInteger();
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                new CoordinatedLookupRegistry(),
                new InMemoryTransactionRecordStore(),
                transaction -> {
                    routedCount.incrementAndGet();
                    return new com.paynestsystem.routing.RouteDecision(
                            new ProviderA(),
                            "test route",
                            List.of(),
                            false);
                },
                new BasicRiskEvaluator(),
                new DecisionLogger());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        Callable<PipelineResult> work = () -> {
            start.await();
            return pipeline.process(tx, "idem-race");
        };

        Future<PipelineResult> firstFuture = executor.submit(work);
        Future<PipelineResult> secondFuture = executor.submit(work);
        start.countDown();

        PipelineResult first = get(firstFuture);
        PipelineResult second = get(secondFuture);
        executor.shutdownNow();

        assertEquals(first.getRecord().getId(), second.getRecord().getId());
        assertEquals(1, routedCount.get());
        assertTrue(first.isDuplicateRequest() || second.isDuplicateRequest());
    }

    @Test
    void registryStoreMismatch_failsClosed() {
        Transaction tx = new Transaction(25.0, "DemoBank", Instant.now());
        InMemoryIdempotencyRegistry registry = new InMemoryIdempotencyRegistry();
        registry.bind("idem-missing", "missing-record");
        ReliableTransactionPipeline pipeline = new ReliableTransactionPipeline(
                registry,
                new InMemoryTransactionRecordStore(),
                new DefaultRoutingEngine(List.of(), List.of(new ProviderA())),
                new BasicRiskEvaluator(),
                new DecisionLogger());

        assertThrows(IllegalStateException.class, () -> pipeline.process(tx, "idem-missing"));
    }

    private static PipelineResult get(Future<PipelineResult> future)
            throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(2, TimeUnit.SECONDS);
    }

    private static final class CoordinatedLookupRegistry extends InMemoryIdempotencyRegistry {
        private final AtomicInteger emptyLookups = new AtomicInteger();
        private final CountDownLatch secondEmptyLookup = new CountDownLatch(1);

        @Override
        public Optional<String> lookup(String idempotencyKey) {
            Optional<String> existing = super.lookup(idempotencyKey);
            if (existing.isPresent()) {
                return existing;
            }
            if (emptyLookups.incrementAndGet() == 2) {
                secondEmptyLookup.countDown();
            } else {
                try {
                    secondEmptyLookup.await(200, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while coordinating lookup", e);
                }
            }
            return super.lookup(idempotencyKey);
        }
    }
}
