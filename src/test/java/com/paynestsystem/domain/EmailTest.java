package com.paynestsystem.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * DDD teaching example: Email is a value object — validated, immutable, equal by value.
 */
class EmailTest {

    @Test
    void constructor_storesNormalisedAddress() {
        Email email = new Email("  John@Example.com  ");

        assertEquals("john@example.com", email.getValue());
        assertEquals("john@example.com", email.toString());
    }

    @Test
    void equals_sameAddressDifferentCase_areEqual() {
        Email left = new Email("john@example.com");
        Email right = new Email("JOHN@EXAMPLE.COM");

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
    }

    @Test
    void equals_differentAddresses_areNotEqual() {
        assertNotEquals(new Email("john@example.com"), new Email("jane@example.com"));
    }

    @Test
    void constructor_rejectsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
        assertThrows(IllegalArgumentException.class, () -> new Email("   "));
    }

    @Test
    void constructor_rejectsMalformed() {
        assertThrows(IllegalArgumentException.class, () -> new Email("john.example.com"));
        assertThrows(IllegalArgumentException.class, () -> new Email("@example.com"));
        assertThrows(IllegalArgumentException.class, () -> new Email("john@"));
        assertThrows(IllegalArgumentException.class, () -> new Email("john@jane@example.com"));
    }
}
