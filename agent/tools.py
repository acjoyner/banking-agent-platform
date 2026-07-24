import httpx
from .config import ACCOUNT_SERVICE_URL, TRANSACTION_SERVICE_URL, COMPLIANCE_SERVICE_URL

def get_account_details(account_id: str) -> dict:
    """
    Fetches the details of a bank account, including the owner's name, email, 
    account type, current balance, credit limits, deposit limits, and risk level.

    Args:
        account_id: The unique identifier of the bank account (e.g., "ACC-12345678").
    """
    url = f"{ACCOUNT_SERVICE_URL}/api/accounts/{account_id}"
    try:
        response = httpx.get(url, timeout=5.0)
        if response.status_code == 200:
            return response.json()
        elif response.status_code == 404:
            return {"error": "Account not found", "account_id": account_id}
        else:
            return {"error": f"Failed to fetch account details. HTTP Status: {response.status_code}", "details": response.text}
    except httpx.RequestError as e:
        return {"error": "Account Service is unreachable", "details": str(e)}

def get_transaction_history(account_id: str) -> dict:
    """
    Retrieves the historical ledger transaction log for a specific bank account.
    Use this to review transfer locations, transaction frequencies, velocity alerts, 
    and transaction statuses.

    Args:
        account_id: The unique identifier of the bank account (e.g., "ACC-12345678").
    """
    url = f"{TRANSACTION_SERVICE_URL}/api/transactions/account/{account_id}?size=100"
    try:
        response = httpx.get(url, timeout=5.0)
        if response.status_code == 200:
            return response.json()
        elif response.status_code == 404:
            return {"error": "No transaction history found or account invalid", "account_id": account_id}
        else:
            return {"error": f"Failed to fetch transaction logs. HTTP Status: {response.status_code}", "details": response.text}
    except httpx.RequestError as e:
        return {"error": "Transaction Service is unreachable", "details": str(e)}

def submit_compliance_report(account_id: str, risk_score: int, reasoning: str, actions_taken: str, drafted_sar: str) -> dict:
    """
    Submits an AI-generated Suspicious Activity Report (SAR) and compliance audit details
    to the centralized Compliance Vault database.

    Args:
        account_id: The unique identifier of the bank account being audited (e.g., "ACC-12345678").
        risk_score: An integer representing calculated risk (from 0 to 100).
        reasoning: A detailed justification for the risk score, highlighting specific transactions or velocity anomalies.
        actions_taken: Action recommended or auto-applied, e.g., "AUTO_FREEZE", "MONITOR", or "NONE".
        drafted_sar: Markdown formatted Suspicious Activity Report (SAR).
    """
    url = f"{COMPLIANCE_SERVICE_URL}/api/compliance/reports"
    payload = {
        "accountId": account_id,
        "riskScore": risk_score,
        "reasoning": reasoning,
        "actionsTaken": actions_taken,
        "draftedSar": drafted_sar
    }
    try:
        response = httpx.post(url, json=payload, timeout=5.0)
        if response.status_code == 201:
            return response.json()
        else:
            return {"error": f"Failed to submit compliance report. HTTP Status: {response.status_code}", "details": response.text}
    except httpx.RequestError as e:
        return {"error": "Compliance Service is unreachable", "details": str(e)}
