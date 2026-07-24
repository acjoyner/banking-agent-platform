---
name: compliance-audit
description: Domain skill instructing agents on how to execute standard compliance audits, evaluate transaction velocity, check geographic anomalies, calculate risk metrics, and structure Suspicious Activity Reports (SARs).
---

# Banking Compliance & Suspicious Activity Audit Skill

Use this skill when auditing a customer account for compliance safety, identifying transaction anomalies, or drafting regulatory documentation (SARs).

## Step 1 — Account & Log Retrieval
- Query the core account profile using `get_account_details` to establish base deposit and credit limits.
- Retrieve the last 100 transactions using `get_transaction_history`.

## Step 2 — Risk Vector Evaluation
Analyze the retrieved transaction lists for the following compliance metrics:
1. **High-Value Flags**: Identify transactions exceeding $10,000. These are automatically flagged and represent immediate risk.
2. **Velocity Anomalies**: Check if more than 3 transactions occur within any 5-minute window.
3. **Geographical velocity hops**: Verify locations. If transactions occur in different cities or countries within a timeframe that is physically impossible to travel (e.g. New York to London in 2 hours), flag as an immediate geo-hop violation.

## Step 3 — Calculate Risk Score (0 - 100)
Establish risk weightings:
- **Base Score**: Start at 10.
- **Each High-Value Flag**: +30 points.
- **Velocity Violation**: +20 points.
- **Geo-hop Violation**: +45 points.
- *Cap the final score at 100.*

## Step 4 — Action Recommendation
Map the final risk score to an operational directive:
- **Score >= 80**: Action = `AUTO_FREEZE` (immediate account hold).
- **Score >= 50**: Action = `MONITOR` (manual oversight).
- **Score < 50**: Action = `NONE` (pass status).

## Step 5 — Suspicious Activity Report (SAR) Template
Draft the report as Markdown using the following structure:

```markdown
# SUSPICIOUS ACTIVITY REPORT (SAR)

## Section I — Subject Profile
- **Account ID**: [Account ID]
- **Account Owner**: [Owner Name]
- **Risk Level**: [Risk Level - LOW/MEDIUM/HIGH]

## Section II — Risk Evaluation
- **Risk Score**: [Calculated Score]/100
- **Primary Anomalies Flagged**:
  * [Bullet points of violations]

## Section III — Transaction Details
| Transaction ID | Date | Amount | Type | Merchant | Location | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| [TX ID] | [Date] | [Amount] | [Type] | [Merchant] | [Location] | [Status] |

## Section IV — Action & Justification
- **Operational Directive**: [AUTO_FREEZE / MONITOR / NONE]
- **Justification Summary**: [Short paragraph explaining the reasoning and specific transaction indicators]
```

## Step 6 — Database Submission
Format the fields and submit the record using `submit_compliance_report`.
