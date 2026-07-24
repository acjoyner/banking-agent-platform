package com.apexbank.platform.compliance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_reports")
public class ComplianceReport {

    @Id
    @Column(length = 50)
    private String id;

    @NotBlank(message = "Account ID is required")
    @Column(name = "account_id", nullable = false, length = 50)
    private String accountId;

    @NotNull(message = "Risk score is required")
    @Min(value = 0, message = "Risk score cannot be less than 0")
    @Max(value = 100, message = "Risk score cannot be greater than 100")
    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @NotBlank(message = "Reasoning details are required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reasoning;

    @NotBlank(message = "Actions taken is required")
    @Column(name = "actions_taken", nullable = false, length = 100)
    private String actionsTaken; // 'AUTO_FREEZE', 'MONITOR', 'NONE'

    @Column(name = "drafted_sar", columnDefinition = "TEXT")
    private String draftedSar; // Markdown text containing Suspicious Activity Report

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Default Constructor
    public ComplianceReport() {}

    // Parametrized Constructor
    public ComplianceReport(String id, String accountId, Integer riskScore, String reasoning,
                            String actionsTaken, String draftedSar) {
        this.id = id;
        this.accountId = accountId;
        this.riskScore = riskScore;
        this.reasoning = reasoning;
        this.actionsTaken = actionsTaken;
        this.draftedSar = draftedSar;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public String getActionsTaken() {
        return actionsTaken;
    }

    public void setActionsTaken(String actionsTaken) {
        this.actionsTaken = actionsTaken;
    }

    public String getDraftedSar() {
        return draftedSar;
    }

    public void setDraftedSar(String draftedSar) {
        this.draftedSar = draftedSar;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
