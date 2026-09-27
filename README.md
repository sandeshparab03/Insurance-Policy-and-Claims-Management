# Insurance Policy & Claims Management System

A full-stack CRUD application: Java Spring Boot (REST API) + MySQL (database) + HTML/CSS/JavaScript (frontend).

## What this project does
- Create, view, search, and delete insurance **policies**.
- File **claims** against a policy, and approve/reject/track their status.
- Enforces one real business rule: **you cannot file a claim on an expired policy** — this is what makes it more than a generic to-do-list clone.

## Tech stack
- **Backend:** Java 17, Spring Boot 3.2, Spring Data JPA (Hibernate), Spring Validation
- **Database:** MySQL
- **Frontend:** Plain HTML, CSS, JavaScript (fetch API) — no framework needed
- **Build tool:** Maven

## Project structure
```
insurance-project/
├── backend/                          <- Spring Boot project (open this folder in IntelliJ/VS Code)
│   ├── pom.xml                       <- Maven dependencies
│   └── src/main/
│       ├── java/com/insurance/policyclaims/
│       │   ├── PolicyClaimsApplication.java   <- main() entry point
│       │   ├── model/                <- Policy.java, Claim.java, ClaimStatus.java (database tables)
│       │   ├── repository/           <- database access (Spring Data JPA)
│       │   ├── service/              <- business logic (expiry rule lives here)
│       │   ├── controller/           <- REST API endpoints
│       │   ├── exception/            <- custom errors + global handler
│       │   └── config/               <- CORS setup
│       └── resources/
│           └── application.properties <- database connection settings
└── frontend/
    ├── index.html
    ├── style.css
    └── script.js
```


