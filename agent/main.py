import argparse
import asyncio
import sys
from google.antigravity import Agent
from .config import get_agent_config
from .tools import get_account_details, get_transaction_history, submit_compliance_report

# Central Persona Instructions for Compliance Investigation
COMPLIANCE_PERSONA = (
    "You are the ApexBank Automated Compliance and Fraud Analyst Agent.\n"
    "Your objective is to run detailed security audits on bank accounts.\n"
    "When requested to audit or investigate an account, perform the following steps:\n"
    "1. Retrieve the account details using 'get_account_details' to check limits and active risk levels.\n"
    "2. Fetch historical ledger logs using 'get_transaction_history' to evaluate recent transactions.\n"
    "3. Review transaction patterns for security indicators:\n"
    "   - Geographic velocity (e.g., transactions in different locations within hours).\n"
    "   - Velocity frequency (e.g., multiple transactions in a few minutes).\n"
    "   - Flagged transactions exceeding $10,000 threshold values.\n"
    "4. Formulate a Risk Score (0 to 100):\n"
    "   - Score >= 80: Critical risk (actionsTaken should be 'AUTO_FREEZE').\n"
    "   - Score >= 50: Moderate risk (actionsTaken should be 'MONITOR').\n"
    "   - Score < 50: Low risk (actionsTaken should be 'NONE').\n"
    "5. Draft a Suspicious Activity Report (SAR) in Markdown. Document account owner details, specific transactions that raised flags, risk score, and suggested action.\n"
    "6. Submit the report using the 'submit_compliance_report' tool.\n"
    "7. Stream a summary of your findings to the officer console.\n"
)

async def run_compliance_audit(account_id: str):
    """
    Instantiates the Antigravity Agent and executes the audit loop for the given account.
    Streams thoughts and outputs to the console.
    """
    # Configure the agent with tools and custom compliance instructions
    config = get_agent_config(
        tools=[get_account_details, get_transaction_history, submit_compliance_report],
        system_instructions=COMPLIANCE_PERSONA
    )

    print(f"\n[APEXBANK AI] Initializing compliance audit for account: {account_id}")
    print("[APEXBANK AI] Loading Antigravity filesystem skills...")

    async with Agent(config) as agent:
        prompt = f"Run a complete security audit on account {account_id}. Verify details, transaction logs, calculate the risk score, and submit the compliance report."
        
        response = await agent.chat(prompt)

        print("\n--- Agent Reasoning (Thoughts) ---")
        async for thought in response.thoughts:
            print(thought, end="", flush=True)
        print("\n----------------------------------")

        print("\n--- Agent Audit Report Summary ---")
        async for token in response:
            print(token, end="", flush=True)
        print("\n----------------------------------")

def main():
    parser = argparse.ArgumentParser(description="ApexBank Antigravity Compliance Agent CLI")
    parser.add_argument("--account", required=True, help="The ID of the account to investigate (e.g., ACC-12345678)")
    
    args = parser.parse_args()
    
    try:
        asyncio.run(run_compliance_audit(args.account))
    except KeyboardInterrupt:
        print("\n[Audit Interrupted] Exiting safely.")
        sys.exit(0)
    except Exception as e:
        print(f"\n[Audit Failure] Error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
