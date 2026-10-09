# Network Troubleshooting Expert System

A rule-based expert system designed to help users identify common
network connectivity problems. The project combines a React frontend, a
Java Spring Boot REST API, and a PostgreSQL database.

> **Project status:** The database schema and initial repository
> structure have been started. Backend inference logic, API endpoints,
> and frontend screens are planned implementation work unless otherwise
> documented in the source code.

## Table of Contents

-   [Overview](#overview)
-   [Objectives](#objectives)
-   [How the Expert System Works](#how-the-expert-system-works)
-   [Technology Stack](#technology-stack)
-   [System Architecture](#system-architecture)
-   [Repository Structure](#repository-structure)
-   [Database Overview](#database-overview)
-   [Planned API Endpoints](#planned-api-endpoints)
-   [Prerequisites](#prerequisites)
-   [Configuration](#configuration)
-   [Running the Project](#running-the-project)
-   [Security Notes](#security-notes)
-   [Future Improvements](#future-improvements)

## Overview

Network Troubleshooting Expert System guides a user through questions
about a network issue, records the answers as facts, evaluates a
knowledge base of rules, and returns a possible diagnosis with an
explanation.

Examples of issues the system may help investigate include: - No
internet connection. - A connected device that cannot access websites. -
A possible Wi-Fi connectivity problem. - A possible router, DNS, or IP
configuration issue.

The diagnoses and rules should be treated as troubleshooting guidance,
not as a guarantee that a particular fault has been identified.

## Objectives

-   Demonstrate the core concepts of a rule-based expert system.
-   Separate the knowledge base from the inference engine.
-   Store rules, conditions, conclusions, user facts, and diagnosis
    history in PostgreSQL.
-   Expose the system through REST APIs.
-   Provide a user-friendly React interface.
-   Explain which rules contributed to a diagnosis.

## How the Expert System Works

The intended diagnostic workflow is:

1.  **Collect facts:** The user answers questions about the current
    network symptoms.
2.  **Create a session:** The backend records a diagnosis session.
3.  **Evaluate rules:** The inference engine loads enabled rules and
    their conditions from PostgreSQL.
4.  **Match rules:** The engine compares the submitted facts with the
    conditions of each rule.
5.  **Produce a conclusion:** Conclusions from matching rules are used
    to determine a possible diagnosis.
6.  **Explain the result:** The system records matching rules and
    returns a human-readable explanation.

### Core concepts

-   **Facts:** Observations supplied by the user, such as
    `internet_available = false`.
-   **Knowledge base:** The stored rules, conditions, and conclusions.
-   **Inference engine:** Java logic that evaluates facts against the
    knowledge base.
-   **Rule matching:** A rule matches when its required conditions are
    satisfied.
-   **Explanation facility:** Information about the matched rules that
    supports the result.

This design makes the project more than a collection of UI conditions:
diagnostic knowledge is represented as data in the database, while a
separate backend component evaluates it.

## Technology Stack

  -----------------------------------------------------------------------
  Layer                   Technology              Responsibility
  ----------------------- ----------------------- -----------------------
  Frontend                React + Vite            User interface and
                                                  symptom collection

  Backend                 Java + Spring Boot      REST API, application
                                                  services, and inference
                                                  engine

  Database                PostgreSQL              Users, sessions, facts,
                                                  rules, conditions,
                                                  conclusions, and
                                                  matched rules

  API style               REST / JSON             Communication between
                                                  frontend and backend

  Version control         Git + GitHub            Source control and
                                                  collaboration
  -----------------------------------------------------------------------

## System Architecture

``` text
User
 |
 v
React Frontend
 |
 | HTTP / JSON
 v
Spring Boot REST API
 |
 v
Diagnosis Service
 |
 +--> Inference Engine ---- reads ----> Knowledge Base
 |                                    (rules and conditions)
 |
 +--> Session / Fact Services
 |
 v
PostgreSQL Database
 |
 v
Diagnosis + Explanation returned to the frontend
```

The frontend should not contain the authoritative diagnostic rules.
Those rules should be stored in PostgreSQL and evaluated by the backend.

## Repository Structure

``` text
Network-Troubleshooting-Expert-System/
├── database/
│   ├── schema.sql
│   └── README.md
├── backend/
│   ├── pom.xml
│   └── src/
├── frontend/
│   ├── package.json
│   └── src/
├── docs/
│   ├── erd.png
│   ├── system-architecture.png
│   └── project-documentation.md
├── .gitignore
└── README.md
```

Some directories or files may be added as development progresses. Git
does not track empty directories.

## Database Overview

The initial database schema defines seven tables:

  -----------------------------------------------------------------------
  Table                               Purpose
  ----------------------------------- -----------------------------------
  `users`                             Stores user account details

  `diagnosis_sessions`                Stores each troubleshooting session
                                      and its final diagnosis

  `session_facts`                     Stores facts collected during a
                                      session

  `rules`                             Stores rule metadata, priority, and
                                      enabled status

  `rule_conditions`                   Stores the conditions required for
                                      each rule

  `rule_conclusions`                  Stores conclusions produced by a
                                      rule

  `matched_rules`                     Records rules that matched during a
                                      session
  -----------------------------------------------------------------------

See [`database/README.md`](database/README.md) for the database design
and relationships, and [`database/schema.sql`](database/schema.sql) for
the SQL schema.

## Planned API Endpoints

These are proposed endpoints for the implementation; they should not be
considered available until the corresponding controllers and services
have been implemented.

  --------------------------------------------------------------------------------------------
  Method                  Endpoint                                     Intended purpose
  ----------------------- -------------------------------------------- -----------------------
  `GET`                   `/api/rules`                                 List available
                                                                       diagnostic rules

  `POST`                  `/api/diagnosis-sessions`                    Create a diagnosis
                                                                       session

  `POST`                  `/api/diagnosis-sessions/{id}/facts`         Add facts to a session

  `POST`                  `/api/diagnosis-sessions/{id}/diagnose`      Run the inference
                                                                       engine

  `GET`                   `/api/diagnosis-sessions/{id}`               Retrieve session
                                                                       details

  `GET`                   `/api/diagnosis-sessions/{id}/explanation`   Retrieve the diagnosis
                                                                       explanation
  --------------------------------------------------------------------------------------------

## Prerequisites

Install the following tools before running the complete application:

-   Java Development Kit (JDK) compatible with the backend
    configuration.
-   Maven, or use the Maven Wrapper if it is included.
-   PostgreSQL.
-   Node.js and npm.
-   Git.

## Configuration

Configure the backend using environment variables rather than committing
database credentials.

Example values for a local PostgreSQL instance:

``` text
DB_URL=jdbc:postgresql://localhost:5432/networkdatabase
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
```

The backend's `application.properties` can reference these variables:

``` properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
```

Ensure that the database and tables exist before starting the backend.
The `validate` setting checks the mapping against the existing schema;
it does not create the schema.

## Running the Project

The exact commands depend on which components have been implemented.

### Database

1.  Create the PostgreSQL database and a user with appropriate
    permissions.
2.  Apply `database/schema.sql` to the intended database.
3.  Review [`database/README.md`](database/README.md) for the schema
    details.

### Backend

Once the Spring Boot application has been implemented and configured:

``` bash
cd backend
./mvnw spring-boot:run
```

If the repository does not contain the Maven Wrapper, use an installed
Maven version:

``` bash
mvn spring-boot:run
```

### Frontend

Once the React application has been implemented:

``` bash
cd frontend
npm install
npm run dev
```

These commands describe the intended workflow; they will work only when
the corresponding Maven and npm project files are present and
configured.

## Security Notes

-   Never commit real database passwords, access tokens, or other
    secrets.
-   Keep local environment files out of version control.
-   Use a restricted database account for the application instead of a
    PostgreSQL superuser.
-   Validate input received by the REST API.
-   If user accounts are implemented, store passwords only with a
    suitable password-hashing mechanism.

## Future Improvements

-   Implement the inference engine and rule-matching service.
-   Add representative rules and a safe way to manage the knowledge
    base.
-   Return matched rules and explanations with each diagnosis.
-   Add validation for facts, rule operators, and priorities.
-   Add backend unit and integration tests.
-   Build the React diagnostic flow and results page.
-   Add an ERD and architecture diagram under `docs/`.
-   Add authentication and authorization if required by the project
    scope.

## License

No license has been specified yet. Add a `LICENSE` file before
presenting the project as open source under a particular license.
