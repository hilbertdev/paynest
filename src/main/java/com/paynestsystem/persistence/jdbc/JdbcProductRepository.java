package com.paynestsystem.persistence.jdbc;

import com.paynestsystem.domain.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Plain JDBC CRUD for the {@code products} table used in the live JDBC session.
 * Does not wipe rows on connect — UI edits must survive refresh and DBeaver inspection.
 */
public class JdbcProductRepository implements AutoCloseable {

    private final DbTarget target;
    private final Connection connection;

    public JdbcProductRepository(DbTarget target) throws SQLException {
        this.target = target;
        this.connection = target.openConnection();
    }

    public DbTarget getTarget() {
        return target;
    }

    public void ensureSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(target.getCreateProductsSql());
        }
    }

    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(
                     "SELECT id, name, price FROM products ORDER BY id")) {
            while (rows.next()) {
                products.add(new Product(
                        rows.getInt("id"),
                        rows.getString("name"),
                        rows.getDouble("price")));
            }
        }
        return products;
    }

    public void insert(Product product) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO products (id, name, price) VALUES (?, ?, ?)")) {
            insert.setInt(1, product.getId());
            insert.setString(2, product.getName());
            insert.setDouble(3, product.getPrice());
            insert.executeUpdate();
        }
    }

    public int update(Product product) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE products SET name = ?, price = ? WHERE id = ?")) {
            update.setString(1, product.getName());
            update.setDouble(2, product.getPrice());
            update.setInt(3, product.getId());
            return update.executeUpdate();
        }
    }

    public int deleteById(int id) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement(
                "DELETE FROM products WHERE id = ?")) {
            delete.setInt(1, id);
            return delete.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
