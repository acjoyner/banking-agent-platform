# Progress Tracker — ApexBank Agentic Platform

Use this file to monitor the execution of the build plan. Check off items as they are completed and fully tested.

---

## Phase 1 — Spring Boot Microservices
- [x] **01 Account Service**
  - [x] Implement JPA repository and model for `accounts` database schema.
  - [x] Write REST controller endpoints (create, fetch details, update limits).
  - [x] Write JUnit 5 unit tests for checking balance bounds and validations.
- [x] **02 Transaction Service**
  - [x] Implement JPA repository and model for transaction history log.
  - [x] Write endpoints to submit deposits, withdrawals, and bank transfers.
  - [x] Implement safety trigger: automatically flag any transaction above $10,000.
  - [x] Write JUnit 5/Mockito tests verifying balance adjustments and insufficient funds states.
- [x] **03 Compliance Service**
  - [x] Create repository and model for compliance reports.
  - [x] Write REST endpoint to submit AI audit findings and markdown reports.
  - [x] Implement integration verification between the three services.

---

## Phase 2 — Python Antigravity Agent & Skills Integration
- [x] **04 SDK Environment & Config**
  - [x] Create python virtual environment and verify package installations (`google-antigravity`, `httpx`).
  - [x] Setup `.env` configuration file with Gemini API Key.
  - [x] Setup `LocalAgentConfig` to resolve custom skill folders.
- [x] **05 Custom Agent Tools**
  - [x] Implement python helper functions to consume Spring Boot REST API endpoints.
  - [x] Define annotations, descriptions, and typings for tool-calling registration.
- [x] **06 Agent Skill Integration**
  - [x] Copy and configure skill folders (`architect`, `scope`, `develop`, `check`, `test`, `audit`, `debug`, `document`, `sync`) from `jsmastery-pro/skills`.
  - [x] Implement specialized `compliance-audit` skill instructions.
- [x] **07 Pytest Suite**
  - [x] Write tests verifying tool selection by the agent under mocked response rules.
  - [x] Write tests verifying error handling (using the `debug` and recovery skill logic).

---

## Phase 3 — Frontend Dashboard (Next.js/React)
- [x] **08 Dashboard Landing & Account Portal**
  - [x] Build layout, navbar, checking/savings overview cards.
  - [x] Build transaction history tables with sorting/filtering controls.
  - [x] Implement customer advisory chat widget.
- [x] **09 Compliance Audit Console**
  - [x] Build compliance dashboard listing flagged accounts.
  - [x] Implement live audit view that streams agent thought blocks and action steps.
  - [x] Wire buttons to submit manual hold/dismiss orders back to Spring Boot.

---

## Phase 4 — Cloud-Native & DevOps
- [ ] **10 Dockerization**
  - [ ] Create multi-stage `Dockerfile` for Spring Boot microservices.
  - [ ] Create `Dockerfile` for Python agent.
  - [ ] Verify local database connectivity using `docker-compose.yml`.
- [ ] **11 CI/CD Pipelines**
  - [ ] Define GitHub Actions pipeline to run maven tests, python lints, and react build checks.
- [ ] **12 Production Support & Health Checks**
  - [ ] Enable Spring Actuator health monitoring.
  - [ ] Implement structured JSON logging formats.
  - [ ] Implement CLI trigger script `/agent/cli.py --run-audit` for operations staff.
