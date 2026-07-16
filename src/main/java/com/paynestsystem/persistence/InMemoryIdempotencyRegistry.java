package com.paynestsystem.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory idempotency map — suitable for single-process demos only (Capstone 4 stub).
 */
public class InMemoryIdempotencyRegistry implements IdempotencyRegistry {

    private final Map<String, String> keyToRecordId = new ConcurrentHashMap<>();

    @Override
    public Optional<String> lookup(String idempotencyKey) {
        return Optional.ofNullable(keyToRecordId.get(idempotencyKey));
    }

    @Override
    public void bind(String idempotencyKey, String recordId) {
        String existingRecordId = keyToRecordId.putIfAbsent(idempotencyKey, recordId);
        if (existingRecordId != null && !existingRecordId.equals(recordId)) {
            throw new IllegalStateException("Idempotency key is already bound to a different record");
        }
    }

    /** For demos and tests — not part of the production registry contract. */
    public int count() {
        return keyToRecordId.size();
    }
}
