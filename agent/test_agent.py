import pytest
from unittest.mock import patch, MagicMock
import httpx
from pathlib import Path
from agent.config import get_agent_config, BASE_DIR, SKILLS_DIR
from agent.tools import get_account_details, get_transaction_history, submit_compliance_report

# -------------------------------------------------------------
# 1. Config Loader Tests
# -------------------------------------------------------------

def test_config_loader_paths():
    """
    Verify that the config loader correctly resolves base directories 
    and skills folder paths absolutely.
    """
    assert BASE_DIR.exists()
    assert SKILLS_DIR.exists()
    assert SKILLS_DIR.is_dir()

def test_get_agent_config_registration():
    """
    Verify get_agent_config registers skills and system instructions.
    """
    custom_instruction = "Test Persona"
    config = get_agent_config(
        tools=[get_account_details],
        system_instructions=custom_instruction
    )
    
    assert config.skills_paths == [str(SKILLS_DIR.resolve())]
    assert config.system_instructions == custom_instruction
    assert get_account_details in config.tools

# -------------------------------------------------------------
# 2. Mock Tool Endpoint Tests (HTTP Mocking)
# -------------------------------------------------------------

@patch("httpx.get")
def test_get_account_details_success(mock_get):
    """
    Test get_account_details handles a successful HTTP response.
    """
    mock_response = MagicMock()
    mock_response.status_code = 200
    mock_response.json.return_value = {
        "id": "ACC-12345678",
        "ownerName": "John Doe",
        "email": "john@example.com",
        "balance": 1000.00
    }
    mock_get.return_value = mock_response

    result = get_account_details("ACC-12345678")

    assert result["id"] == "ACC-12345678"
    assert result["ownerName"] == "John Doe"
    assert "error" not in result
    mock_get.assert_called_once_with("http://localhost:8081/api/accounts/ACC-12345678", timeout=5.0)

@patch("httpx.get")
def test_get_account_details_not_found(mock_get):
    """
    Test get_account_details returns an error dict on 404.
    """
    mock_response = MagicMock()
    mock_response.status_code = 404
    mock_get.return_value = mock_response

    result = get_account_details("ACC-INVALID")

    assert "error" in result
    assert result["error"] == "Account not found"
    assert result["account_id"] == "ACC-INVALID"

@patch("httpx.get")
def test_get_account_details_unreachable(mock_get):
    """
    Test get_account_details handles connection timeouts and RequestErrors gracefully.
    """
    mock_get.side_effect = httpx.RequestError("Connection timeout")

    result = get_account_details("ACC-12345678")

    assert "error" in result
    assert "unreachable" in result["error"].lower()

@patch("httpx.get")
def test_get_transaction_history_success(mock_get):
    """
    Test get_transaction_history returns transactions list.
    """
    mock_response = MagicMock()
    mock_response.status_code = 200
    mock_response.json.return_value = {
        "content": [
            {"id": "TX-1", "amount": 150.00, "txType": "DEPOSIT"}
        ]
    }
    mock_get.return_value = mock_response

    result = get_transaction_history("ACC-12345678")

    assert "content" in result
    assert len(result["content"]) == 1
    assert result["content"][0]["id"] == "TX-1"
    mock_get.assert_called_once_with("http://localhost:8082/api/transactions/account/ACC-12345678?size=100", timeout=5.0)

@patch("httpx.post")
def test_submit_compliance_report_success(mock_post):
    """
    Test submit_compliance_report correctly formats payload and submits.
    """
    mock_response = MagicMock()
    mock_response.status_code = 201
    mock_response.json.return_value = {
        "id": "COMP-12345678",
        "accountId": "ACC-12345678",
        "riskScore": 75
    }
    mock_post.return_value = mock_response

    result = submit_compliance_report(
        account_id="ACC-12345678",
        risk_score=75,
        reasoning="Multiple velocity flags",
        actions_taken="MONITOR",
        drafted_sar="# Suspicious Activity Report..."
    )

    assert result["id"] == "COMP-12345678"
    assert result["riskScore"] == 75
    mock_post.assert_called_once_with(
        "http://localhost:8083/api/compliance/reports",
        json={
            "accountId": "ACC-12345678",
            "riskScore": 75,
            "reasoning": "Multiple velocity flags",
            "actionsTaken": "MONITOR",
            "draftedSar": "# Suspicious Activity Report..."
        },
        timeout=5.0
    )
