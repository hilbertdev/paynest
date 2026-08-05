-- Verification queries for the JDBC live session.
-- Run after: mvn -q compile exec:java

SELECT * FROM products ORDER BY id;

SELECT COUNT(*) AS product_count FROM products;

SELECT name, price
FROM products
WHERE price >= 1000
ORDER BY price DESC;
