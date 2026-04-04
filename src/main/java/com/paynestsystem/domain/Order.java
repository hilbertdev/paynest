package com.paynestsystem.domain;

import com.paynestsystem.common.CurrencyFormatter;
import com.paynestsystem.payment.PaymentMethod;
import com.paynestsystem.payment.PaymentProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.paynestsystem.common.ValidationUtils.requireNonNull;

/**
 * Represents an order placed by a customer.
 * Contains the customer, a list of items, and methods to calculate totals and print a summary.
 */
public class Order {

    private final int id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final PaymentProcessor paymentProcessor;

    /**
     * Creates a new order for the given customer.
     *
     * @param id       unique identifier for the order
     * @param customer the customer placing the order
     */
    public Order(int id, Customer customer) {
        this(id, customer, new PaymentProcessor());
    }

    public Order(int id, Customer customer, PaymentProcessor paymentProcessor) {
        this.id = id;
        this.customer = requireNonNull(customer, "customer");
        this.paymentProcessor = requireNonNull(paymentProcessor, "paymentProcessor");
        this.items = new ArrayList<>();
    }

    /**
     * Adds a product to the order with the specified quantity.
     *
     * @param product  the product to add
     * @param quantity the number of units
     */
    public void addItem(Product product, int quantity) {
        items.add(new OrderItem(product, quantity));
    }

    /**
     * Calculates the total cost of all items in the order.
     *
     * @return the total amount
     */
    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.calculateTotal();
        }
        return total;
    }

    public String buildSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Order Summary").append(System.lineSeparator());
        summary.append("Customer: ").append(customer.getName()).append(System.lineSeparator());
        summary.append(System.lineSeparator());
        summary.append("Items:").append(System.lineSeparator());

        for (OrderItem item : items) {
            summary.append(item.getProduct().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(" - ")
                    .append(CurrencyFormatter.formatZar(item.calculateTotal()))
                    .append(System.lineSeparator());
        }

        summary.append(System.lineSeparator());
        summary.append("Total: ").append(CurrencyFormatter.formatZar(calculateTotal()));
        return summary.toString();
    }

    /**
     * Prints a summary of the order to the console.
     * Shows customer name, each item with quantity and price, and the total.
     */
    public void printSummary() {
        System.out.println(buildSummary());
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Completes the order by processing payment with the given payment method.
     *
     * @param paymentMethod the payment method to use for checkout
     */
    public void checkout(PaymentMethod paymentMethod) {
        double total = calculateTotal();
        paymentProcessor.processPayment(paymentMethod, total);
        System.out.println("Order completed successfully.");
    }
}
