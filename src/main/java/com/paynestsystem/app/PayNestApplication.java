package com.paynestsystem.app;

import com.paynestsystem.domain.Customer;
import com.paynestsystem.domain.Email;
import com.paynestsystem.domain.Order;
import com.paynestsystem.domain.Product;
import com.paynestsystem.payment.CardPayment;
import com.paynestsystem.payment.PaymentMethod;
import com.paynestsystem.service.OrderService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Main entry point for the PayNest application.
 * Demonstrates the core commerce flow, then a beginner JDBC lesson:
 * connect → create table → insert → query.
 */
public class PayNestApplication {

    /**
     * File-backed H2 database under {@code ./data/paynest}.
     * {@code AUTO_SERVER=TRUE} lets the app and DBeaver share the file.
     * {@code TRACE_LEVEL_FILE=3} writes SQL debug output to {@code data/paynest.trace.db}.
     */
    private static final String JDBC_URL =
            "jdbc:h2:file:./data/paynest;AUTO_SERVER=TRUE;TRACE_LEVEL_FILE=3";

    public static void main(String[] args) throws Exception {
        // --- Capstone 1: Merchant order desk / catalogue engine ---
        // Step 1: Catalogue — at least two products with unit prices in Rand.
        Product laptop = new Product(1, "Laptop", 12000);
        Product mouse = new Product(2, "Mouse", 200);

        // Step 2: Customer identity for the receipt header.
        // Email is a value object: no id, immutable, equal by address — not a raw String.
        Customer customer = new Customer(1, "John Smith", new Email("john@example.com"));

        // Step 3: Create an empty order through OrderService (thin API over Order).
        OrderService orderService = new OrderService();
        Order order = orderService.createOrder(1, customer);

        // Step 4: Add line items — include one product with quantity greater than 1
        // so reviewers can manually check: Mouse 200 * 2 = R400; grand total R12400.
        orderService.addProductsToOrder(order, laptop, 1);
        orderService.addProductsToOrder(order, mouse, 2);

        // Step 5: Print summary — customer, each line subtotal, grand total.
        order.printSummary();

        // --- Capstone 2: OOP Payment System (not required for Capstone 1) ---
        PaymentMethod paymentMethod = new CardPayment();
        order.checkout(paymentMethod);

        // --- JDBC teaching demo (connection → table → insert → query) ---
        runJdbcDemo(laptop, mouse);
    }

    /**
     * Beginner JDBC walkthrough using H2. All steps live in this class on purpose
     * so students can read one file and see the full database story.
     */
    private static void runJdbcDemo(Product laptop, Product mouse) throws Exception {
        Files.createDirectories(Path.of("data"));

        System.out.println();
        System.out.println("=== JDBC demo: H2 database ===");
        System.out.println("1) Connecting to: " + JDBC_URL);

        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {

            // 2) Create a table (DDL)
            System.out.println("2) Creating table products (if it does not exist)...");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                      id    INT PRIMARY KEY,
                      name  VARCHAR(100) NOT NULL,
                      price DOUBLE NOT NULL
                    )
                    """);

            // Clear previous demo rows so re-runs stay easy to follow
            statement.executeUpdate("DELETE FROM products");

            // 3) Insert rows (DML) — use PreparedStatement for values
            System.out.println("3) Inserting product rows...");
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO products (id, name, price) VALUES (?, ?, ?)")) {
                insertProduct(insert, laptop);
                insertProduct(insert, mouse);
            }

            // 4) Query rows back
            System.out.println("4) Querying products:");
            try (ResultSet rows = statement.executeQuery(
                    "SELECT id, name, price FROM products ORDER BY id")) {
                while (rows.next()) {
                    int id = rows.getInt("id");
                    String name = rows.getString("name");
                    double price = rows.getDouble("price");
                    System.out.println("   - #" + id + " " + name + " R" + String.format("%.0f", price));
                }
            }
        }

        System.out.println();
        System.out.println("Done. Open the same DB in DBeaver:");
        System.out.println("  data file:  data/paynest.mv.db   (real tables/rows)");
        System.out.println("  trace file: data/paynest.trace.db (SQL debug log, not the database)");
        System.out.println("  try: SELECT * FROM products;");
    }

    private static void insertProduct(PreparedStatement insert, Product product) throws Exception {
        insert.setInt(1, product.getId());
        insert.setString(2, product.getName());
        insert.setDouble(3, product.getPrice());
        insert.executeUpdate();
    }
}
