package com.paynestsystem.app;

import com.paynestsystem.domain.Transaction;
import com.paynestsystem.domain.TransactionRecord;
import com.paynestsystem.domain.TransactionStatus;
import com.paynestsystem.persistence.InMemoryIdempotencyRegistry;
import com.paynestsystem.persistence.InMemoryTransactionRecordStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Teaching demo: payment records in {@link InMemoryTransactionRecordStore} vanish when the JVM stops.
 *
 * <p><b>Run 1</b> (first launch): saves sample records, writes a small marker file, asks you to re-run.
 * <p><b>Run 2</b> (same command after exit): new empty in-memory store — previous records are gone.
 *
 * <p>Run:
 * <pre>{@code
 * mvn compile exec:java -Dexec.mainClass="com.paynestsystem.app.InMemoryDurabilityDemo"
 * }</pre>
 * Then run the same command again. Output should differ between Run 1 and Run 2.
 *
 * <p>Reset to Run 1:
 * <pre>{@code
 * mvn compile exec:java -Dexec.mainClass="com.paynestsystem.app.InMemoryDurabilityDemo" -Dexec.args="--reset"
 * }</pre>
 */
public final class InMemoryDurabilityDemo {

    private static final Path SESSION_MARKER = Path.of("data", "inmemory-demo.marker");

    private static final List<SamplePayment> SAMPLES = List.of(
            new SamplePayment("pay-001", "merchant-ref-100", 500.0, "FNB", TransactionStatus.PENDING),
            new SamplePayment("pay-002", "merchant-ref-101", 1200.0, "Capitec", TransactionStatus.COMPLETED)
    );

    public static void main(String[] args) {
        printBanner();

        if (args.length > 0 && "--reset".equals(args[0])) {
            resetSession();
            return;
        }

        if (Files.exists(SESSION_MARKER)) {
            runAfterRestart();
        } else {
            runFirstLaunch();
        }
    }

    private static void runFirstLaunch() {
        System.out.println("RUN 1 — First launch (saving to in-memory store)");
        System.out.println("------------------------------------------------");
        System.out.println("New JVM → new empty InMemoryTransactionRecordStore.");
        System.out.println();

        InMemoryTransactionRecordStore store = new InMemoryTransactionRecordStore();
        InMemoryIdempotencyRegistry idempotency = new InMemoryIdempotencyRegistry();

        for (SamplePayment sample : SAMPLES) {
            saveSample(store, idempotency, sample);
        }

        printStoreContents(store);
        System.out.println("Idempotency registry entries: " + idempotency.count());
        System.out.println("  merchant-ref-100 -> " + idempotency.lookup("merchant-ref-100").orElse("(missing)"));
        System.out.println();

        writeSessionMarker();
        System.out.println("Marker written: " + SESSION_MARKER.toAbsolutePath());
        System.out.println("(Marker only records that Run 1 happened — it is NOT the payment database.)");
        System.out.println();
        System.out.println(">>> Stop this program (Ctrl+C or let it exit), then run the SAME command again.");
        System.out.println(">>> Run 2 will start a new JVM with an empty in-memory store.");
    }

    private static void runAfterRestart() {
        System.out.println("RUN 2 — New launch (simulating restart after you stopped the program)");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Marker found — a previous Run 1 claimed it saved:");
        try {
            Files.readAllLines(SESSION_MARKER).forEach(line -> System.out.println("  " + line));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + SESSION_MARKER, e);
        }
        System.out.println();
        System.out.println("This JVM creates a brand-new InMemoryTransactionRecordStore (empty HashMap).");
        System.out.println();

        InMemoryTransactionRecordStore store = new InMemoryTransactionRecordStore();
        InMemoryIdempotencyRegistry idempotency = new InMemoryIdempotencyRegistry();

        System.out.println("Looking up records from the previous run:");
        for (SamplePayment sample : SAMPLES) {
            lookupAndPrint(store, sample.id());
        }
        System.out.println();
        System.out.println("Store count in THIS JVM: " + store.count());
        System.out.println("Idempotency lookup merchant-ref-100: "
                + idempotency.lookup("merchant-ref-100").orElse("(missing)"));
        System.out.println();
        System.out.println("CONCLUSION");
        System.out.println("----------");
        System.out.println("Run 1 had 2 records in RAM. Run 2 has 0 — memory does not survive restart.");
        System.out.println("Capstone 4 persists rows to H2 on disk so they reload after restart.");
        System.out.println();
        System.out.println("Replay Run 1: mvn ... -Dexec.args=\"--reset\"  (or delete " + SESSION_MARKER + ")");
    }

    private static void resetSession() {
        try {
            if (Files.deleteIfExists(SESSION_MARKER)) {
                System.out.println("Deleted " + SESSION_MARKER.toAbsolutePath());
            } else {
                System.out.println("No marker file — already at Run 1 state.");
            }
            System.out.println("Run again without --reset to see Run 1 (save), then Run 2 (empty store).");
        } catch (IOException e) {
            throw new IllegalStateException("Could not delete " + SESSION_MARKER, e);
        }
    }

    private static void writeSessionMarker() {
        try {
            Files.createDirectories(SESSION_MARKER.getParent());
            List<String> lines = SAMPLES.stream()
                    .map(sample -> sample.id() + " | key=" + sample.idempotencyKey()
                            + " | R" + (int) sample.amount() + " | " + sample.bank())
                    .toList();
            Files.write(SESSION_MARKER, lines);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write " + SESSION_MARKER, e);
        }
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("========================================");
        System.out.println(" PayNest: In-Memory vs Restart Demo");
        System.out.println("========================================");
        System.out.println();
    }

    private static void saveSample(
            InMemoryTransactionRecordStore store,
            InMemoryIdempotencyRegistry idempotency,
            SamplePayment sample) {
        Instant now = Instant.now();
        Transaction transaction = new Transaction(sample.amount(), sample.bank(), now);
        TransactionRecord record = new TransactionRecord(
                sample.id(),
                sample.idempotencyKey(),
                transaction,
                sample.status(),
                now,
                now);
        store.save(record);
        idempotency.bind(sample.idempotencyKey(), sample.id());
        System.out.printf("  Saved %s | key=%s | R%.0f | %s | %s%n",
                sample.id(), sample.idempotencyKey(), sample.amount(), sample.bank(), sample.status());
    }

    private static void printStoreContents(InMemoryTransactionRecordStore store) {
        System.out.println();
        System.out.println("Store now contains " + store.count() + " record(s):");
        store.findAll().stream()
                .sorted(Comparator.comparing(TransactionRecord::getId))
                .forEach(record -> System.out.printf(
                        "  - %s | key=%s | R%.0f | %s | %s%n",
                        record.getId(),
                        record.getIdempotencyKey(),
                        record.getTransaction().getAmount(),
                        record.getTransaction().getBank(),
                        record.getStatus()));
    }

    private static void lookupAndPrint(InMemoryTransactionRecordStore store, String id) {
        var found = store.findById(id);
        if (found.isPresent()) {
            TransactionRecord record = found.get();
            System.out.printf("  %s: FOUND (R%.0f, %s)%n",
                    id, record.getTransaction().getAmount(), record.getStatus());
        } else {
            System.out.printf("  %s: NOT FOUND%n", id);
        }
    }

    private record SamplePayment(
            String id,
            String idempotencyKey,
            double amount,
            String bank,
            TransactionStatus status) {
    }

    private InMemoryDurabilityDemo() {
    }
}
