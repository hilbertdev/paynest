-- PayNest live session 2026-07-23 — beginner JDBC
-- Run in DBeaver against: jdbc:h2:file:<repo>/data/paynest;AUTO_SERVER=TRUE
-- The same DDL is also executed from PayNestApplication.

CREATE TABLE IF NOT EXISTS products (
  id    INT PRIMARY KEY,
  name  VARCHAR(100) NOT NULL,
  price DOUBLE NOT NULL
);
