# Live session: Noun–verb domain analysis (DDD walkthrough)

**Audience:** Java students who have completed or are working through Capstone 1, preparing for later capstones.  
**Goal:** learn how to read a codebase like an architect — extract business language, spot aggregates, and map nouns/verbs to code.

## How to use this folder

1. Start with the full walkthrough: **[01-noun-verb-domain-analysis.md](01-noun-verb-domain-analysis.md)**
2. Skim the capstone briefs for business context: [`docs/assessments/`](../../assessments/README.md)
3. Run the Capstone 1–2 demo while reading Section 1:
   ```bash
   mvn exec:java
   ```
4. Optional follow-up session (persistence): [ERD + code-first migrations (2026-07-16)](../20260716-ERDMigrations/README.md)

## What you will learn

| Section | Topic |
|---------|--------|
| 1 | Business problem, actors, workflows |
| 2 | Ubiquitous language — every important noun classified |
| 3 | Verbs grouped as commands, behaviours, queries, events |
| 4 | Aggregate responsibilities and invariants |
| 5 | Mermaid relationship diagram |
| 6 | Bounded contexts and why the repo is split |
| 7 | Use cases with trigger → outcome |
| 8 | Architecture layers mapped to business language |
| 9–11 | Design decisions — why nouns/verbs became what they did |
| 12 | Apply patterns to your own payment orchestration platform |

## Related material

- Capstone 1 brief: [capstone-01-commerce-engine.md](../../assessments/capstone-01-commerce-engine.md)
- Project README: [README.md](../../../README.md)
- ERD live session: [20260716-ERDMigrations](../20260716-ERDMigrations/README.md)
