# Code Standards — ApexBank Agentic Platform

## 1. Java / Spring Boot Microservice Standards
All Java development must follow clean-coding practices, targeting high-performance cloud-native runtime environments.

### Core Configuration
- **Java Version**: Java 21 (utilize modern features such as Records, Pattern Matching, and Virtual Threads where applicable).
- **Spring Boot Version**: 3.x
- **Build Tool**: Maven

### Package Structure
Use a consistent domain-driven package layout:
```
com.apexbank.platform.<service_name>
├── config          # Security, Web, and Bean configuration
├── controller      # REST Controllers (exposing API endpoints)
├── dto             # Request/Response Data Transfer Objects
├── model           # JPA Entity models
├── repository      # Spring Data JPA repositories
├── service         # Core business logic interfaces and implementations
└── exception       # Custom exception classes and global handler
```

### REST API Standards
- **Naming Conventions**: Endpoints must be pluralized nouns (e.g. `/api/accounts`, `/api/transactions`). Use kebab-case.
- **HTTP Methods**: 
  - `GET`: Retrieve resource. Safe and idempotent.
  - `POST`: Create new resource.
  - `PUT`: Idempotent resource replacement.
  - `DELETE`: Remove resource.
- **HTTP Status Codes**:
  - `200 OK`: Successful retrieval or action.
  - `201 Created`: Resource successfully created.
  - `400 Bad Request`: Client validation error (e.g., negative transfer amount).
  - `401 Unauthorized`: Missing or invalid auth credentials.
  - `403 Forbidden`: Authenticated, but lacking role permissions (e.g. customer attempting manual limit change).
  - `404 Not Found`: Resource does not exist.
  - `500 Internal Server Error`: Uncaught exceptions.
- **Error Responses**: All exceptions must map to a standardized error JSON:
  ```json
  {
    "timestamp": "2026-07-23T21:17:16Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Insufficient funds in checking account",
    "path": "/api/transactions/transfer"
  }
  ```

### Unit & Integration Testing
- **Framework**: JUnit 5, Mockito.
- **Coverage**: Aim for 90%+ code coverage on service and controller logic.
- **Assertions**: Prefer AssertJ style for readable comparisons (`assertThat(account.getBalance()).isEqualByComparingTo("100.00")`).

---

## 2. Python / Antigravity Agent Standards
Python components must be clean, typed, and structured for reliable AI execution.

### Environment & Styles
- **Python Version**: 3.11+
- **Formatting**: Format all python files with `black` and lint with `flake8`.
- **Typing**: Use PEP 484 type hints for all function signatures and return types.

### Google Antigravity SDK Best Practices
- **Lifecycle Management**: Always manage agent resources using async context managers:
  ```python
  async with Agent(config) as agent:
      response = await agent.chat(prompt)
  ```
- **Error Handling**: Use the `recover` skill strategies. Intercept exceptions, print diagnostic logs, and report failure states gracefully rather than terminating the worker.
- **Structured Output**: Always enforce Pydantic parsing when returning assessments to the backend. Avoid parsing plain LLM text strings:
  ```python
  from pydantic import BaseModel, Field

  class FraudAssessment(BaseModel):
      risk_score: int = Field(..., ge=0, le=100)
      reasoning: str
      recommended_action: str
  ```

---

## 3. Frontend Standards (React/TypeScript)
- **TypeScript**: Strict type checking enabled (`strict: true` in `tsconfig.json`). No `any` type usage.
- **Components**: Functional components only. Hooks managed in separate `hooks/` files.
- **State Management**: Context API for global session states; local state for interactive elements.
- **Styling**: Vanilla CSS with predefined root tokens (colors, font-sizes) to ensure dynamic styling (e.g. glassmorphism elements, micro-animations on actions).

---

## 4. SQL & Database Migrations
- **Naming Conventions**: Table names and column names must be lowercase `snake_case` (e.g. `account_type`).
- **Indexes**: Explicitly create indexes on columns that are queried frequently (e.g. `transactions.account_id`, `compliance_reports.account_id`).
- **Constraints**: Enforce foreign keys, unique email columns, and non-nullable status fields.
