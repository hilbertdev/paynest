-- PayNest live session — PostgreSQL init (Docker Compose)
-- Mounted into /docker-entrypoint-initdb.d on first container start.
-- IDs are INT for teaching clarity.

CREATE TABLE IF NOT EXISTS products (
  id    INT PRIMARY KEY,
  name  VARCHAR(100) NOT NULL,
  price DOUBLE PRECISION NOT NULL
);
