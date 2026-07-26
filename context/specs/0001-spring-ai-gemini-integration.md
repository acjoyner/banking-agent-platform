# 0001. Spring AI Gemini Integration

**Date**: 2026-07-24
**Status**: Proposed

## Summary
This decision adds the Spring AI model starter directly to the compliance service microservice. It allows the Java backend to run automated compliance audits and draft suspicious activity reports using the Gemini model. This changes the design from a local Python script trigger to a backend service integration.

## Context
The current platform relies on an external Python script to pull transaction logs and invoke the Gemini API. This setup makes it hard to run automated audits in real time when transactions are flagged by the system.
Moving the compliance investigator logic directly into the Java backend creates a self contained system. The compliance service can now listen to internal events or REST calls and immediately trigger the Gemini API using Spring AI. This simplifies the infrastructure and ensures that all compliance reports are stored securely in the database.

## Requirements

**User stories**:
* As a compliance officer, I want the system to run an automated audit on flagged transactions so that we can detect fraud immediately.
* As a compliance officer, I want to manually trigger an audit from the dashboard so that I can inspect any account on demand.

**Acceptance criteria**:
* **AC-1**: The system triggers an automatic audit immediately when a transaction is flagged.
* **AC-2**: The system triggers a manual audit when requested via the compliance REST endpoint.
* **AC-3**: The compliance report contains a risk score calculated using the weighted scoring rules.
* **AC-4**: The compliance report contains a markdown formatted suspicious activity report drafted by Gemini.
* **AC-5**: If the Gemini API is offline, the system falls back to a programmatic audit report using the weighted scoring logic.
* **AC-6**: The audit report stores the source transaction ID and the trigger type (values: "AUTOMATIC" or "MANUAL").

## Options considered

### Option 1: Spring AI Starter
This option adds `spring-ai-starter-model-google-genai` to the compliance service. It uses the Spring milestone repository to download the starter.

**Pros**:
* Fits the daily focus on Spring AI.
* Integrates with Spring configurations.

**Cons**:
* Requires configuring the Spring Milestone repository in the Maven configuration.

### Option 2: Custom Java REST Client
This option uses standard `RestTemplate` to call the Google AI Studio Gemini API endpoint directly.

**Pros**:
* Avoids extra dependencies.
* Works instantly without milestones configurations.

**Cons**:
* Does not use Spring AI structures.

## Decision
**Chosen option**: Option 1: Spring AI Starter
We will integrate the Spring AI model starter with the `gemini-1.5-flash` model.

**Implementation skills**: `develop` (`builtin`, `skills/develop/`) · `test` (`builtin`, `skills/test/`)

## Rationale
Using the Spring AI Google GenAI model starter aligns the codebase with your learning goals. It integrates with Spring Boot configurations, making the service modular. It handles prompt structures and response binding cleanly.

## Feature design

**Data model sketch**:
The `ComplianceReport` entity in the compliance service is extended with two fields:
* `flaggedTransactionId` (VARCHAR(50), nullable): The ID of the transaction that triggered this audit.
* `triggerType` (VARCHAR(20), required): The type of the trigger, either "AUTOMATIC" or "MANUAL".

**State transitions**:
No state transitions exist for this entity. It is read only once written to the database.

**API surface**:
| Endpoint | Method | Key inputs | Key outputs | Auth | Key errors |
|---|---|---|---|---|---|
| `/api/compliance/reports/audit/{accountId}` | POST | `transactionId` (string, opt), `triggerType` (string, opt) | `id` (string), `riskScore` (int), `reasoning` (string), `actionsTaken` (string), `draftedSar` (string) | None (internal service communication) | 400 Bad Request, 404 Account Not Found, 500 Internal Error |

**Value sourcing**:
| Action | Value produced / displayed | Source |
|---|---|---|
| Run Audit | `id` | System generated (UUID format COMP-XXXXXXXX) |
| Run Audit | `accountId` | Path variable from request |
| Run Audit | `flaggedTransactionId` | Query parameter from request |
| Run Audit | `triggerType` | Query parameter from request (defaults to "MANUAL") |
| Run Audit | `riskScore` | JSON response from Gemini model |
| Run Audit | `reasoning` | JSON response from Gemini model |
| Run Audit | `actionsTaken` | Derived from the calculated risk score (AUTO_FREEZE if risk >= 80, MONITOR if risk >= 50, else NONE) |
| Run Audit | `draftedSar` | Markdown response text from Gemini model |
| Run Audit | `createdAt` | System generated (LocalDateTime.now() on persist) |

**Key invariants**:
* The risk score must be between 0 and 100 inclusive.
* If the risk score is 80 or above, the action taken must be set to `AUTO_FREEZE`.
* If the risk score is between 50 and 79 inclusive, the action taken must be set to `MONITOR`.

**Security model**:
These endpoints are internal. They communicate inside the secure virtual network.

**Configuration required**:
* `SPRING_AI_GOOGLE_GENAI_API_KEY`: The Google AI Studio API key used to authenticate with Gemini.
* `ACCOUNT_SERVICE_URL`: The URL to fetch account details from the Accounts Service.
* `TRANSACTION_SERVICE_URL`: The URL to fetch ledger logs from the Transaction Service.

**Critical test scenarios**:
* Happy path: Trigger audit on account with transactions, verifies **AC-1**, **AC-2**, **AC-3**, **AC-4**, **AC-6**.
* Failure case: Gemini API is offline, system returns programmatic fallback report, verifies **AC-5**.

## Build plan
1. Add Spring Milestone repository and Spring AI starter to compliance-service `pom.xml`, satisfies **AC-3**, **AC-4**.
2. Modify ComplianceReport entity and schema to include flaggedTransactionId and triggerType, satisfies **AC-6**.
3. Implement Spring AI Gemini config and audit service block to call `gemini-1.5-flash` with weighted scoring prompts, satisfies **AC-1**, **AC-2**, **AC-3**, **AC-4**, **AC-6**.
4. Implement the fallback local audit logic when the API is unreachable, satisfies **AC-5**.
5. Update transaction-service to trigger async compliance service audits, satisfies **AC-1**, **AC-6**.
6. Create REST endpoint `POST /api/compliance/reports/audit/{accountId}` in compliance service, satisfies **AC-2**.
7. Write JUnit integration tests verifying both the happy path audit and the fallback handler, satisfies **AC-1**, **AC-2**, **AC-3**, **AC-4**, **AC-5**, **AC-6**.

## Consequences

**Positive**:
* Simplifies architecture by eliminating the external Python CLI runtime dependencies for automated triggers.
* Runs audits instantly when transactions are flagged.

**Negative / tradeoffs**:
* Adds dependency on the Spring Milestone repository.
* Costs extra tokens on Gemini API calls.

## References
**Project sources**:
* `context/project-memory.md` (verifiable stack ports)
* `skills/compliance-audit/SKILL.md` (compliance guidelines)
