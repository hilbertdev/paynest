package com.paynestsystem.app;

import com.paynestsystem.domain.Customer;
import com.paynestsystem.domain.Order;
import com.paynestsystem.domain.Product;
import com.paynestsystem.orchestration.PaymentOrchestrator;
import com.paynestsystem.orchestration.PaymentProvider;
import com.paynestsystem.orchestration.Region;
import com.paynestsystem.payment.CardPayment;
import com.paynestsystem.payment.EftPayment;
import com.paynestsystem.payment.PaymentMethod;
import com.paynestsystem.service.OrderService;

import java.util.List;

/**
 * Main entry point for the PayNest application.
 * Demonstrates the core commerce flow (Capstone 1), payment processing (Capstone 2),
 * and payment orchestration with provider routing (Capstone 3).
 */
public class PayNestApplication {

    public static void main(String[] args) {
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

        // --- Capstone 3: Payment Orchestration ---
        System.out.println();
        System.out.println("========================================");
        System.out.println("  Capstone 3: Payment Orchestration");
        System.out.println("========================================");
        System.out.println();

        // Set up payment providers with different characteristics
        PaymentProvider payFast = new PaymentProvider(
                "PayFast",
                List.of("CARD", "EFT"),
                List.of(Region.ZA),
                true, 120,           // available, 120ms latency
                0.029, 5.00,         // 2.9% + R5 flat fee
                0, 10000             // handles amounts up to R10,000
        );

        PaymentProvider stripe = new PaymentProvider(
                "Stripe",
                List.of("CARD", "WALLET"),
                List.of(Region.ZA, Region.EU, Region.US, Region.UK, Region.APAC),
                true, 200,           // available, 200ms latency
                0.035, 10.00,        // 3.5% + R10 flat fee
                10000, 100000        // handles high-value amounts R10,000–R100,000
        );

        PaymentProvider ozow = new PaymentProvider(
                "Ozow",
                List.of("EFT"),
                List.of(Region.ZA),
                true, 80,            // available, 80ms latency
                0.015, 2.00,         // 1.5% + R2 flat fee
                0, 50000             // handles amounts up to R50,000
        );

        PaymentProvider unavailableProvider = new PaymentProvider(
                "DownProvider",
                List.of("CARD", "EFT", "WALLET"),
                List.of(Region.ZA, Region.EU),
                false, 50,           // unavailable despite low latency
                0.01, 1.00,
                0, 100000
        );

        // Register providers with the orchestrator
        PaymentOrchestrator orchestrator = new PaymentOrchestrator();
        orchestrator.registerProvider(payFast);
        orchestrator.registerProvider(stripe);
        orchestrator.registerProvider(ozow);
        orchestrator.registerProvider(unavailableProvider);

        // Scenario 1: Standard card payment from South Africa (under R10,000)
        System.out.println("--- Scenario 1: Standard Card Payment (ZA) ---");
        PaymentMethod card = new CardPayment();
        orchestrator.routePayment(card, 5000, Region.ZA);
        System.out.println();

        // Scenario 2: High-value card payment routes to premium provider
        System.out.println("--- Scenario 2: High-Value Card Payment (ZA) ---");
        double highValueTotal = order.calculateTotal(); // R12,400
        orchestrator.routePayment(card, highValueTotal, Region.ZA);
        System.out.println();

        // Scenario 3: EFT payment selects lowest-latency provider
        System.out.println("--- Scenario 3: EFT Payment - Lowest Latency (ZA) ---");
        PaymentMethod eft = new EftPayment();
        orchestrator.routePayment(eft, 3000, Region.ZA);
        System.out.println();

        // Scenario 4: International payment from Europe
        System.out.println("--- Scenario 4: International Card Payment (EU) ---");
        Customer euCustomer = new Customer(2, "Marie Dupont", "marie@example.eu", Region.EU);
        orchestrator.routePayment(card, 15000, euCustomer.getRegion());
        System.out.println();

        // Scenario 5: No provider available for the request
        System.out.println("--- Scenario 5: No Provider Available (APAC EFT) ---");
        orchestrator.routePayment(eft, 1000, Region.APAC);
    }
}
