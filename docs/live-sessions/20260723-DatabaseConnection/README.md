# Live session 2026-07-23 — JDBC with H2

A beginner session: open a database connection, create a table, insert rows, and query them back. All Java for this lesson lives in [`PayNestApplication.java`](../../../src/main/java/com/paynestsystem/app/PayNestApplication.java).

## Learning goals

By the end of this session you can:

1. **Connect** to a file-backed H2 database with JDBC
2. **Create** a table with SQL (`CREATE TABLE`)
3. **Insert** rows with a `PreparedStatement`
4. **Query** rows with `SELECT` and read a `ResultSet`
5. Inspect the same data in **DBeaver**

## What is H2?

**H2** is a lightweight open-source SQL database written in Java. It can run *inside* your app (embedded) or as a small local server — see the [H2 Database Tutorial](https://www.tutorialspoint.com/h2_database/index.htm).

**Analogy:** think of H2 as a **notebook that sits next to your Java program**. You write product lists into it and read them back later. You do not need a big “library building” (Postgres/MySQL) for this lesson.

Two common modes:

- **File-backed** (this session) — data is saved on disk and survives restart
- **In-memory** — data lives only while the app runs, then disappears

PayNest uses a **file-backed** H2 URL:

```text
jdbc:h2:file:./data/paynest;AUTO_SERVER=TRUE;TRACE_LEVEL_FILE=3
```

| Piece | Meaning |
|-------|---------|
| `jdbc:h2:file:` | Use H2, store data on disk (survives restart) |
| `./data/paynest` | Database name/path relative to the project root |
| `AUTO_SERVER=TRUE` | App and DBeaver can open the same file together |
| `TRACE_LEVEL_FILE=3` | Write a SQL debug log |

### Files you will see under `data/`

| File | What it is |
|------|------------|
| `paynest.mv.db` | **The real database** — tables and rows live here |
| `paynest.trace.db` | **Debug log only** — SQL statements H2 recorded; not for DBeaver queries |

Always connect DBeaver to the database name `.../data/paynest` (which uses `.mv.db`). Do not open `.trace.db` as your database.

### DBeaver vs pgAdmin

| Tool | Use with H2? |
|------|----------------|
| **DBeaver** | Yes — recommended |
| H2 Console | Yes — optional fallback |
| **pgAdmin** | No — PostgreSQL only |

## What is JDBC?

**JDBC** (Java Database Connectivity) is the standard Java API for talking to relational databases: connect, run SQL, and read results. Overview: [Introduction to JDBC](https://www.geeksforgeeks.org/java/introduction-to-jdbc/).

**Analogy:** JDBC is the **phone line between Java and the database**. Java speaks methods and objects; the database speaks SQL. A JDBC *driver* (here, the H2 driver) translates both ways.

Cast of characters you will use today:

| Piece | Role (analogy) |
|-------|----------------|
| `DriverManager` | Dials the number — opens a connection from a JDBC URL |
| `Connection` | The open call — your session with the database |
| `Statement` / `PreparedStatement` | The message you send — SQL to create, insert, or select |
| `ResultSet` | The reply — walk rows with `next()`, then `getInt` / `getString` |

Tiny shape of the flow (same idea as in `PayNestApplication`):

```java
try (Connection conn = DriverManager.getConnection(url);
     Statement stmt = conn.createStatement()) {
    stmt.execute("CREATE TABLE IF NOT EXISTS products (...)");
    // PreparedStatement for INSERT with ? placeholders
    try (ResultSet rows = stmt.executeQuery("SELECT id, name, price FROM products")) {
        while (rows.next()) {
            // read one row at a time
        }
    }
}
```

H2 is *which database*; JDBC is *how Java talks to it*. Same JDBC pattern works later with PostgreSQL — only the URL and driver change.

## Prerequisites

- Java 21, Maven 3.6+
- [DBeaver Community](https://dbeaver.io/download/)
- This repo cloned; run Maven from the **project root**

## Step-by-step

### 1. Run the Java demo

```bash
mvn -q compile exec:java
```

After the order/payment output you should see something like:

```text
=== JDBC demo: H2 database ===
1) Connecting to: jdbc:h2:file:./data/paynest;...
2) Creating table products ...
3) Inserting product rows...
4) Querying products:
   - #1 Laptop R12000
   - #2 Mouse R200
```

That is the full loop: **connect → create → insert → query**.

### 2. Connect DBeaver

1. **Database → New Database Connection → H2**
2. JDBC URL (use your absolute path):

   ```text
   jdbc:h2:file:/Users/YOU/source/PayNest/data/paynest;AUTO_SERVER=TRUE
   ```

3. User/password: empty, or `sa` / empty
4. Test connection → Finish

### 3. Optional: run SQL scripts by hand

- Schema: [`sql/01_schema.sql`](sql/01_schema.sql)
- Checks: [`sql/02_sample_queries.sql`](sql/02_sample_queries.sql)

Or simply:

```sql
SELECT * FROM products ORDER BY id;
```

### 4. Wipe and start over

```bash
# Disconnect DBeaver first
rm -rf data/
```

## Code map (one class)

In `PayNestApplication`:

| Step | API |
|------|-----|
| Connect | `DriverManager.getConnection(JDBC_URL)` |
| Create table | `Statement.execute("CREATE TABLE ...")` |
| Insert | `PreparedStatement` + `setInt` / `setString` / `setDouble` |
| Query | `executeQuery("SELECT ...")` + `ResultSet.next()` |

## Slides

Importable deck for class: [`slides/PayNest-JDBC-H2.pptx`](slides/PayNest-JDBC-H2.pptx)

Open in **Google Slides**: Drive → New → File upload → open with Google Slides  
(or PowerPoint / Keynote).

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| Empty `data/` after run | Run Maven from the repo root |
| “Database may be already in use” | Use the same URL with `AUTO_SERVER=TRUE`; close extra connections |
| Table not found in DBeaver | Wrong path, or run the app once so the table is created |
| Confused by `.trace.db` | That file is a log — query `.mv.db` via the `paynest` JDBC URL |
| pgAdmin will not connect | Expected for H2 — use DBeaver, or switch to the PostgreSQL alternative below |

## Visual demo — Swing product CRUD

A small desktop UI to **create / update / delete** products while you show the same rows in DBeaver or pgAdmin. Switch **H2 ↔ Postgres** in the window (two separate databases — data does not copy across).

```bash
# optional — only needed when you flip the UI switch to Postgres
docker compose up -d

mvn -q compile exec:java -Dexec.mainClass="com.paynestsystem.app.ProductCrudApp"
```

| Piece | Role |
|-------|------|
| [`ProductCrudApp`](../../../src/main/java/com/paynestsystem/app/ProductCrudApp.java) | Swing window + CRUD buttons |
| [`JdbcProductRepository`](../../../src/main/java/com/paynestsystem/persistence/jdbc/JdbcProductRepository.java) | JDBC insert / update / delete / select |
| [`DbTarget`](../../../src/main/java/com/paynestsystem/persistence/jdbc/DbTarget.java) | H2 vs Postgres URL + `CREATE TABLE` dialect |

H2 still uses `AUTO_SERVER=TRUE`, so keep DBeaver connected to `./data/paynest` and refresh after each click. For Postgres, inspect `localhost:5432` / `paynest` the same way as below.

Unlike the console demos, this UI **does not wipe** the `products` table on connect — edits stick so students can see them in the DB tool.

## Alternative demo — PostgreSQL + Docker

Same JDBC lesson (connect → create → insert → query), but against a **real PostgreSQL** server instead of H2. Useful if you want pgAdmin, or to avoid H2 driver version mismatches in DBeaver.

### 1. Start the database

From the repo root:

```bash
docker compose up -d
```

This starts Postgres 16 with:

| Setting | Value |
|---------|--------|
| Host / port | `localhost:5432` |
| Database | `paynest` |
| User | `paynest` |
| Password | `paynest` (classroom demo only) |
| JDBC URL | `jdbc:postgresql://localhost:5432/paynest` |

Init SQL: [`sql/postgres/01_schema.sql`](sql/postgres/01_schema.sql) (`products.id` is **INT**).

### 2. Run the Java demo

Connection values are **hardcoded** in [`PostgresJdbcDemo.java`](../../../src/main/java/com/paynestsystem/app/PostgresJdbcDemo.java) to match Docker Compose:

```bash
mvn -q compile exec:java -Dexec.mainClass="com.paynestsystem.app.PostgresJdbcDemo"
```

### 3. Inspect in pgAdmin or DBeaver

- Host `localhost`, port `5432`, database `paynest`
- User / password: `paynest` / `paynest`
- Queries: [`sql/postgres/02_sample_queries.sql`](sql/postgres/02_sample_queries.sql)

### 4. Stop / reset

```bash
docker compose down
# wipe volume and re-init schema on next start:
docker compose down -v && docker compose up -d
```
