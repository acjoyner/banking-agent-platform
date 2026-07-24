# ApexBank Agentic Platform — Project Memory & Decisions Log

This document serves as the absolute state reference and memory for subsequent developers or agent sessions. It details the setup, architectural rules, operational controls, and lessons learned.

---

## 1. Project Directory & Repository
*   **Local Directory:** `/Users/anthonyjoyner/Documents/Projects/banking-agent-platform`
*   **Git Remote GitHub:** `https://github.com/acjoyner/banking-agent-platform.git`
*   **Branch:** `master`

---

## 2. Technical Environment & Version Quirks (CRITICAL)
*   **Active Java Version:** The local environment runs **Java 17 (OpenJDK 17.0.18)**. Compilation targeting Java 21 fails. All Spring Boot `pom.xml` configurations must target `<java.version>17</java.version>`.
*   **Active Python Version:** Runs **Python 3.12.7** in the virtual environment located at `/Users/anthonyjoyner/Documents/Projects/banking-agent-platform/venv/`.
*   **Active Node/React Version:** Next.js **16.2.11**, React **19.2.4**, Tailwind CSS **v4**.
*   **OpenCode CLI Model:** Configured `moonshot/kimi-k3` using the OpenAI-compatible Moonshot AI API (`https://api.moonshot.cn/v1`) driven by the environment variable `${MOONSHOT_API_KEY}`.

---

## 3. Microservices Architecture Map

| Service | Directory | Local Port | Database Target | Functionality |
| :--- | :--- | :--- | :--- | :--- |
| **PostgreSQL** | Docker | `5432` | `postgres` | Central ledger database instance |
| **Account Service** | `backend/account-service` | `8081` | `apexbank_accounts` | Manages account types, owner profile, balances, limits |
| **Transaction Service** | `backend/transaction-service` | `8082` | `apexbank_transactions` | Performs ledger adjustments. Balance updates routed via REST to Account Service. Automatically flags transactions > $10,000 |
| **Compliance Service** | `backend/compliance-service` | `8083` | `apexbank_compliance` | Vault for storing AI-generated Suspicious Activity Reports (SARs) and compliance scores |
| **Python Agent** | `agent/` | CLI-triggered | N/A | Antigravity AI Investigator loaded with custom `compliance-audit` skill instructions |
| **Frontend UI** | `frontend/` | `3000` | N/A | Next.js App Router workspace featuring glassmorphic ledger views, transaction simulators, and investigator stream consoles |

---

## 4. Architectural Rules & Logic Boundaries
*   **Transaction Balance Adjustment:** Transactions are sent to `transaction-service`. The service calls `account-service` balance endpoint: `PUT /api/accounts/{id}/balance?amount={amount}`. Depositing amounts are positive (+), withdrawals/transfers are negative (-).
*   **Compliance Risk Score Formula:**
    *   Base Score: `10`
    *   High-Value Flag (> $10,000 transaction): `+30`
    *   Velocity Violation (> 3 transactions in 5 min): `+20`
    *   Geo-hop Violation (impossible travel locations): `+45`
    *   *Cap at 100.*
*   **Hold Directives:** Risk score >= 80 auto-applies `AUTO_FREEZE`. Risk score >= 50 applies `MONITOR`. Else `NONE`.

---

## 5. Operations & Execution Reference

### Running the Services via Docker
```bash
# In the root directory: build and run all microservices, postgres and nextjs app
docker-compose up --build
```

### Running Microservices Locally (Development)
Ensure PostgreSQL is active on port `5432` (or adjust properties in `application.yml`).
```bash
# Terminal 1: Account Service
cd backend/account-service && mvn spring-boot:run

# Terminal 2: Transaction Service
cd backend/transaction-service && mvn spring-boot:run

# Terminal 3: Compliance Service
cd backend/compliance-service && mvn spring-boot:run
```

### Running and Testing the Python Agent
The virtual environment is ready in `venv/`.
```bash
# Run pytest verification suite
./venv/bin/python -m pytest agent/test_agent.py

# Execute Investigator audit scan on an account
./venv/bin/python -m agent.main --account ACC-55667788

# Operational CLI wrapper tool
./agent/cli.py --run-audit --account ACC-55667788
```

### Running Frontend Dashboard
```bash
cd frontend
npm run dev
```
Accessible at `http://localhost:3000`.

---

## 6. Styling System (Imprint Baseline)
Located in `context/ui-registry.md`. Premium dark mode (`#0B0F19` base) with glassmorphic cards (`.glass-panel`) and emerald green status indicator lights (`#10B981`).
