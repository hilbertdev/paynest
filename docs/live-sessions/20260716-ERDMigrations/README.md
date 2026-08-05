# Live session: ERD + code-first migrations (2026-07-16)

**Audience:** first-time programmers preparing for Capstone 4 (durable payment state with H2).  
**Goal:** understand why we need a database, map PayNest **Java classes** to tables, **write a migration (SQL)**, and know how to apply or wipe a local H2 database.

## How to use this folder

1. Start with the walkthrough: **[01-code-first-migrations-walkthrough.md](01-code-first-migrations-walkthrough.md)**  
   Follow the numbered **Steps** in order.
2. Use the SQL worked example while on **Step 2:** **[schema-v1.sql](schema-v1.sql)**
3. Optionally open the diagram anytime you want a picture: **[paynest-erd.drawio](paynest-erd.drawio)**

### Critical reminder

| Artefact | Role |
|----------|------|
| Domain Java classes under `src/main/java/com/paynestsystem/domain/` | **Source of truth** for what to persist |
| `schema-v1.sql` / later `H2Schema` | **Migration** you write in Step 2 |
| `paynest-erd.drawio` | **Visualisation only** — not required to write migrations |

We do **not** export SQL from draw.io. The diagram and the SQL should describe the same model, but you author SQL by reading your Java fields.

## Session spine (Steps)

| Step | What you do |
|------|-------------|
| **1** | Understand the domain classes and map fields → columns |
| **2** | Create the migration (`CREATE TABLE` SQL) |
| **3** | Apply the migration to H2 via JDBC |
| **4** | Preview how Java talks to tables (stores) |
| **5** | Change or reset the schema (wipe runbook) |

## Files in this pack

| File | Purpose |
|------|---------|
| [01-code-first-migrations-walkthrough.md](01-code-first-migrations-walkthrough.md) | Full teaching guide (verbose, beginner-friendly) |
| [erd-migrations-lecture.pptx](erd-migrations-lecture.pptx) | **Lecture slides** (upload to Google Slides or open in PowerPoint) |
| [build_deck.py](build_deck.py) | Regenerate slides: `python3 -m venv .venv && .venv/bin/pip install python-pptx && .venv/bin/python build_deck.py` |
| [schema-v1.sql](schema-v1.sql) | Commented migration V1 (worked example for Step 2) |
| [paynest-erd.drawio](paynest-erd.drawio) | Optional ERD picture (open in [diagrams.net](https://app.diagrams.net/) or the VS Code Draw.io extension) |

## In-memory durability demo

Shows why Capstone 4 needs a real database. **Run 1 and Run 2 produce different output:**

```bash
# Run 1 — saves records to RAM, writes a marker file
mvn compile exec:java -Dexec.mainClass=com.paynestsystem.app.InMemoryDurabilityDemo

# Run 2 — same command after Run 1 exits; in-memory store is empty (NOT FOUND)
mvn compile exec:java -Dexec.mainClass=com.paynestsystem.app.InMemoryDurabilityDemo

# Reset back to Run 1
mvn compile exec:java -Dexec.mainClass=com.paynestsystem.app.InMemoryDurabilityDemo -Dexec.args=--reset
```

## Related course material

- Capstone 4 brief: [capstone-04-persistence-reliability.md](../../assessments/capstone-04-persistence-reliability.md)
- DDL placeholder in code: [`H2Schema.java`](../../../src/main/java/com/paynestsystem/persistence/jdbc/H2Schema.java)
- Project build/run: [README.md](../../../README.md)
`