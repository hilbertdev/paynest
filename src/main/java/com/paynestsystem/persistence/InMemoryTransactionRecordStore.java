package com.paynestsystem.persistence;

import com.paynestsystem.domain.TransactionRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for development and tests — not durable across JVM restarts (Capstone 4 stub).
 */
public class InMemoryTransactionRecordStore implements TransactionRecordStore {

    private final Map<String, TransactionRecord> byId = new ConcurrentHashMap<>();

    @Override
    public void save(TransactionRecord record) {
        byId.put(record.getId(), record);
    }

    @Override
    public Optional<TransactionRecord> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    /** For demos and tests — not part of the production store contract. */
    public int count() {
        return byId.size();
    }

    /** For demos and tests — not part of the production store contract. */
    public List<TransactionRecord> findAll() {
        return new ArrayList<>(byId.values());
    }
}
