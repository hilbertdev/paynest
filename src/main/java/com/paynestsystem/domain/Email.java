package com.paynestsystem.domain;

import java.util.Objects;

/**
 * Customer contact address modelled as a <em>value object</em>.
 * <p>
 * Unlike {@link Customer}, an email has no identity of its own: two addresses
 * with the same normalised text are the same email. That is why this class
 * has no {@code id}, is immutable, and implements {@link #equals(Object)} /
 * {@link #hashCode()} on the address itself.
 * <p>
 * The constructor owns the invariant: a {@code Customer} never stores a blank
 * or malformed string. Invalid input fails here, not later on a receipt or
 * in a database row.
 */
public final class Email {

    private final String value;

    /**
     * Creates an email from a raw string. Leading/trailing space is trimmed and
     * the address is stored in lowercase so {@code John@Example.com} and
     * {@code john@example.com} compare equal.
     *
     * @param value the address (must include a local part and a domain)
     * @throws IllegalArgumentException if the value is blank or missing {@code @}
     */
    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        String normalised = value.trim().toLowerCase();
        int at = normalised.indexOf('@');
        if (at <= 0 || at != normalised.lastIndexOf('@') || at == normalised.length() - 1) {
            throw new IllegalArgumentException(
                    "Email must contain a local part and a domain, got: " + value);
        }
        this.value = normalised;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Email email)) {
            return false;
        }
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
