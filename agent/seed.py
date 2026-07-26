import httpx
import sys

def seed():
    url = "http://localhost:8081/api/accounts"
    
    accounts = [
        {
            "ownerName": "Anthony Joyner",
            "email": "anthony.checking@apexbank.io",
            "accountType": "CHECKING",
            "initialDeposit": 14250.00,
            "creditLimit": 5000.00,
            "depositLimit": 5000.00
        },
        {
            "ownerName": "Anthony Joyner",
            "email": "anthony.savings@apexbank.io",
            "accountType": "SAVINGS",
            "initialDeposit": 89400.00,
            "creditLimit": 0.00,
            "depositLimit": 10000.00
        },
        {
            "ownerName": "Suspect Account X",
            "email": "anonymous@darkweb.org",
            "accountType": "CHECKING",
            "initialDeposit": 1540.00,
            "creditLimit": 2000.00,
            "depositLimit": 2000.00
        }
    ]
    
    print("Starting database seeding...")
    
    for account in accounts:
        try:
            response = httpx.post(url, json=account, timeout=10.0)
            if response.status_code == 201:
                data = response.json()
                print(f"Created account: {data['ownerName']} ({data['accountType']}) with ID: {data['id']}")
            else:
                print(f"Failed to create account for {account['ownerName']}: Status {response.status_code} - {response.text}")
        except Exception as e:
            print(f"Connection error while seeding {account['ownerName']}: {e}")
            sys.exit(1)
            
    print("Database seeding completed.")

if __name__ == "__main__":
    seed()
