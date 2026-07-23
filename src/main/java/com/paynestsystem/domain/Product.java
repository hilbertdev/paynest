package com.paynestsystem.domain;

import static com.paynestsystem.common.ValidationUtils.requireNonBlank;
import static com.paynestsystem.common.ValidationUtils.requireNonNegative;

/**
 * Represents a product that can be sold on the PayNest platform.
 * Each product has an id, name, and price.
 */
public class Product {

    private final int id;
    private final String name;
    private final double price;

    /**
     * Creates a new product.
     *
     * @param id    unique identifier for the product
     * @param name  display name of the product
     * @param price price in the local currency (e.g. Rands)
     */
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = requireNonBlank(name, "name");
        this.price = requireNonNegative(price, "price");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
