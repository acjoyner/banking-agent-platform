#!/usr/bin/env python3
import argparse
import asyncio
import sys
from agent.main import run_compliance_audit

def main():
    parser = argparse.ArgumentParser(description="ApexBank Operations Compliance AI Investigator Trigger")
    
    # Operations flag mapping
    parser.add_argument("--run-audit", action="store_true", help="Trigger standard security audit pipeline")
    parser.add_argument("--account", required=True, help="The target bank account ID to audit (e.g., ACC-12345678)")
    
    args = parser.parse_args()
    
    if not args.run_audit:
        print("[Operational Guide] Please supply --run-audit parameter to execute the compliance scan.")
        sys.exit(1)
        
    try:
        print(f"[Ops Command] Initiating AI investigator hold scan on: {args.account}")
        asyncio.run(run_compliance_audit(args.account))
    except KeyboardInterrupt:
        print("\n[Ops Command] Audit cancelled by user.")
        sys.exit(0)
    except Exception as e:
        print(f"\n[Ops Command] Audit crashed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
