package com.paynestsystem.app;

import com.paynestsystem.domain.Customer;
import com.paynestsystem.domain.Order;
import com.paynestsystem.domain.Product;
import com.paynestsystem.payment.CardPayment;
import com.paynestsystem.payment.PaymentMethod;
import com.paynestsystem.service.OrderService;
import com.paynestsystem.types.TypesDemo;

/**
 * Main entry point for the PayNest application.
 * Demonstrates the Java Types foundation module, core commerce flow (Capstone 1),
 * and payment processing (Capstone 2).
 */
public class PayNestApplication {

    public static void main(String[] args) {
        // --- Foundation: Java Types ---
        TypesDemo.runAllDemos();

        // --- Capstone 1: Core Commerce Engine ---
        // Create sample products
        Product laptop = new Product(1, "Laptop", 12000);
        Product mouse = new Product(2, "Mouse", 200);

        // Create a customer
        Customer customer = new Customer(1, "John Smith", "john@example.com");

        // Create order and add products via OrderService
        OrderService orderService = new OrderService();
        Order order = orderService.createOrder(1, customer);
        orderService.addProductsToOrder(order, laptop, 1);
        orderService.addProductsToOrder(order, mouse, 2);

        // Print order summary
        order.printSummary();

        // --- Capstone 2: OOP Payment System ---
        // Choose a payment method and process checkout
        PaymentMethod paymentMethod = new CardPayment();
        order.checkout(paymentMethod);
    }
}
