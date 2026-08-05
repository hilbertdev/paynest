# Code-first migrations walkthrough

**Live session:** 2026-07-16  
**Audience:** first-time programmers  
**Companion files:** [schema-v1.sql](schema-v1.sql), [paynest-erd.drawio](paynest-erd.drawio) (visualisation only), [session README](README.md)

---

## What problem are we solving?

Imagine you run PayNest and take a payment. Your Java program stores that attempt in a **variable in memory** (for example a `HashMap` inside `InMemoryTransactionRecordStore`).

What happens if you:

- stop the program,
- close your laptop,
- or the process crashes?

**Everything in memory is gone.** The next time you start the app, it has no memory of yesterday’s payments. Finance cannot trust counts. Merchants dispute “did you charge me twice?” and you have no row to point at.

A **database** is long-term storage on disk. When your Java program restarts, it can **reload** rows that were saved earlier. Capstone 4 asks you to make payment attempts **durable** — they survive restarts — using **H2** (a small database that already ships on the project classpath).

This session teaches the **code-first** path:

1. You already have **Java classes** that describe the data.
2. You **write SQL** that creates matching tables (a **migration**).
3. You **run** that SQL against H2 so empty tables exist.
4. Later (Capstone 4), you write Java that **inserts and reads** rows.

---

## How we use the ERD in this course

There is a draw.io file in this folder: [paynest-erd.drawio](paynest-erd.drawio).

**Open it whenever a picture helps you.** For example:

- during the live session on a projector,
- while studying relationships (“one order has many line items”),
- when you feel lost about foreign keys.

**Do not treat the ERD as Step 2.**

| Do this | Do not do this |
|---------|----------------|
| Write SQL by reading Java fields | Wait for draw.io, or “export” SQL from the diagram |
| Use the diagram to *check* your understanding | Copy column names only from boxes without opening the `.java` files |

The diagram and [schema-v1.sql](schema-v1.sql) describe the **same** conceptual model on purpose. The **source of truth for writing migrations** is still the domain code under `src/main/java/com/paynestsystem/domain/`.

---

## Glossary (read once, come back often)

**Entity**  
A “thing” your business cares about — for example a customer, a product, or a payment attempt. In Java this is usually a **class**. In a database this becomes a **table**.

**Attribute / field / column**  
A single piece of data about an entity.  
- Java: `private String name;`  
- Database: a **column** named `name`

**Primary key (PK)**  
A value that uniquely identifies **one row**. Like a student number — no two people share the same one. Example: `transaction_records.id`.

**Foreign key (FK)**  
A column that **points to** another table’s primary key. Example: `orders.customer_id` points to `customers.id`. It answers: “which customer does this order belong to?”

**Relationship**  
How entities connect: one customer can have **many** orders (written `1:*` or “one-to-many”).

**ERD (Entity Relationship Diagram)**  
A **picture** of entities and relationships. Helpful for learning. **Not** a tool that writes your PayNest migrations for you.

**DDL (Data Definition Language)**  
SQL that **defines structure**: `CREATE TABLE`, `CREATE INDEX`, constraints. That is what migrations contain.

**DML (Data Manipulation Language)**  
SQL that **moves data**: `INSERT`, `UPDATE`, `SELECT`, `DELETE`. You use DML after tables exist.

**Migration**  
A deliberate, versioned change to the database structure — often a `.sql` file such as `schema-v1.sql`. “v1” means “first version of our schema for this learning session.”

**H2**  
A lightweight Java database. PayNest uses a **file-backed** URL like `jdbc:h2:file:./data/paynest`, so data is stored as files under a `data/` folder (gitignored).

**JDBC**  
Java Database Connectivity — the standard Java API to open a connection and run SQL (`DriverManager`, `Connection`, `Statement` / `PreparedStatement`).

**Code-first**  
You design (or already have) **code**, then create the database shape to match. PayNest is code-first: domain classes exist first; you author DDL next.

**Database-first**  
You design tables first (sometimes with a GUI), then generate or hand-write classes. We are **not** using that approach here.

**ORM / Flyway / Spring `ddl-auto`**  
Tools many companies use to manage schema automatically. **This course does not require them.** Capstone 4 wants you to understand plain JDBC and a schema artefact you wrote yourself.

**Idempotency key**  
A string from an upstream system that means “this is the same logical request again.” If they retry a webhook, the **same key** must return the **same** attempt — not a second charge. We store that key and mark it **UNIQUE** in the database.

---

## Code-first vs database-first (PayNest’s choice)

```
CODE-FIRST (PayNest)

  Java classes already exist
           │
           ▼
  You decide which data must survive restart
           │
           ▼
  You WRITE CREATE TABLE SQL  ←── this is Step 2 (migration)
           │
           ▼
  You APPLY SQL to H2 with JDBC  ←── Step 3
           │
           ▼
  You implement stores that INSERT / SELECT  ←── Capstone 4 work

DATABASE-FIRST (not our path)

  Design tables in a GUI / SQL first
           │
           ▼
  Generate or invent Java classes later
```

Capstone briefs already forbade mandating Spring/Jakarta unless an instructor overrides. Sticking to **hand-written DDL + JDBC** matches Capstone 4 and trains the fundamentals.

---

# Step 1 — Understand the domain (classes)

**Goal:** know which Java types exist, which fields they have, and which ones must become **tables and columns** for Capstone 4.

Open these files in your IDE (paths relative to the repo root):

### Commerce (Capstone 1 — usually still in memory)

| Class | Path | Meaning |
|-------|------|---------|
| `Product` | `src/main/java/com/paynestsystem/domain/Product.java` | Something you sell (`id`, `name`, `price`) |
| `Customer` | `.../domain/Customer.java` | Buyer (`id`, `name`, `email`) |
| `Order` | `.../domain/Order.java` | One purchase (`id`, `customer`, list of items) |
| `OrderItem` | `.../domain/OrderItem.java` | One line: product + quantity |

Notice: `Order` does **not** store `customerId` as an `int`. It holds a whole `Customer` **object**. Databases cannot store Java objects as “nested objects” the same way — they store a **foreign key** (`customer_id`) instead. That translation is part of mapping.

### Payments (Capstone 3–5 — Capstone 4 makes these durable)

| Class | Path | Meaning |
|-------|------|---------|
| `Transaction` | `.../domain/Transaction.java` | Payment inputs: `amount`, `bank`, `timestamp` |
| `TransactionStatus` | `.../domain/TransactionStatus.java` | Enum: `PENDING`, `ROUTED`, `COMPLETED`, `FAILED` |
| `TransactionRecord` | `.../domain/TransactionRecord.java` | Durable wrapper: id, idempotency key, nested `Transaction`, status, timestamps, support fields |
| `AiDecisionRecord` | `.../domain/AiDecisionRecord.java` | Capstone 5 audit: which transaction, AI payload, risk, fallback flag |
| `RiskLevel` | `src/main/java/com/paynestsystem/risk/RiskLevel.java` | Enum: `LOW`, `MEDIUM`, `HIGH` |

### Nested objects (important idea)

In Java, `TransactionRecord` **embeds** a `Transaction`:

```text
TransactionRecord
  ├── id
  ├── idempotencyKey
  ├── transaction  ──► Transaction { amount, bank, timestamp }
  ├── status
  ├── createdAt / updatedAt
  └── ...
```

In SQL for v1 we **flatten** that nesting: columns `amount`, `bank`, `transaction_timestamp` live **on** `transaction_records`. One row still means one attempt; you do not need a separate `transactions` table unless you choose a more advanced design later.

### Worked mapping sheet (Step 1 output)

Use a sheet like this **before** you write SQL. This is how code-first thinking works.

**`TransactionRecord` → table `transaction_records`**

| Java field | SQL column | Suggested type | Notes |
|------------|------------|----------------|-------|
| `id` | `id` | `VARCHAR(64)` | Primary key |
| `idempotencyKey` | `idempotency_key` | `VARCHAR(128)` | **UNIQUE** — Capstone rule |
| `transaction.amount` | `amount` | `DOUBLE` | Flattened |
| `transaction.bank` | `bank` | `VARCHAR(64)` | Flattened |
| `transaction.timestamp` | `transaction_timestamp` | `TIMESTAMP` | Flattened (rename avoids clash with other times) |
| `status` | `status` | `VARCHAR(32)` | Store enum name as text |
| `createdAt` | `created_at` | `TIMESTAMP` | |
| `updatedAt` | `updated_at` | `TIMESTAMP` | |
| `routingSummary` | `routing_summary` | `VARCHAR(512)` | Optional / nullable |
| `assessedRisk` | `assessed_risk` | `VARCHAR(16)` | `RiskLevel` as text |
| `monitoringFlag` | `monitoring_flag` | `BOOLEAN` | |
| `aiAssessmentSummary` | `ai_assessment_summary` | `CLOB` | Long text |

**`AiDecisionRecord` → table `ai_decision_records`**

| Java field | SQL column | Suggested type | Notes |
|------------|------------|----------------|-------|
| *(none)* | `id` | `BIGINT IDENTITY` | Tables often need a PK even if the POJO has none yet |
| `transactionRecordId` | `transaction_record_id` | `VARCHAR(64)` | **FK** → `transaction_records.id` |
| `createdAt` | `created_at` | `TIMESTAMP` | |
| `rawAiPayload` | `raw_ai_payload` | `CLOB` | |
| `resolvedRiskLevel` | `resolved_risk_level` | `VARCHAR(16)` | |
| `usedFallback` | `used_fallback` | `BOOLEAN` | |

**Commerce (optional practise)** — same idea: `Customer` → `customers`, and `Order.customer` becomes `orders.customer_id`.

### Checkpoint (Step 1)

Before you continue, you should be able to answer **without** opening draw.io:

1. Name two Java classes Capstone 4 cares about for durability.  
2. Why is `idempotency_key` special?  
3. What happens to `Transaction` fields when you design `transaction_records`?

---

# Step 2 — Create the migrations (DDL)

**Goal:** turn your Step 1 mapping into real `CREATE TABLE` SQL.

This is the heart of the live session. You are **authoring** structure. Nothing magic generates it for you.

### Open the worked example

Read [schema-v1.sql](schema-v1.sql) from top to bottom. Every major clause has a comment explaining **why** it exists.

Here is the same idea in mini form for `transaction_records` (shortened):

```sql
CREATE TABLE IF NOT EXISTS transaction_records (
    id                VARCHAR(64)  NOT NULL,
    idempotency_key   VARCHAR(128) NOT NULL,
    amount            DOUBLE       NOT NULL,
    bank              VARCHAR(64)  NOT NULL,
    transaction_timestamp TIMESTAMP NOT NULL,
    status            VARCHAR(32)  NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL,
    -- ... support columns ...
    CONSTRAINT pk_transaction_records PRIMARY KEY (id),
    CONSTRAINT uq_transaction_records_idempotency UNIQUE (idempotency_key)
);
```

### What each keyword means (why it exists)

| Piece | Plain meaning |
|-------|----------------|
| `CREATE TABLE` | “Build a new spreadsheet-like place to store rows.” |
| `IF NOT EXISTS` | “If we already created it, do not crash — useful while learning.” |
| `NOT NULL` | “This column must always have a value.” |
| `PRIMARY KEY` | “This column uniquely identifies the row; the DB will enforce uniqueness.” |
| `UNIQUE` | “No two rows may share this value” — our safety net for idempotency. |
| `FOREIGN KEY ... REFERENCES` | “This value must match an existing row in the parent table.” |
| `DEFAULT FALSE` | “If you omit the value on insert, use this.” |
| `CLOB` | “Large text blob” — good for AI payloads and long summaries. |
| `CREATE INDEX` | “Make lookups by this column faster” (and clarify intent for reviewers). |

### Linking SQL to Capstone 4 code

The scaffold already has a placeholder:

[`src/main/java/com/paynestsystem/persistence/jdbc/H2Schema.java`](../../../src/main/java/com/paynestsystem/persistence/jdbc/H2Schema.java)

Capstone 4 asks for a **schema artefact**: either a SQL file **or** constants embedded in that class (or both). For coursework you will often:

1. Develop and review SQL in a file like `schema-v1.sql`.  
2. Copy the statements into `H2Schema` string constants so Java can run them at startup.

### Common mistakes in Step 2

- **Waiting for the ERD before writing SQL.** The classes are enough.  
- **Naming columns only from memory of the diagram** and drifting from Java field meaning.  
- **Forgetting UNIQUE on `idempotency_key`.** Without it, a bug could insert two rows for one retry.  
- **Trying to store a Java object as one column.** Flatten or use foreign keys.  
- **Using spaces in table names.** Prefer `snake_case` like `transaction_records`.

### Checkpoint (Step 2)

1. In your own words: what is a migration?  
2. Why does Capstone 4 care about `UNIQUE (idempotency_key)`?  
3. Point to where the same SQL will likely live in Java for the Capstone deliverable.

If stuck on **relationships**, you may **peek** at [paynest-erd.drawio](paynest-erd.drawio) — then return to the `.java` files to name columns correctly.

---

# Step 3 — Apply the migration

**Goal:** run your DDL against H2 so the empty tables actually exist on disk.

### Where the database lives

Capstone 4 recommends a URL similar to:

```text
jdbc:h2:file:./data/paynest
```

Meaning:

- `jdbc:h2:` — use the H2 driver  
- `file:` — store on disk (not only in RAM)  
- `./data/paynest` — files under a `data/` directory next to where you run Maven  

Those files are **gitignored** (see `.gitignore`) so you do not accidentally commit local databases.

### Mental model of applying a migration

```text
Your SQL text (schema-v1.sql or H2Schema constant)
        │
        ▼
 Java opens a Connection (JDBC)
        │
        ▼
 Java executes CREATE TABLE statements
        │
        ▼
 H2 creates/updates files under ./data/
        │
        ▼
 Tables exist — still empty until you INSERT
```

### Sketch of the Java pattern (learning sketch — not a full Capstone solution)

```java
// Pseudocode / sketch for discussion — you will harden this in Capstone 4
Connection connection = DriverManager.getConnection(
        "jdbc:h2:file:./data/paynest",
        "sa",
        ""
);
try (connection;
     Statement statement = connection.createStatement()) {
    statement.execute(H2Schema.CREATE_TRANSACTION_RECORDS);
    // ... execute other DDL strings ...
}
```

Notes for beginners:

- `sa` / empty password is a common **local** H2 default for teaching. Do not confuse local learning credentials with production security.  
- Always close connections (try-with-resources as above).  
- Run DDL **once at startup** (or via a small “migrate” helper), not on every save of a record.

### How to check it worked

After applying DDL:

- Prefer writing a small test that connects and queries metadata, **or**  
- Use the H2 console / a JDBC tool if your instructor shows one, **or**  
- Attempt an `INSERT` of one row and a `SELECT` back.

If tables are missing, you will usually see a SQLException such as “table not found” — that almost always means DDL was not applied, or you opened a **different** database file path than you think.

### Checkpoint (Step 3)

1. What folder should contain your local H2 files?  
2. What happens if you apply the same `CREATE TABLE IF NOT EXISTS` twice?

---

# Step 4 — Talk to tables from Java (preview)

**Goal:** see how Capstone 4 will *use* the tables — without implementing the full solution here.

The project already separates **interfaces** from **implementations**:

| Interface | Today’s stub | Your Capstone 4 job |
|-----------|--------------|---------------------|
| `TransactionRecordStore` | `InMemoryTransactionRecordStore` | JDBC implementation that `INSERT`/`UPDATE`/`SELECT` against `transaction_records` |
| `IdempotencyRegistry` | `InMemoryIdempotencyRegistry` | Often implemented with the UNIQUE key + lookup on `transaction_records` |
| `AiDecisionStore` | `InMemoryAiDecisionStore` | JDBC against `ai_decision_records` (Capstone 5) |

The **mapping goes both ways**:

```text
Java object  ──save──►  SQL INSERT / UPDATE  ──►  row on disk
Java object  ◄─load──  SQL SELECT           ◄──  row on disk
```

Example story for one field:

1. Java: `customer.getName()` returns `"Ada"`  
2. SQL: column `customers.name` stores `'Ada'`  
3. Later, `ResultSet.getString("name")` rebuilds the Java object  

You already practised the **forward** direction in Steps 1–2 (class → column). Capstone 4 is mostly the **round trip**, plus the `ReliableTransactionPipeline` orchestration and reporting.

**Out of scope for this live session:** shipping a complete `JdbcTransactionRecordStore`. Use this step as a mental bridge.

### Checkpoint (Step 4)

Name the interface Capstone 4 will implement to save a `TransactionRecord`.

---

# Step 5 — Change or reset the schema

**Goal:** know how to recover when you are learning and you change your mind about columns.

### Learning mode: wipe local H2 and start again

When you are still designing v1, it is often easiest to **delete the local database files** and re-run DDL.

**Runbook (local wipe):**

1. **Stop** any running PayNest / Maven process that might hold the DB open.  
2. Delete files under `./data/` that belong to H2 — commonly names like:  
   - `paynest.mv.db`  
   - `paynest.trace.db`  
   (exact names depend on the URL; if unsure, remove the whole `data/` folder contents carefully).  
3. Start the app (or your migrate helper) again so **Step 3** recreates empty tables from your SQL.  
4. Re-run tests that need a clean store.

**Warning:** never delete database files while a connection is open — you can get locked or corrupted local files.

### Later: true versioned migrations

When your schema must evolve **without** wiping production-like data, you add **new** scripts (`schema-v2.sql`) that `ALTER TABLE ...`, keep old files forever, and maybe track a `schema_version` table. Capstone 4 accepts migration notes if your schema evolves iteratively — the important part is honesty and a reproducible runbook.

For early learning, **wipe + recreate** is fine and matches the Capstone emphasis on `data/` hygiene.

### Checkpoint (Step 5)

List the wipe steps from memory. When would wipe be a bad idea outside a student laptop?

---

## Optional visualisation lab (not a dependency)

If the instructor projects the ERD:

1. Open [paynest-erd.drawio](paynest-erd.drawio) in [diagrams.net](https://app.diagrams.net/) or the Draw.io VS Code extension.  
2. Find the green **Commerce** group and the blue **Payments** group.  
3. Trace one crow’s-foot line and say aloud which side is “one” and which is “many.”  
4. Close the diagram and return to your Java mapping sheet — confirm column names still come from classes.

---

## End-of-session lab checklist

Work through these alone or in pairs:

- [ ] Open `TransactionRecord.java` and list every field.  
- [ ] Without looking at SQL, draft on paper the `CREATE TABLE transaction_records (...)` header and three constraints you need.  
- [ ] Compare your draft to [schema-v1.sql](schema-v1.sql).  
- [ ] Explain UNIQUE idempotency to a partner using a “duplicate webhook” story.  
- [ ] Write down the wipe runbook (Step 5).  
- [ ] Optional: open the ERD and match two boxes to class file names.

---

## What we are not using (yet)

| Tool | Role elsewhere | Here |
|------|----------------|------|
| Spring Boot / `ddl-auto` | Auto-create tables from entities | Not required; Capstone forbids mandating Spring |
| Flyway / Liquibase | Numbered migration runners | Fine later if instructor approves; same SQL can move into `V1__....sql` |
| JPA / Hibernate | Map classes ↔ tables with annotations | Not in this scaffold — we train plain JDBC |

If your instructor later introduces Flyway, you already know the hard part: **authoring clear DDL from a domain model.** The runner just applies files in order.

---

## Where this fits Capstone 4

Capstone 4 deliverables include:

- Durable storage (H2 + JDBC is the intended path)  
- A **schema artefact** (SQL file and/or `H2Schema` constants)  
- **Migration notes** if the schema grows  
- A **runbook** for wiping local H2 and knowing where files land  

This live session was the teaching version of that path:

**Step 1 domain → Step 2 migrations → Step 3 apply → stores & wipe.**

The ERD remains a study aid, not a build dependency.

---

## Quick reference links

| Resource | Link |
|----------|------|
| Session index | [README.md](README.md) |
| Migration V1 | [schema-v1.sql](schema-v1.sql) |
| Visual ERD (optional) | [paynest-erd.drawio](paynest-erd.drawio) |
| Capstone 4 brief | [capstone-04-persistence-reliability.md](../../assessments/capstone-04-persistence-reliability.md) |
| `H2Schema` placeholder | [`H2Schema.java`](../../../src/main/java/com/paynestsystem/persistence/jdbc/H2Schema.java) |
| Project README | [../../../README.md](../../../README.md) |
