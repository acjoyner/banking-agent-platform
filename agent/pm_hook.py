#!/usr/bin/env python3
"""
Project Management Hook Script for Banking Agent Platform.
Provides deterministic CLI functions for status checking and project reporting
without requiring arbitrary code execution.
"""
import argparse
import json
import os
import subprocess
import sys
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parent.parent

def get_progress_status():
    tracker_path = PROJECT_ROOT / "context" / "progress-tracker.md"
    if not tracker_path.exists():
        return {"status": "error", "message": "progress-tracker.md not found"}
    
    completed_items = []
    pending_items = []
    
    with open(tracker_path, "r", encoding="utf-8") as f:
        for line in f:
            line_str = line.strip()
            if line_str.startswith("- [x]"):
                completed_items.append(line_str.replace("- [x]", "").strip())
            elif line_str.startswith("- [ ]"):
                pending_items.append(line_str.replace("- [ ]", "").strip())

    return {
        "status": "success",
        "project": "Banking Agent Platform (ApexBank)",
        "completed_count": len(completed_items),
        "pending_count": len(pending_items),
        "pending_tasks": pending_items,
        "completed_tasks": completed_items
    }

def get_git_status():
    try:
        res = subprocess.run(
            ["git", "status", "--porcelain"],
            cwd=PROJECT_ROOT,
            capture_output=True,
            text=True,
            check=True
        )
        changes = [line for line in res.stdout.splitlines() if line.strip()]
        return {
            "clean": len(changes) == 0,
            "modified_files_count": len(changes),
            "uncommitted_changes": changes[:10]
        }
    except Exception as e:
        return {"error": str(e)}

def main():
    parser = argparse.ArgumentParser(description="Banking Agent Platform PM Hook")
    parser.add_argument("--status", action="store_true", help="Output project progress and status in JSON")
    parser.add_argument("--summary", action="store_true", help="Print human-readable status summary")
    
    args = parser.parse_args()
    
    if args.status:
        progress = get_progress_status()
        git_info = get_git_status()
        output = {
            "progress": progress,
            "git": git_info
        }
        print(json.dumps(output, indent=2))
        sys.exit(0)
        
    if args.summary:
        progress = get_progress_status()
        print(f"=== Banking Agent Platform Project Status ===")
        print(f"Completed Tasks: {progress.get('completed_count', 0)}")
        print(f"Pending Tasks:   {progress.get('pending_count', 0)}")
        if progress.get("pending_tasks"):
            print("\nPending Items:")
            for task in progress["pending_tasks"]:
                print(f" - {task}")
        else:
            print("\nAll tracked milestone items are marked COMPLETE.")
        sys.exit(0)
        
    parser.print_help()
    sys.exit(1)

if __name__ == "__main__":
    main()
