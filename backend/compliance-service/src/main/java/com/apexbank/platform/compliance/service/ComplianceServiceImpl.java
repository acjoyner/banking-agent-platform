package com.apexbank.platform.compliance.service;

import com.apexbank.platform.compliance.dto.ComplianceResponse;
import com.apexbank.platform.compliance.dto.SubmitComplianceRequest;
import com.apexbank.platform.compliance.model.ComplianceReport;
import com.apexbank.platform.compliance.repository.ComplianceReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceReportRepository reportRepository;
    private final ChatModel chatModel;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.services.account.url}")
    private String accountServiceUrl;

    @Value("${app.services.transaction.url}")
    private String transactionServiceUrl;

    @Autowired
    public ComplianceServiceImpl(ComplianceReportRepository reportRepository, @Autowired(required = false) ChatModel chatModel) {
        this.reportRepository = reportRepository;
        this.chatModel = chatModel;
    }

    @Override
    @Transactional
    public ComplianceResponse submitReport(SubmitComplianceRequest request) {
        // Generate custom Compliance Report ID: COMP-XXXXXXXX
        String id = "COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ComplianceReport report = new ComplianceReport(
            id,
            request.accountId(),
            request.riskScore(),
            request.reasoning(),
            request.actionsTaken(),
            request.draftedSar(),
            request.flaggedTransactionId(),
            request.triggerType() != null ? request.triggerType() : "MANUAL"
        );

        ComplianceReport saved = reportRepository.save(report);
        return ComplianceResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplianceResponse> getReportsByAccountId(String accountId) {
        return reportRepository.findByAccountId(accountId).stream()
            .map(ComplianceResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplianceResponse> getAllReports() {
        return reportRepository.findAll().stream()
            .map(ComplianceResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComplianceResponse runAudit(String accountId, String flaggedTransactionId, String triggerType) {
        String finalTriggerType = triggerType != null ? triggerType : "MANUAL";
        AuditResult auditResult;

        if (chatModel != null) {
            try {
                // Fetch profiles
                String accountProfile = getAccountJson(accountId);
                String transactionsJson = getTransactionsJson(accountId);

                String systemInstruction = "You are a senior banking compliance investigator agent. " +
                    "Your job is to analyze the account profile and historical transaction ledger " +
                    "to determine the compliance risk score and write a Suspicious Activity Report (SAR) in Markdown.\n" +
                    "Respond ONLY in valid JSON format matching this schema:\n" +
                    "{\n" +
                    "  \"riskScore\": 85,\n" +
                    "  \"reasoning\": \"Detailed reasoning of why the risk score is high/medium/low...\",\n" +
                    "  \"actionsTaken\": \"AUTO_FREEZE\",\n" +
                    "  \"draftedSar\": \"# Suspicious Activity Report...\"\n" +
                    "}\n" +
                    "Ensure riskScore is between 0 and 100.\n" +
                    "actionsTaken must be: 'AUTO_FREEZE' (if riskScore >= 80), 'MONITOR' (if riskScore >= 50), or 'NONE' (otherwise).\n" +
                    "draftedSar must be a detailed Markdown SAR containing transaction highlights, velocity patterns, and a final compliance recommendation.";

                String promptText = systemInstruction + "\n\n" +
                    "Account Profile:\n" + accountProfile + "\n\n" +
                    "Transaction Ledger History:\n" + transactionsJson + "\n\n" +
                    "Source Flagged Transaction ID: " + (flaggedTransactionId != null ? flaggedTransactionId : "None") + "\n" +
                    "Trigger Type: " + finalTriggerType + "\n\n" +
                    "Please generate the JSON response now.";

                ChatResponse chatResponse = chatModel.call(new Prompt(promptText));
                String responseText = chatResponse.getResult().getOutput().getText();
                String cleanedJson = cleanJson(responseText);

                ObjectMapper mapper = new ObjectMapper();
                auditResult = mapper.readValue(cleanedJson, AuditResult.class);
            } catch (Exception e) {
                System.err.println("Spring AI/Gemini invocation failed: " + e.getMessage());
                auditResult = runFallbackAudit(accountId, flaggedTransactionId, finalTriggerType);
            }
        } else {
            auditResult = runFallbackAudit(accountId, flaggedTransactionId, finalTriggerType);
        }

        // Save compliance report
        String reportId = "COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ComplianceReport report = new ComplianceReport(
            reportId,
            accountId,
            auditResult.riskScore,
            auditResult.reasoning,
            auditResult.actionsTaken,
            auditResult.draftedSar,
            flaggedTransactionId,
            finalTriggerType
        );

        ComplianceReport saved = reportRepository.save(report);
        return ComplianceResponse.fromEntity(saved);
    }

    private String getAccountJson(String accountId) {
        try {
            return restTemplate.getForObject(accountServiceUrl + "/api/accounts/" + accountId, String.class);
        } catch (Exception e) {
            return "{\"error\":\"Failed to fetch account profile: " + e.getMessage() + "\"}";
        }
    }

    private String getTransactionsJson(String accountId) {
        try {
            return restTemplate.getForObject(transactionServiceUrl + "/api/transactions/account/" + accountId, String.class);
        } catch (Exception e) {
            return "{\"error\":\"Failed to fetch transaction logs: " + e.getMessage() + "\"}";
        }
    }

    private String cleanJson(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        if (raw.startsWith("```")) {
            int firstLineBreak = raw.indexOf('\n');
            int lastBackticks = raw.lastIndexOf("```");
            if (firstLineBreak != -1 && lastBackticks > firstLineBreak) {
                raw = raw.substring(firstLineBreak, lastBackticks).trim();
            }
        }
        return raw;
    }

    private AuditResult runFallbackAudit(String accountId, String flaggedTransactionId, String triggerType) {
        int riskScore = 10;
        StringBuilder reasoning = new StringBuilder("AI Engine offline. Programmatic fallback check triggered.");
        StringBuilder sar = new StringBuilder("# Programmatic Compliance Audit Report\n\n");
        sar.append("AI Engine offline. Applied fallback compliance checks.\n\n");

        try {
            String txJson = getTransactionsJson(accountId);
            ObjectMapper mapper = new ObjectMapper();
            java.util.Map<?, ?> page = mapper.readValue(txJson, java.util.Map.class);
            java.util.List<?> content = (java.util.List<?>) page.get("content");

            boolean velocityViolation = false;
            boolean highValueViolation = false;
            boolean travelHopViolation = false;

            if (content != null && !content.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                int recentCount = 0;
                java.util.Set<String> locations = new java.util.HashSet<>();

                for (Object item : content) {
                    java.util.Map<?, ?> tx = (java.util.Map<?, ?>) item;

                    Object amountObj = tx.get("amount");
                    if (amountObj != null) {
                        double amount = Double.parseDouble(amountObj.toString());
                        if (amount > 10000.0) {
                            highValueViolation = true;
                        }
                    }

                    Object createdObj = tx.get("createdAt");
                    if (createdObj != null) {
                        try {
                            LocalDateTime created = LocalDateTime.parse(createdObj.toString().substring(0, 19));
                            if (created.isAfter(now.minusHours(1))) {
                                recentCount++;
                            }
                        } catch (Exception ignored) {}
                    }

                    Object locObj = tx.get("merchantLocation");
                    if (locObj != null) {
                        locations.add(locObj.toString());
                    }
                }

                if (recentCount > 5) {
                    velocityViolation = true;
                }
                if (locations.size() > 1) {
                    travelHopViolation = true;
                }
            }

            if (highValueViolation) {
                riskScore += 30;
                reasoning.append(" Flagged high-value transaction violation (+30).");
                sar.append("* **High Value Violation**: Account executed a transaction exceeding $10,000.\n");
            }
            if (velocityViolation) {
                riskScore += 20;
                reasoning.append(" Flagged high transaction velocity violation (+20).");
                sar.append("* **High Velocity Violation**: Account executed more than 5 transactions in the last hour.\n");
            }
            if (travelHopViolation) {
                riskScore += 45;
                reasoning.append(" Flagged travel-hop geographic violation (+45).");
                sar.append("* **Geographic Travel Hop**: Transactions executed from multiple locations within a short timeframe.\n");
            }

        } catch (Exception e) {
            reasoning.append(" Error parsing transaction ledger: ").append(e.getMessage());
            sar.append("* **Ledger parsing failure**: Could not retrieve full transactions history.\n");
        }

        if (riskScore > 100) riskScore = 100;

        String action = "NONE";
        if (riskScore >= 80) {
            action = "AUTO_FREEZE";
        } else if (riskScore >= 50) {
            action = "MONITOR";
        }

        sar.append("\n### Final Action Recommendation: ").append(action).append("\n");
        sar.append("Score: ").append(riskScore).append(" (Rule Based Fallback)\n");

        return new AuditResult(riskScore, reasoning.toString(), action, sar.toString());
    }

    public static class AuditResult {
        public Integer riskScore;
        public String reasoning;
        public String actionsTaken;
        public String draftedSar;

        public AuditResult() {}
        public AuditResult(Integer riskScore, String reasoning, String actionsTaken, String draftedSar) {
            this.riskScore = riskScore;
            this.reasoning = reasoning;
            this.actionsTaken = actionsTaken;
            this.draftedSar = draftedSar;
        }
    }
}
