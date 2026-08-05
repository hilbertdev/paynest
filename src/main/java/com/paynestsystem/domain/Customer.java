package com.paynestsystem.domain;

/**
 * Person buying from the merchant.
 * <p>
 * Identity lives here (id, name, contact) so an {@link Order} can print a
 * receipt header without duplicating customer fields on every line item.
 * Later you can add phone or address without touching order arithmetic.
 */
public class Customer {

    private final int id;
    private final String name;
    private final String email;

    /**
     * Creates a customer.
     *
     * @param id    unique identifier for the customer
     * @param name  full name shown on the order summary
     * @param email contact email
     */
    public Customer(int id, String name, String email) {
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

    public String getEmail() {
        return email;
    }
}
