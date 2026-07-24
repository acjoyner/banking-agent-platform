import os
from pathlib import Path
from google.antigravity import LocalAgentConfig
from dotenv import load_dotenv

# Load env variables from .env if present
load_dotenv()

# Resolve absolute path to the skills directory (root of the banking-agent-platform)
BASE_DIR = Path(__file__).resolve().parent.parent
SKILLS_DIR = BASE_DIR / "skills"

# Verify skills directory exists
if not SKILLS_DIR.exists():
    raise FileNotFoundError(f"Skills directory not found at: {SKILLS_DIR}")

# Export Microservice Connection URLs
ACCOUNT_SERVICE_URL = os.getenv("ACCOUNT_SERVICE_URL", "http://localhost:8081")
TRANSACTION_SERVICE_URL = os.getenv("TRANSACTION_SERVICE_URL", "http://localhost:8082")
COMPLIANCE_SERVICE_URL = os.getenv("COMPLIANCE_SERVICE_URL", "http://localhost:8083")

def get_agent_config(tools=None, system_instructions=None):
    """
    Constructs a LocalAgentConfig instance with absolute skills paths resolved.
    """
    return LocalAgentConfig(
        skills_paths=[str(SKILLS_DIR.resolve())],
        tools=tools or [],
        system_instructions=system_instructions
    )
