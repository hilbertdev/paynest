package com.paynestsystem.app;

import com.paynestsystem.domain.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Alternative JDBC teaching demo against PostgreSQL (not H2).
 * Start the database first: {@code docker compose up -d}
 *
 * <pre>
 *   mvn -q compile exec:java -Dexec.mainClass="com.paynestsystem.app.PostgresJdbcDemo"
 * </pre>
 *
 * Connection values match {@code docker-compose.yml} (local classroom demo only).
 */
public class PostgresJdbcDemo {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/paynest";
    private static final String JDBC_USER = "paynest";
    private static final String JDBC_PASSWORD = "paynest";

    public static void main(String[] args) throws Exception {
        Product laptop = new Product(1, "Laptop", 12000);
        Product mouse = new Product(2, "Mouse", 200);

        System.out.println("=== JDBC demo: PostgreSQL ===");
        System.out.println("1) Connecting to: " + JDBC_URL);
        System.out.println("   user: " + JDBC_USER);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {

            System.out.println("2) Creating table products (if it does not exist)...");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                      id    INT PRIMARY KEY,
                      name  VARCHAR(100) NOT NULL,
                      price DOUBLE PRECISION NOT NULL
                    )
                    """);

            statement.executeUpdate("DELETE FROM products");

            System.out.println("3) Inserting product rows...");
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO products (id, name, price) VALUES (?, ?, ?)")) {
                insertProduct(insert, laptop);
                insertProduct(insert, mouse);
            }

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
        System.out.println("Done. Inspect in pgAdmin or DBeaver:");
        System.out.println("  host: localhost  port: 5432  database: paynest");
        System.out.println("  user / password: paynest / paynest");
        System.out.println("  try: SELECT * FROM products ORDER BY id;");
    }

    private static void insertProduct(PreparedStatement insert, Product product) throws Exception {
        insert.setInt(1, product.getId());
        insert.setString(2, product.getName());
        insert.setDouble(3, product.getPrice());
        insert.executeUpdate();
    }
}
