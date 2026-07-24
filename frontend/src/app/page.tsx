"use client";

import React, { useState, useEffect } from "react";

// Types for Mock Database
interface Transaction {
  id: string;
  date: string;
  amount: number;
  type: "DEPOSIT" | "WITHDRAWAL" | "TRANSFER";
  merchant: string;
  location: string;
  status: "COMPLETED" | "FLAGGED" | "PENDING";
}

interface Account {
  id: string;
  ownerName: string;
  email: string;
  type: "CHECKING" | "SAVINGS";
  balance: number;
  dailyLimit: number;
  riskLevel: "LOW" | "MEDIUM" | "HIGH";
}

interface ComplianceReport {
  id: string;
  accountId: string;
  riskScore: number;
  reasoning: string;
  actionsTaken: "AUTO_FREEZE" | "MONITOR" | "NONE";
  draftedSar: string;
  createdAt: string;
}

export default function Dashboard() {
  const [activeTab, setActiveTab] = useState<"portal" | "compliance">("portal");
  const [selectedAccountId, setSelectedAccountId] = useState("ACC-87654321");
  const [filterType, setFilterType] = useState<"ALL" | "DEPOSIT" | "WITHDRAWAL" | "TRANSFER">("ALL");
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [chatMessages, setChatMessages] = useState<Array<{ sender: "user" | "agent"; text: string }>>([
    { sender: "agent", text: "Welcome to ApexBank Advisory. How can I assist you with your ledger today?" }
  ]);
  const [newMsg, setNewMsg] = useState("");
  
  // Compliance Audit Console state
  const [isAuditing, setIsAuditing] = useState(false);
  const [auditProgress, setAuditProgress] = useState<string[]>([]);
  const [finalReport, setFinalReport] = useState<ComplianceReport | null>(null);

  // High fidelity default data
  const [accounts, setAccounts] = useState<Account[]>([
    { id: "ACC-87654321", ownerName: "Anthony Joyner", email: "anthony@apexbank.io", type: "CHECKING", balance: 14250.00, dailyLimit: 5000, riskLevel: "LOW" },
    { id: "ACC-11223344", ownerName: "Anthony Joyner", email: "anthony@apexbank.io", type: "SAVINGS", balance: 89400.00, dailyLimit: 10000, riskLevel: "LOW" },
    { id: "ACC-55667788", ownerName: "Suspect Account X", email: "anonymous@darkweb.org", type: "CHECKING", balance: 1540.00, dailyLimit: 2000, riskLevel: "HIGH" }
  ]);

  const [transactions, setTransactions] = useState<Transaction[]>([
    { id: "TX-4091", date: "2026-07-24 09:12", amount: 1500.00, type: "DEPOSIT", merchant: "Payroll Apex", location: "New York, NY", status: "COMPLETED" },
    { id: "TX-4092", date: "2026-07-24 10:04", amount: 89.50, type: "WITHDRAWAL", merchant: "Whole Foods", location: "New York, NY", status: "COMPLETED" },
    { id: "TX-4093", date: "2026-07-24 11:32", amount: 450.00, type: "TRANSFER", merchant: "Zelle to Bob", location: "New York, NY", status: "COMPLETED" },
    { id: "TX-4094", date: "2026-07-24 11:34", amount: 12500.00, type: "DEPOSIT", merchant: "Wire Transfer", location: "Moscow, RU", status: "FLAGGED" },
    { id: "TX-4095", date: "2026-07-24 11:36", amount: 800.00, type: "WITHDRAWAL", merchant: "ATM Cash", location: "London, UK", status: "FLAGGED" }
  ]);

  const [reports, setReports] = useState<ComplianceReport[]>([
    {
      id: "COMP-A98B12C",
      accountId: "ACC-55667788",
      riskScore: 95,
      reasoning: "High frequency international cash hops detected between Moscow and London within a 2-minute window, paired with a wire transfer bypassing daily boundaries.",
      actionsTaken: "AUTO_FREEZE",
      draftedSar: "# SUSPICIOUS ACTIVITY REPORT\n\n- **Flagged Account**: ACC-55667788\n- **Identified Violation**: Velocity Hop & Wire Violation\n- **Recommendation**: Immediate Freeze applied.",
      createdAt: "2026-07-24 11:37"
    }
  ]);

  // Interactive transaction submission simulation (demonstrates Java microservice safety trigger)
  const [txAmount, setTxAmount] = useState("");
  const [txType, setTxType] = useState<"DEPOSIT" | "WITHDRAWAL">("DEPOSIT");
  const [txLocation, setTxLocation] = useState("New York, NY");

  const handlePostTransaction = (e: React.FormEvent) => {
    e.preventDefault();
    const amountVal = parseFloat(txAmount);
    if (isNaN(amountVal) || amountVal <= 0) return;

    // Safety Trigger matching backend: amount > 10000 -> automatically FLAGGED
    const status: "COMPLETED" | "FLAGGED" = amountVal > 10000 ? "FLAGGED" : "COMPLETED";

    const newTx: Transaction = {
      id: "TX-" + Math.floor(1000 + Math.random() * 9000),
      date: new Date().toISOString().replace("T", " ").substring(0, 16),
      amount: amountVal,
      type: txType === "DEPOSIT" ? "DEPOSIT" : "WITHDRAWAL",
      merchant: txType === "DEPOSIT" ? "Self Deposit" : "Direct Debit",
      location: txLocation,
      status: status
    };

    // Update locally
    setTransactions([newTx, ...transactions]);

    // Update account balance
    setAccounts(accounts.map(acc => {
      if (acc.id === selectedAccountId) {
        const factor = txType === "DEPOSIT" ? 1 : -1;
        const newBalance = acc.balance + (amountVal * factor);
        // If high risk transaction, increase risk level representation
        const newRisk = (amountVal > 10000 || status === "FLAGGED") ? "HIGH" : acc.riskLevel;
        return { ...acc, balance: newBalance, riskLevel: newRisk as "LOW" | "MEDIUM" | "HIGH" };
      }
      return acc;
    }));

    setTxAmount("");
    
    if (status === "FLAGGED") {
      alert("🚨 SAFETY LIMIT TRIGGERED: Transaction exceeds $10,000 threshold and has been marked FLAGGED. Antigravity Investigator recommended.");
    }
  };

  // Antigravity AI Investigator streaming simulation (high fidelity agent thoughts)
  const triggerInvestigation = async () => {
    setIsAuditing(true);
    setAuditProgress([]);
    setFinalReport(null);

    const logs = [
      "🔄 [Antigravity SDK] Initializing compliance investigator agent...",
      "📂 [Skills Loader] Resolving absolute path: /Users/anthonyjoyner/Documents/Projects/banking-agent-platform/skills",
      "⚡ [Skill loaded] compliance-audit skill instructions active.",
      "🔍 [Tool: get_account_details] Fetching profile for ACC-55667788...",
      "📋 [Tool: get_transaction_history] Fetching ledger list (size=100)...",
      "🧠 [Thinking] Running transaction velocity audit...",
      "🧠 [Thinking] WARNING: High-value wire ($12,500.00) from Moscow, RU exceeds the $10,000 threshold.",
      "🧠 [Thinking] WARNING: ATM withdrawal in London, UK ($800.00) recorded 2 minutes after NYC transaction. Physical travel hop impossible.",
      "📊 [Thinking] Calculating compliance metrics: Base(10) + HighValue(30) + GeoHop(45) = 85/100 risk score.",
      "⚖️ [Thinking] Recommended operational hold: AUTO_FREEZE.",
      "📝 [Thinking] Drafting Suspicious Activity Report (SAR) markdown document...",
      "💾 [Tool: submit_compliance_report] Writing audit payload to Spring Boot vault service (Port 8083)...",
      "✅ [Antigravity SDK] Report database entry created: COMP-B19F88A"
    ];

    for (let i = 0; i < logs.length; i++) {
      await new Promise(res => setTimeout(res, 800));
      setAuditProgress(prev => [...prev, logs[i]]);
    }

    const mockNewReport: ComplianceReport = {
      id: "COMP-B19F88A",
      accountId: "ACC-55667788",
      riskScore: 85,
      reasoning: "High-value wire ($12,500.00) from Moscow, RU paired with physically impossible geographic hop to London, UK ($800.00 ATM) within 2 minutes. Account balance is near exhaustion.",
      actionsTaken: "AUTO_FREEZE",
      draftedSar: `# SUSPICIOUS ACTIVITY REPORT (SAR)

## Section I — Subject Profile
- **Account ID**: ACC-55667788
- **Owner Name**: Suspect Account X
- **Risk Level**: HIGH

## Section II — Risk Evaluation
- **Risk Score**: 85/100
- **Anomalies Flagged**:
  * High-value international wire bypasses limits.
  * Geo-hop violation (Russia -> United Kingdom).

## Section III — Transaction Details
- **TX-4094**: Deposit $12,500.00 | Wire Transfer | Moscow, RU | FLAGGED
- **TX-4095**: Withdrawal $800.00 | ATM Cash | London, UK | FLAGGED

## Section IV — Action & Justification
- **Operational Directive**: AUTO_FREEZE
- **Justification**: Flagged velocity hops and high-value wire transfers bypass account daily guidelines. Immediate balance hold applied.`,
      createdAt: new Date().toISOString().replace("T", " ").substring(0, 16)
    };

    setFinalReport(mockNewReport);
    setReports([mockNewReport, ...reports]);
    
    // Auto-update Account risk level visual
    setAccounts(accounts.map(acc => {
      if (acc.id === "ACC-55667788") {
        return { ...acc, riskLevel: "HIGH" };
      }
      return acc;
    }));

    setIsAuditing(false);
  };

  // Chat agent response simulation
  const handleSendChat = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMsg.trim()) return;

    const userMessage = { sender: "user" as const, text: newMsg };
    setChatMessages(prev => [...prev, userMessage]);
    setNewMsg("");

    setTimeout(() => {
      let botResponse = "I have scanned the ledger. Transactions are currently within daily safety limits.";
      if (newMsg.toLowerCase().includes("transfer") || newMsg.toLowerCase().includes("limit")) {
        botResponse = "Your account daily limit is currently $5,000 for Checking and $10,000 for Savings. Any single transaction exceeding $10,000 is automatically flagged for Antigravity Compliance Audit.";
      } else if (newMsg.toLowerCase().includes("freeze") || newMsg.toLowerCase().includes("audit")) {
        botResponse = "Audits are run by the Antigravity Investigator Agent using filesystem skills. You can trigger an investigator scan via the 'Compliance Audit Console' tab.";
      }
      setChatMessages(prev => [...prev, { sender: "agent", text: botResponse }]);
    }, 600);
  };

  // Filtered transactions
  const filteredTxs = transactions.filter(tx => {
    if (filterType === "ALL") return true;
    return tx.type === filterType;
  });

  const selectedAcc = accounts.find(a => a.id === selectedAccountId) || accounts[0];

  return (
    <div className="flex-1 flex flex-col min-h-screen">
      {/* Premium Navbar */}
      <header className="glass-panel rounded-none border-t-0 border-x-0 bg-opacity-70 px-6 py-4 flex items-center justify-between z-20">
        <div className="flex items-center gap-3">
          <div className="h-4 w-4 bg-[#10B981] rounded-full animate-pulse shadow-[0_0_12px_#10B981]"></div>
          <span className="font-mono font-bold tracking-widest text-lg text-gradient-green">
            APEXBANK // COGNITIVE PLATFORM
          </span>
        </div>
        
        <div className="flex gap-2">
          <button
            onClick={() => setActiveTab("portal")}
            className={`btn-secondary border-none px-4 py-2 text-sm ${activeTab === "portal" ? "bg-[rgba(16,185,129,0.15)] text-[#10B981] border border-[rgba(16,185,129,0.3)]" : ""}`}
          >
            Ledger Portal
          </button>
          <button
            onClick={() => setActiveTab("compliance")}
            className={`btn-secondary border-none px-4 py-2 text-sm ${activeTab === "compliance" ? "bg-[rgba(16,185,129,0.15)] text-[#10B981] border border-[rgba(16,185,129,0.3)]" : ""}`}
          >
            Investigator Console
          </button>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 p-6 md:p-8 flex flex-col gap-6 max-w-7xl w-full mx-auto slide-up">
        {activeTab === "portal" ? (
          <>
            {/* Row 1: Account Selection & Metrics */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {/* Account Selector Glass Panel */}
              <div className="glass-panel p-6 flex flex-col gap-4">
                <h3 className="font-mono text-sm tracking-wider text-muted uppercase">Selected Ledger Account</h3>
                <div className="flex flex-col gap-2">
                  {accounts.map(acc => (
                    <button
                      key={acc.id}
                      onClick={() => setSelectedAccountId(acc.id)}
                      className={`text-left p-3 rounded-lg border transition-all ${
                        selectedAccountId === acc.id 
                          ? "bg-[rgba(16,185,129,0.08)] border-[#10B981]" 
                          : "bg-transparent border-transparent hover:bg-white/5"
                      }`}
                    >
                      <div className="flex justify-between items-center">
                        <span className="font-mono font-semibold text-sm">{acc.id}</span>
                        <span className={`text-xs px-2 py-0.5 rounded font-mono ${
                          acc.riskLevel === "HIGH" ? "bg-red-500/20 text-red-400" :
                          acc.riskLevel === "MEDIUM" ? "bg-amber-500/20 text-amber-400" :
                          "bg-emerald-500/20 text-emerald-400"
                        }`}>
                          {acc.riskLevel}
                        </span>
                      </div>
                      <div className="text-xs text-muted mt-1">{acc.ownerName} ({acc.type})</div>
                    </button>
                  ))}
                </div>
              </div>

              {/* Dynamic Balance Card */}
              <div className={`glass-panel p-6 flex flex-col justify-between ${
                selectedAcc.riskLevel === "HIGH" ? "glow-card-red" : "glow-card-green"
              }`}>
                <div>
                  <div className="flex justify-between items-start">
                    <span className="font-mono text-xs text-muted tracking-wider uppercase">Available Balance</span>
                    <span className="text-xs font-mono text-muted">{selectedAcc.type}</span>
                  </div>
                  <h2 className="text-4xl font-extrabold font-mono mt-3 text-gradient-green">
                    ${selectedAcc.balance.toLocaleString("en-US", { minimumFractionDigits: 2 })}
                  </h2>
                </div>
                <div className="flex justify-between text-xs font-mono mt-4 pt-3 border-t border-white/5">
                  <span className="text-muted">Daily Limit:</span>
                  <span>${selectedAcc.dailyLimit.toLocaleString()}</span>
                </div>
              </div>

              {/* Instant Simulator Control */}
              <div className="glass-panel p-6 flex flex-col justify-between">
                <div>
                  <h3 className="font-mono text-sm tracking-wider text-muted uppercase mb-3">Simulate Local Ledger Transaction</h3>
                  <form onSubmit={handlePostTransaction} className="flex flex-col gap-2">
                    <div className="flex gap-2">
                      <select 
                        value={txType} 
                        onChange={(e) => setTxType(e.target.value as any)}
                        className="bg-black/40 border border-white/10 rounded px-2 py-1 text-xs text-main focus:outline-none"
                      >
                        <option value="DEPOSIT">Deposit</option>
                        <option value="WITHDRAWAL">Withdrawal</option>
                      </select>
                      <input 
                        type="number" 
                        placeholder="Amount ($)" 
                        value={txAmount}
                        onChange={(e) => setTxAmount(e.target.value)}
                        className="bg-black/40 border border-white/10 rounded px-3 py-1 text-xs text-main flex-1 focus:outline-none"
                        required
                      />
                    </div>
                    <input 
                      type="text" 
                      placeholder="Location (e.g. New York, NY)" 
                      value={txLocation}
                      onChange={(e) => setTxLocation(e.target.value)}
                      className="bg-black/40 border border-white/10 rounded px-3 py-1 text-xs text-main focus:outline-none"
                    />
                    <button type="submit" className="btn-primary text-xs py-1.5 mt-1">
                      Post Transaction
                    </button>
                  </form>
                </div>
              </div>
            </div>

            {/* Row 2: Transaction History Ledger */}
            <div className="glass-panel p-6 flex-1 flex flex-col gap-4">
              <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 border-b border-white/5 pb-4">
                <div>
                  <h3 className="font-mono text-base font-bold text-main">Ledger Transactions</h3>
                  <p className="text-xs text-muted">Real-time ledger pipeline reporting transactions for safety threshold verification.</p>
                </div>
                <div className="flex gap-1.5">
                  {(["ALL", "DEPOSIT", "WITHDRAWAL", "TRANSFER"] as const).map(type => (
                    <button
                      key={type}
                      onClick={() => setFilterType(type)}
                      className={`text-xs px-2.5 py-1 rounded transition-all ${
                        filterType === type 
                          ? "bg-white/10 text-main border border-white/20" 
                          : "bg-transparent text-muted hover:text-main"
                      }`}
                    >
                      {type}
                    </button>
                  ))}
                </div>
              </div>

              {/* Responsive Table */}
              <div className="overflow-x-auto flex-1">
                <table className="w-full text-left border-collapse text-xs font-mono">
                  <thead>
                    <tr className="border-b border-white/5 text-muted uppercase">
                      <th className="py-2 px-3">TX ID</th>
                      <th className="py-2 px-3">Timestamp</th>
                      <th className="py-2 px-3">Type</th>
                      <th className="py-2 px-3">Merchant</th>
                      <th className="py-2 px-3">Location</th>
                      <th className="py-2 px-3 text-right">Amount</th>
                      <th className="py-2 px-3 text-right">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredTxs.map(tx => (
                      <tr key={tx.id} className="border-b border-white/5 hover:bg-white/5">
                        <td className="py-3 px-3 font-semibold text-[#10B981]">{tx.id}</td>
                        <td className="py-3 px-3 text-muted">{tx.date}</td>
                        <td className="py-3 px-3">
                          <span className={`px-2 py-0.5 rounded text-[10px] ${
                            tx.type === "DEPOSIT" ? "bg-emerald-500/10 text-emerald-400" :
                            tx.type === "WITHDRAWAL" ? "bg-indigo-500/10 text-indigo-400" :
                            "bg-purple-500/10 text-purple-400"
                          }`}>
                            {tx.type}
                          </span>
                        </td>
                        <td className="py-3 px-3 text-main font-sans">{tx.merchant}</td>
                        <td className="py-3 px-3 text-muted font-sans">{tx.location}</td>
                        <td className="py-3 px-3 text-right font-bold text-main">
                          ${tx.amount.toLocaleString("en-US", { minimumFractionDigits: 2 })}
                        </td>
                        <td className="py-3 px-3 text-right">
                          <span className={`px-2 py-0.5 rounded text-[10px] ${
                            tx.status === "FLAGGED" ? "bg-red-500/20 text-red-400 animate-pulse" :
                            tx.status === "PENDING" ? "bg-amber-500/10 text-amber-400" :
                            "bg-white/5 text-muted"
                          }`}>
                            {tx.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </>
        ) : (
          /* Tab 2: Compliance investigator Console */
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 flex-1 items-stretch">
            {/* Column A: Investigation & Live Agent Pipeline */}
            <div className="lg:col-span-2 flex flex-col gap-6">
              {/* Trigger Agent Control */}
              <div className="glass-panel p-6 flex flex-col gap-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-mono text-base font-bold text-main">Antigravity Compliance investigator</h3>
                    <p className="text-xs text-muted">Lease an autonomous AI investigator agent running filesystem compliance skills.</p>
                  </div>
                  <button
                    onClick={triggerInvestigation}
                    disabled={isAuditing}
                    className="btn-primary text-xs py-2 px-4 flex items-center gap-2"
                  >
                    {isAuditing ? "Auditing..." : "Trigger AI Security Audit"}
                  </button>
                </div>

                {/* Console Log screen */}
                <div className="bg-black/60 border border-white/10 rounded-lg p-4 font-mono text-[11px] h-60 overflow-y-auto flex flex-col gap-1.5 text-emerald-400 scroll-bar">
                  <div className="text-[#10B981] font-semibold mb-1">
                    APEXBANK // COMPLIANCE_INVESTIGATOR_SERVICE v0.1.8
                  </div>
                  {auditProgress.length === 0 ? (
                    <div className="text-muted">Agent pipeline idle. Press trigger to lease Investigator Agent...</div>
                  ) : (
                    auditProgress.map((prog, idx) => (
                      <div key={idx} className={`${prog.includes("Thinking") ? "text-indigo-300" : ""}`}>
                        {prog}
                      </div>
                    ))
                  )}
                </div>
              </div>

              {/* SAR Markdown Output view */}
              {finalReport && (
                <div className="glass-panel p-6 flex flex-col gap-4 slide-up">
                  <div className="flex items-center justify-between border-b border-white/5 pb-3">
                    <span className="font-mono text-sm font-bold text-main">Generated Suspicious Activity Report</span>
                    <span className="text-xs font-mono bg-red-500/20 text-red-400 px-2 py-0.5 rounded">
                      {finalReport.actionsTaken}
                    </span>
                  </div>
                  <div className="bg-black/20 border border-white/5 rounded-lg p-5 font-sans text-sm text-main max-h-96 overflow-y-auto prose prose-invert leading-relaxed">
                    {/* Inline markdown renderer for rendering SAR text */}
                    <div className="flex flex-col gap-3 font-mono text-xs">
                      <pre className="whitespace-pre-wrap">{finalReport.draftedSar}</pre>
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Column B: Compliance Audit Reports List */}
            <div className="flex flex-col gap-6">
              <div className="glass-panel p-6 flex flex-col gap-4 flex-1">
                <h3 className="font-mono text-sm tracking-wider text-muted uppercase">Archived Audits & SAR Logs</h3>
                <div className="flex flex-col gap-3 overflow-y-auto max-h-[500px]">
                  {reports.map(rep => (
                    <div key={rep.id} className="p-3 bg-white/5 rounded-lg border border-white/5 hover:border-white/10 flex flex-col gap-2">
                      <div className="flex justify-between items-center text-xs">
                        <span className="font-mono font-bold text-[#10B981]">{rep.id}</span>
                        <span className="text-muted text-[10px]">{rep.createdAt}</span>
                      </div>
                      <div className="text-xs font-semibold text-main">Account: {rep.accountId}</div>
                      <p className="text-xs text-muted line-clamp-3 font-sans leading-relaxed">{rep.reasoning}</p>
                      <div className="flex justify-between items-center mt-2 pt-2 border-t border-white/5 text-[10px]">
                        <span className="text-red-400 font-mono">Score: {rep.riskScore}/100</span>
                        <span className="bg-red-500/10 text-red-400 font-mono px-1.5 py-0.5 rounded">{rep.actionsTaken}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Floating Wealth Advisor Chat Widget */}
      <div className="fixed bottom-6 right-6 z-30 flex flex-col items-end">
        {isChatOpen && (
          <div className="glass-panel w-80 h-96 mb-3 flex flex-col overflow-hidden shadow-2xl slide-up border border-[#10B981]">
            {/* Chat Header */}
            <div className="bg-[#111827] border-b border-white/5 p-3 flex justify-between items-center">
              <div className="flex items-center gap-2">
                <div className="h-2.5 w-2.5 bg-[#10B981] rounded-full animate-pulse"></div>
                <span className="font-mono text-xs font-bold text-main">APEXBANK ADVISORY CHAT</span>
              </div>
              <button 
                onClick={() => setIsChatOpen(false)}
                className="text-muted hover:text-main text-sm"
              >
                ✕
              </button>
            </div>

            {/* Messages Screen */}
            <div className="flex-1 p-3 overflow-y-auto flex flex-col gap-2 bg-black/40">
              {chatMessages.map((msg, idx) => (
                <div 
                  key={idx} 
                  className={`max-w-[85%] rounded-lg p-2.5 text-xs ${
                    msg.sender === "user" 
                      ? "bg-[#10B981] text-white self-end" 
                      : "bg-white/5 text-main self-start border border-white/5"
                  }`}
                >
                  <p className="font-sans leading-relaxed">{msg.text}</p>
                </div>
              ))}
            </div>

            {/* Chat Input form */}
            <form onSubmit={handleSendChat} className="border-t border-white/5 p-2 flex gap-1.5 bg-[#111827]">
              <input
                type="text"
                placeholder="Ask about limits, transfers..."
                value={newMsg}
                onChange={(e) => setNewMsg(e.target.value)}
                className="bg-black/40 border border-white/10 rounded px-2.5 py-1.5 text-xs text-main flex-1 focus:outline-none"
              />
              <button type="submit" className="btn-primary text-xs py-1 px-3">
                Send
              </button>
            </form>
          </div>
        )}

        {/* Floating trigger toggle button */}
        <button
          onClick={() => setIsChatOpen(!isChatOpen)}
          className="btn-primary flex items-center justify-center h-12 w-12 rounded-full shadow-[0_4px_20px_rgba(16,185,129,0.4)]"
        >
          {isChatOpen ? "✕" : "💬"}
        </button>
      </div>
    </div>
  );
}
