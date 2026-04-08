package com.paynestsystem.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionResultTest {

    @Test
    void success_containsValue() {
        TransactionResult<String> result = TransactionResult.success("REF-001");
        assertEquals("REF-001", result.getValue());
    }

    @Test
    void success_isSuccess_returnsTrue() {
        TransactionResult<String> result = TransactionResult.success("REF-001");
        assertTrue(result.isSuccess());
    }

    @Test
    void failure_isSuccess_returnsFalse() {
        TransactionResult<String> result = TransactionResult.failure("Declined");
        assertFalse(result.isSuccess());
    }

    @Test
    void failure_getErrorMessage_returnsMessage() {
        TransactionResult<String> result = TransactionResult.failure("Insufficient funds");
        assertEquals("Insufficient funds", result.getErrorMessage());
    }

    @Test
    void success_getErrorMessage_throwsException() {
        TransactionResult<String> result = TransactionResult.success("REF-001");
        assertThrows(IllegalStateException.class, result::getErrorMessage);
    }

    @Test
    void failure_getValue_throwsException() {
        TransactionResult<String> result = TransactionResult.failure("Declined");
        assertThrows(IllegalStateException.class, result::getValue);
    }

    @Test
    void success_withMoneyPayload_worksCorrectly() {
        Money refund = new Money(5000, CurrencyCode.ZAR);
        TransactionResult<Money> result = TransactionResult.success(refund);
        assertTrue(result.isSuccess());
        assertEquals(refund, result.getValue());
    }
}
