package com.paynestsystem.domain;

/**
 * Catalogue entry for something a merchant can sell.
 * <p>
 * Kept separate from {@link Order} / {@link OrderItem} on purpose: you can later
 * add fields (SKU, category, description) to {@code Product} without rewriting
 * checkout or total calculation. Orders reference products; they do not own
 * the catalogue definition.
 */
public class Product {

    private final int id;
    private final String name;
    /** Unit price in Rand (R). Capstone 1 uses {@code double} like the starter. */
    private final double price;

    /**
     * Creates a product in one line, e.g. {@code new Product(1, "Laptop", 12000)}.
     *
     * @param id    unique identifier for the product
     * @param name  display name shown on receipts
     * @param price unit price in Rand
     */
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
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
