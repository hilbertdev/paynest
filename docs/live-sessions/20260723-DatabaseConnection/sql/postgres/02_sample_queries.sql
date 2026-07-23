-- Verification queries for the PostgreSQL JDBC demo.
-- Connect with pgAdmin or DBeaver:
--   host localhost / port 5432 / database paynest / user paynest / password paynest

SELECT * FROM products ORDER BY id;

SELECT COUNT(*) AS product_count FROM products;

SELECT name, price
FROM products
WHERE price >= 1000
ORDER BY price DESC;
