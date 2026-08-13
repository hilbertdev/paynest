package com.paynestsystem.domain;

/**
 * Person buying from the merchant.
 * <p>
 * Identity lives here ({@code id}, name, contact) so an {@link Order} can print a
 * receipt header without duplicating customer fields on every line item.
 * {@link Email} is a value object: the customer <em>has</em> an address, but the
 * address itself is not an entity. Later you can add phone or address without
 * touching order arithmetic.
 */
public class Customer {

    private final int id;
    private final String name;
    private final Email email;

    /**
     * Creates a customer.
     *
     * @param id    unique identifier for the customer
     * @param name  full name shown on the order summary
     * @param email contact email (value object — not a raw string)
     */
    public Customer(int id, String name, Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email must not be null");
        }
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }
}
