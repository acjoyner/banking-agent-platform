# Build Plan — ApexBank Agentic Platform

## Core Planning Principle
Mock data first, verified visually and programmatically. Build the backend REST endpoints and test their correctness. Next, build the Python agent and hook it to the REST endpoints. Finally, wrap it in a premium user interface with full streaming capabilities. Every component must be verified with automated unit and integration tests.

---

## Phase 1 — Spring Boot Microservices (Core Ledger)

### 01 Account Service
Build the core user account storage, profile fields, and credit profiles.
- **REST Endpoints**:
  - `POST /api/accounts` — Create checkings/savings account.
  - `GET /api/accounts/{id}` — Fetch account balances, limits, and risk levels.
  - `PUT /api/accounts/{id}/limits` — Update credit/deposit limits (called by Credit Risk Agent).
- **Unit Tests**:
  - JUnit 5 test covering business rules (e.g. balances cannot go negative, deposit limit updates must be positive).
  - Mockito-based service tier testing.

### 02 Transaction Service
Manage transactional ledger entries, transfer mechanics, and simple safety triggers.
- **REST Endpoints**:
  - `POST /api/transactions` — Submit transaction (deposit, withdrawal, transfer). Checks balance, flags transaction as 'FLAGGED' if it exceeds $10,000.
  - `GET /api/transactions/account/{accountId}` — Fetch historical transactions (with pagination).
- **Unit Tests**:
  - Assert transaction statuses ('COMPLETED', 'FLAGGED') based on transaction rules.
  - Test transfer failure states (insufficient funds, invalid target account).

### 03 Compliance Service
Audit reporting engine to store generated Suspicious Activity Reports (SARs) and agent audit logs.
- **REST Endpoints**:
  - `POST /api/compliance/reports` — Create compliance audit report with JSON metadata and Markdown SAR.
  - `GET /api/compliance/reports/account/{accountId}` — Retrieve historical audit logs.
- **Unit Tests**:
  - Validate PDF/Markdown storage and status transition logic.

---

## Phase 2 — Python Antigravity Agent & Skills Integration

### 04 SDK Environment & Config
Install necessary Python dependencies and configure API connection details.
- **Tasks**:
  - Create `requirements.txt` with `google-antigravity`, `pydantic`, `httpx`, `pytest`, `python-dotenv`.
  - Create `.env.template` requesting `GEMINI_API_KEY`, `SPRING_API_URL`.
  - Implement `agent/config.py` defining `LocalAgentConfig` with `skills_paths` pointing to the `/skills` directory.

### 05 Custom Agent Tools
Expose Spring Boot microservice REST endpoints to the Antigravity Agent as tools.
- **Logic**:
  - Implement Python helper functions using `httpx` to call:
    - `get_account_details(account_id: str)`
    - `get_transaction_history(account_id: str)`
    - `submit_compliance_report(account_id: str, risk_score: int, reasoning: str, report: str)`
  - Use Python function annotations and docstrings so the agent can discover these functions as native tools.

### 06 Agent Skill Integration (architect, scope, develop, check, test, audit, debug, document, sync)
Load and adapt filesystem-based skills from `jsmastery-pro/skills`.
- **Logic**:
  - Verify that skills like `architect` (system changes), `scope` (bounding tasks), and `debug` (resolving execution path errors) are correctly resolved from the filesystem.
  - Add a custom banking skill: `compliance-audit`. The skill instructs the agent on how to run transaction velocity analysis and structures SAR reports.

### 07 Pytest Suite
Create integration tests validating agent logic.
- **Tests**:
  - Use `pytest-asyncio` to mock Gemini response endpoints and test that the agent selects the correct custom tools when prompted.
  - Test that the agent handles API failure states gracefully (testing the `recover` skill).

---

## Phase 3 — Frontend Dashboard (Next.js/React)

### 08 Dashboard Landing & Account Portal
Create a modern customer interface.
- **UI**:
  - User balance summary cards (Checking, Savings, Credit).
  - Transaction history tables with search and sorting.
  - Integrated customer chatbot widget in the bottom-right corner.
- **Logic**:
  - Fetch account ledger details via REST from the Spring Boot Accounts/Transaction APIs.

### 09 Compliance Audit Console
The command center for Bank Compliance Officers.
- **UI**:
  - A table of accounts with alerts (marked as 'FLAGGED' or 'HIGH' risk).
  - Click-through detail page showing account history.
  - **Live Audit Window**: Interactive terminal view that streams agent thoughts (reasoning process) and tool execution logs.
- **Logic**:
  - Open a SSE (Server-Sent Events) or WebSocket connection to stream `agent.chat()` output tokens and thoughts in real time.
  - Buttons to "Approve Hold" or "Dismiss Alert", calling Compliance REST endpoints.

---

## Phase 4 — Cloud-Native & DevOps

### 10 Dockerization
Ensure all platform parts are containerized.
- **Tasks**:
  - Create multi-stage `Dockerfile` for the Spring Boot application (using OpenJDK 21 slim).
  - Create `Dockerfile` for the Python Antigravity Agent.
  - Create `docker-compose.yml` defining services: `postgres`, `spring-app`, `python-agent`, `frontend-dashboard`, along with networks and environment variables.

### 11 CI/CD Pipelines
Build and test automation.
- **Tasks**:
  - Create `.github/workflows/ci.yml`.
  - Define parallel test pipelines:
    - **Java Build**: Maven clean verify (running JUnit 5).
    - **Python Build**: Install requirements, run flake8/black lint, run pytest.
    - **Frontend Build**: npm run lint, npm run build.

### 12 Production Support & Health Checks
Build telemetry and support handles.
- **Tasks**:
  - Configure Spring Boot Actuator endpoints: `/actuator/health`, `/actuator/metrics`, `/actuator/info`.
  - Set up structured logging in JSON for easier parsing by standard log aggregators (ELK/Splunk).
  - Implement a manual trigger script `/agent/cli.py --run-audit` for on-call operations to run manual batch audits.
