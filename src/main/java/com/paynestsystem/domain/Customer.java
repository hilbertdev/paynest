package com.paynestsystem.domain;

import com.paynestsystem.orchestration.Region;

/**
 * Represents a customer who can place orders on the PayNest platform.
 * Each customer has an id, name, email address, and region.
 */
public class Customer {

    private final int id;
    private final String name;
    private final String email;
    private final Region region;

    /**
     * Creates a new customer with a region.
     *
     * @param id     unique identifier for the customer
     * @param name   full name of the customer
     * @param email  email address for contact
     * @param region the geographic region the customer is paying from
     */
    public Customer(int id, String name, String email, Region region) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.region = region;
    }

    /**
     * Creates a new customer with a default region of ZA (South Africa).
     *
     * @param id    unique identifier for the customer
     * @param name  full name of the customer
     * @param email email address for contact
     */
    public Customer(int id, String name, String email) {
        this(id, name, email, Region.ZA);
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

    public Region getRegion() {
        return region;
    }
}
