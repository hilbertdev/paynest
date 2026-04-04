package com.paynestsystem.service;

import com.paynestsystem.domain.Customer;
import com.paynestsystem.domain.Order;
import com.paynestsystem.domain.Product;

import static com.paynestsystem.common.ValidationUtils.requireNonNull;

/**
 * Handles business logic for creating and managing orders.
 * Provides methods to create orders, add products, and calculate totals.
 */
public class OrderService {

    /**
     * Creates a new order for the given customer.
     *
     * @param orderId  unique identifier for the order
     * @param customer the customer placing the order
     * @return the newly created order
     */
    public Order createOrder(int orderId, Customer customer) {
        return new Order(orderId, requireNonNull(customer, "customer"));
    }

    /**
     * Adds a product to an existing order.
     *
     * @param order    the order to add to
     * @param product  the product to add
     * @param quantity the number of units
     */
    public void addProductsToOrder(Order order, Product product, int quantity) {
        requireNonNull(order, "order").addItem(requireNonNull(product, "product"), quantity);
    }

    /**
     * Calculates the total cost of all items in the order.
     *
     * @param order the order to calculate
     * @return the total amount
     */
    public double calculateTotals(Order order) {
        return requireNonNull(order, "order").calculateTotal();
    }
}
