package com.paynestsystem.domain;

/**
 * One line on an order: a single {@link Product} paired with a quantity.
 * <p>
 * Why this class exists: an order is not just a list of products. Merchants
 * sell multiple units of the same product, so quantity must live next to the
 * product. Line subtotal is {@code unitPrice * quantity} — the same rule
 * {@link Order#calculateTotal()} and {@link Order#printSummary()} rely on.
 */
public class OrderItem {

    private final Product product;
    private final int quantity;

    /**
     * Creates a line item. Quantities must be positive integers; null products
     * are rejected so totals cannot be built from broken data.
     *
     * @param product  the product being ordered (must not be null)
     * @param quantity number of units (must be greater than 0)
     * @throws IllegalArgumentException if product is null or quantity is not positive
     */
    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive integer, got: " + quantity);
        }
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Line subtotal: unit price times quantity (plain {@code double} arithmetic).
     * No extra rounding policy is applied — matches Capstone 1 starter behaviour.
     *
     * @return the total cost for this line
     */
    public double calculateTotal() {
        return product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
