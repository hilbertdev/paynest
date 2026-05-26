package com.paynestsystem.persistence;

import java.util.Optional;

/**
 * Maps idempotency keys to transaction record ids to prevent duplicate processing (Capstone 4).
 */
public interface IdempotencyRegistry {

    /**
     * @return record id if this key was already bound
     */
    Optional<String> lookup(String idempotencyKey);

    /**
     * Associates a key with a record after the record is created (call once per successful new attempt).
     * Implementations should reject attempts to rebind a key to a different record.
     */
    void bind(String idempotencyKey, String recordId);
}
