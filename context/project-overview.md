# Project Overview — ApexBank Agentic Platform

## About the Project
**ApexBank Agentic Platform** is a state-of-the-art, cloud-native banking platform designed to demonstrate the integration of Java/Spring Boot microservices with an autonomous AI agentic workflow built using the **Google Antigravity SDK (ADK)** and the **Gemini API**. 

The platform serves two primary user personas:
1. **Bank Compliance & Fraud Officers**: An internal dashboard allows officers to monitor high-risk transactions, run automated compliance audits, and collaborate with the **Antigravity compliance agent** to investigate suspicious accounts.
2. **Retail Banking Customers**: A modern customer dashboard where users can view account history, transfer funds, and consult the **Antigravity personal finance agent** for real-time transaction analysis and savings advice.

The entire system is orchestratred across dockerized microservices, backed by robust database engines, monitored using structured telemetry, and secured with production-grade enterprise practices.

---

## The Problem It Solves
Modern banking systems face immense pressure to detect fraud in real-time, maintain rigorous regulatory compliance, and deliver high-touch customer support. Traditional rules-based banking systems are fragile and struggle to adapt to complex fraud patterns or provide personalized guidance.

**ApexBank Agentic Platform** solves this by inserting **Agentic AI** directly into the banking architecture. Rather than relying on simple static rules, the platform leverages Gemini-powered agents equipped with specific domain skills (Fraud Detection, Credit Risk, Portfolio Analysis) to query live banking data via REST APIs, reason through complex customer transaction logs, and generate actionable risk dossiers or financial plans.

---

## Core Pages & Layout

```
/                    → Landing & Login Page (Federated Auth)
/dashboard           → Compliance Officer Portal / Overview & Telemetry
/accounts            → Customer Accounts & Ledger Management
/audit               → Compliance Auditing Center (Interactive Agent Playground)
/audit/[accountId]   → In-depth Account Investigation dossier & Agent Reasoning logs
/profile             → Customer Profile and Credit Risk Profile
```

### Navigation Structure
A sleek, responsive top navbar for quick access to key operational centers:
```
[Logo] ApexBank    Dashboard    Accounts    Audit Center    Profile
```

---

## Core Operational Flows

### 1. Account Onboarding & Credit Risk Profiling
- **User Flow**: A customer registers, enters profile details, and uploads income documentation (PDF/DOCX).
- **Agentic Workflow**: The credit risk subagent parses the documentation, queries the customer credit history, calculates a risk score, and recommends an initial deposit limit and credit limit.
- **Java Microservice**: The **Account Service** stores the profile and triggers the credit approval workflow.

### 2. Transaction Auditing & Fraud Analysis
- **User Flow**: A compliance officer opens a high-risk alert on the dashboard.
- **Agentic Workflow**:
  - The main **ApexBank Agent** invokes a specialized **Fraud Analyst Subagent**.
  - The subagent uses custom tools to pull transaction history from the **Transaction Service**.
  - It reviews transaction volume, geographic hops, velocity, and merchant risk profiles.
  - It generates a detailed fraud risk dossier showing a risk score, reasoning path, and recommended action (e.g., "Freeze Account", "Flag for manual review").
- **Java Microservice**: The **Transaction Service** exposes REST APIs for historical query, and the **Fraud Service** stores the resulting AI audit reports.

### 3. Automated Compliance Reporting
- **User Flow**: A compliance manager schedules a daily audit report.
- **Agentic Workflow**: The agent runs a cron schedule using the **periodic trigger** mechanism, scans all flagged transactions, drafts standard Suspicious Activity Reports (SARs) in Markdown, and saves them to the compliance vault.
- **Java Microservice**: The **Compliance Service** records the generated SARs and exposes them via authenticated endpoints.

---

## Features In Scope
- **Spring Boot Core Ledger Microservices**: Fully-functional REST endpoints for account creation, balance checks, deposits, withdrawals, and bank-to-bank transfers.
- **Google Antigravity SDK Agent (Python)**:
  - Integration of Gemini models for reasoning and tool-calling.
  - Filesystem-based skill loading, importing the standard agent skills (`architect`, `scope`, `develop`, `check`, `test`, `audit`, `debug`, `document`, `sync`) to drive system behavior.
  - Multi-agent orchestration (Main Supervisor agent delegating tasks to Fraud, Compliance, and Credit subagents).
- **Interactive Officer Chat & Audit Console**:
  - React/Next.js interface displaying real-time agent "thoughts" (reasoning tokens) and execution logs.
  - Beautiful transaction visualization charts.
- **Unit & Integration Testing**:
  - Mockito and JUnit 5 tests for Spring Boot microservices.
  - Pytest suite with mock connections for the Antigravity Agent.
- **Cloud-Native Infrastructure**:
  - Multi-container Docker Compose setup for local development.
  - GitHub Actions CI/CD pipeline template configuration.
  - Health checks and production support endpoints (Actuator for Spring Boot).

---

## Features Out of Scope
- **Real Payment Gateways**: All transfers and banking transactions are processed against mock ledgers.
- **Real Credit Agency API Integration**: External credit histories are simulated rather than queried from actual bureaus.
- **Mobile Native Applications**: Web-only responsive application (no iOS or Android native packages).
- **Multi-Currency Forex Settlement**: USD-only currency transactions.

---

## Target Audience
- **Interview Panels & Hiring Managers**: Demonstrates enterprise Java/Spring Boot experience coupled with cutting-edge Agentic AI engineering.
- **System Architects**: Illustrates how to safely plug LLM-based agentic workflows into legacy transactional systems using REST APIs and secure agent sandboxes.

---

## Success Criteria
1. **Zero-Trust API Interaction**: The AI agent cannot execute transactions without explicit compliance-approved credentials.
2. **Low Latency Reasoning**: The agent streams thoughts and responses under 3 seconds using the Gemini 2.0 Flash model.
3. **95%+ Test Coverage**: Core microservice business logic is fully protected by automated unit and integration tests.
4. **Seamless Local Run**: The entire project builds and runs using a single `docker compose up` command.
5. **Interactive Skill Execution**: The agent successfully utilizes loaded skills (e.g. `architect` to draft loan restructures, `scope` to bound feature tasks, and `audit` to evaluate compliance metrics).
