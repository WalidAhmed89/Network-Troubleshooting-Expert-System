# Database Documentation

This document describes the PostgreSQL schema for the **Network
Troubleshooting Expert System**.

## 1. Purpose

The database stores the knowledge used by the rule-based expert system,
the facts collected from users, and the history of diagnosis sessions.
The Java backend is intended to read rules and evaluate them through a
separate inference engine.

The schema currently contains seven tables:

1.  `users`
2.  `diagnosis_sessions`
3.  `session_facts`
4.  `rules`
5.  `rule_conditions`
6.  `rule_conclusions`
7.  `matched_rules`

## 2. Entity Relationship Overview

``` text
users
  1
  |
  | many
  v
diagnosis_sessions
  1                 1
  |                 |
  | many            | many
  v                 v
session_facts    matched_rules
                    ^
                    | many
                    | 
                  rules
                    |
             +------+------+
             |             |
             | 1           | 1
             v             v
      rule_conditions  rule_conclusions
```

Relationship summary:

-   One user can have many diagnosis sessions.
-   One diagnosis session can contain many facts.
-   One diagnosis session can have many matched-rule records.
-   One rule can have many conditions.
-   One rule can have many conclusions.
-   One rule can match in many diagnosis sessions, represented through
    `matched_rules`.

In the SQL schema, these relationships are represented by foreign keys.
If the schema file does not contain a foreign-key constraint for a
relationship, a similarly named column alone does not enforce that
relationship.

## 3. Table Reference

### `users`

Stores user information.

  -----------------------------------------------------------------------
  Column                  Type                    Description
  ----------------------- ----------------------- -----------------------
  `id`                    `SERIAL`                Primary key

  `name`                  `VARCHAR(100)`          User's name; required

  `email`                 `VARCHAR(150)`          Required, unique email

  `created_at`            `TIMESTAMP`             Creation timestamp;
                                                  defaults to the current
                                                  time
  -----------------------------------------------------------------------

### `diagnosis_sessions`

Represents a troubleshooting attempt.

  -----------------------------------------------------------------------
  Column                  Type                    Description
  ----------------------- ----------------------- -----------------------
  `id`                    `SERIAL`                Primary key

  `user_id`               `INT`                   References `users.id`;
                                                  nullable in the initial
                                                  schema

  `final_diagnosis`       `VARCHAR(200)`          Diagnosis recorded for
                                                  the session; nullable
                                                  before diagnosis

  `created_at`            `TIMESTAMP`             Session creation
                                                  timestamp
  -----------------------------------------------------------------------

A session can be created before a final diagnosis is available. The
backend should set `final_diagnosis` after running the inference engine.

### `session_facts`

Stores facts collected during a session as name-value pairs.

  Column         Type             Description
  -------------- ---------------- ------------------------------------
  `id`           `SERIAL`         Primary key
  `session_id`   `INT`            References `diagnosis_sessions.id`
  `fact_name`    `VARCHAR(100)`   Name of the observed fact
  `fact_value`   `VARCHAR(100)`   Value recorded for that fact

Example fact names and values:

  `fact_name`            `fact_value`
  ---------------------- --------------
  `wifi_connected`       `false`
  `router_powered_on`    `true`
  `internet_available`   `false`

These are illustrative examples, not seed records automatically inserted
by the schema.

**Design consideration:** If the application should store only one
current value for each fact in a session, consider adding a unique
constraint on `(session_id, fact_name)`.

### `rules`

Stores metadata for diagnostic rules.

  -----------------------------------------------------------------------
  Column                  Type                    Description
  ----------------------- ----------------------- -----------------------
  `id`                    `SERIAL`                Primary key

  `name`                  `VARCHAR(100)`          Rule name; required

  `description`           `TEXT`                  Human-readable
                                                  description

  `priority`              `INT`                   Rule priority; defaults
                                                  to `3`

  `enabled`               `BOOLEAN`               Whether the rule is
                                                  enabled; defaults to
                                                  `TRUE`
  -----------------------------------------------------------------------

The `priority` column can help the inference engine decide how to order
matching rules when multiple rules apply. The exact priority policy must
be implemented in the backend.

### `rule_conditions`

Stores the conditions that must be satisfied for a rule to match.

  Column             Type             Description
  ------------------ ---------------- ---------------------------------
  `id`               `SERIAL`         Primary key
  `rule_id`          `INT`            References `rules.id`
  `fact_name`        `VARCHAR(100)`   Fact that the condition checks
  `operator`         `VARCHAR(20)`    Comparison operator
  `expected_value`   `VARCHAR(100)`   Value expected by the condition

Illustrative condition:

``` text
fact_name      = wifi_connected
operator       = =
expected_value = false
```

The inference engine must define and validate the supported operators.
It should not execute arbitrary SQL or treat the operator field as raw
SQL.

### `rule_conclusions`

Stores the conclusions associated with a rule.

  Column               Type             Description
  -------------------- ---------------- ----------------------------
  `id`                 `SERIAL`         Primary key
  `rule_id`            `INT`            References `rules.id`
  `conclusion_name`    `VARCHAR(100)`   Name of the conclusion
  `conclusion_value`   `VARCHAR(200)`   Value or diagnostic result

A rule may have one or more conclusions, depending on how the
application is designed. The backend should define how multiple
conclusions are combined and returned.

### `matched_rules`

Records which rules matched during a diagnosis session.

  -------------------------------------------------------------------------
  Column                  Type                    Description
  ----------------------- ----------------------- -------------------------
  `id`                    `SERIAL`                Primary key

  `session_id`            `INT`                   References
                                                  `diagnosis_sessions.id`

  `rule_id`               `INT`                   References `rules.id`

  `matched_at`            `TIMESTAMP`             Time of the match;
                                                  defaults to the current
                                                  time
  -------------------------------------------------------------------------

This table supports the explanation facility. The backend can use the
matched-rule records to explain which rules contributed to the
diagnosis.

## 4. How the Tables Work Together

A typical diagnostic operation is intended to follow this sequence:

1.  Create a record in `diagnosis_sessions`.
2.  Save the user's observations in `session_facts`.
3.  Load enabled rules from `rules`.
4.  Load each rule's conditions from `rule_conditions`.
5.  Compare those conditions with the session's facts.
6.  For matching rules, read conclusions from `rule_conclusions`.
7.  Record matched rules in `matched_rules`.
8.  Save the selected final result in
    `diagnosis_sessions.final_diagnosis`.
9.  Return the diagnosis and an explanation through the API.

The inference engine and this workflow are application responsibilities;
the database schema alone does not perform rule evaluation.

## 5. SQL Schema

The table definitions are maintained in [`schema.sql`](schema.sql).
Apply that file to the intended PostgreSQL database when setting up the
project.

The initial schema uses `SERIAL` primary keys and `CURRENT_TIMESTAMP`
defaults. Foreign-key actions and nullability should be reviewed against
the intended lifecycle of users, sessions, facts, and rules.

## 6. Recommended Integrity Improvements

Before the project is considered complete, review these constraints
against the actual SQL file:

-   Make required foreign keys `NOT NULL` where orphan-like or
    incomplete records are not valid.
-   Add `UNIQUE (session_id, fact_name)` if a session should have only
    one value per fact.
-   Consider a check constraint for allowed priority values.
-   Consider indexes on frequently queried foreign-key columns such as
    `session_id` and `rule_id`.
-   Decide whether deleting a session or user should delete, retain, or
    anonymize associated history.
-   Keep rule conditions and conclusions linked to a valid rule; use
    `ON DELETE CASCADE` where deleting a rule should also remove its
    child rows.
-   Use transactions when saving a diagnosis and its matched-rule
    records so that related writes stay consistent.

These are recommendations, not claims that every constraint is already
present in `schema.sql`.

## 7. Security and Configuration

Do not store real database credentials in this documentation or commit
them to GitHub. Configure the backend through environment variables, for
example:

``` text
DB_URL=jdbc:postgresql://localhost:5432/networkdatabase
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
```

Grant the application database user only the permissions it needs. Do
not use the PostgreSQL superuser account for normal application access.

## 8. Test Data

This repository does not require a `seed.sql` file. The schema creates
the table structure; it does not automatically populate the knowledge
base with diagnostic rules. Rules and example facts can be added later
through controlled SQL statements, a backend service, or an
administrative interface.
