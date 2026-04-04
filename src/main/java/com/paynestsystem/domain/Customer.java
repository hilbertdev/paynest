package com.paynestsystem.domain;

import static com.paynestsystem.common.ValidationUtils.requireNonBlank;

/**
 * Represents a customer who can place orders on the PayNest platform.
 * Each customer has an id, name, and email address.
 */
public class Customer {

    private final int id;
    private final String name;
    private final String email;

    /**
     * Creates a new customer.
     *
     * @param id    unique identifier for the customer
     * @param name  full name of the customer
     * @param email email address for contact
     */
    public Customer(int id, String name, String email) {
        this.id = id;
        this.name = requireNonBlank(name, "name");
        this.email = requireNonBlank(email, "email");
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
