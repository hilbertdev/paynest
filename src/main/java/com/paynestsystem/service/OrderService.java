package com.paynestsystem.service;

import com.paynestsystem.domain.Customer;
import com.paynestsystem.domain.Order;
import com.paynestsystem.domain.Product;

/**
 * Thin business API over the order domain for Capstone 1.
 * <p>
 * Flow: {@link #createOrder} → {@link #addProductsToOrder} → {@link #calculateTotals}
 * (or {@link Order#printSummary()}). Validation of quantity and product lives in
 * the domain ({@code OrderItem} / {@code Order}), not here — one place owns the
 * rules so callers cannot bypass them by using {@code Order} directly.
 */
public class OrderService {

    /**
     * Creates a new empty order for the given customer.
     *
     * @param orderId  unique identifier for the order
     * @param customer the customer placing the order
     * @return the newly created order
     */
    public Order createOrder(int orderId, Customer customer) {
        return new Order(orderId, customer);
    }

    /**
     * Adds a product line to an existing order.
     * Delegates to {@link Order#addItem}; domain rejects invalid quantities.
     *
     * @param order    the order to add to
     * @param product  the product to add
     * @param quantity the number of units (must be &gt; 0)
     */
    public void addProductsToOrder(Order order, Product product, int quantity) {
        order.addItem(product, quantity);
    }

    /**
     * Returns the order grand total via the same domain method used by printSummary.
     *
     * @param order the order to calculate
     * @return the total amount
     */
    public double calculateTotals(Order order) {
        return order.calculateTotal();
    }
}
