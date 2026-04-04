package com.paynestsystem.domain;

import static com.paynestsystem.common.ValidationUtils.requireNonNull;
import static com.paynestsystem.common.ValidationUtils.requirePositive;

/**
 * Represents a single line item in an order.
 * Links a product to a quantity and provides the total cost for that line.
 */
public class OrderItem {

    private final Product product;
    private final int quantity;

    /**
     * Creates a new order item.
     *
     * @param product  the product being ordered
     * @param quantity the number of units
     */
    public OrderItem(Product product, int quantity) {
        this.product = requireNonNull(product, "product");
        this.quantity = requirePositive(quantity, "quantity");
    }

    /**
     * Calculates the total cost for this line item (price * quantity).
     *
     * @return the total cost for this item
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
