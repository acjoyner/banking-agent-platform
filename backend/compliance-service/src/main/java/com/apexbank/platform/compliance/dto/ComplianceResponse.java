package com.apexbank.platform.compliance.dto;

import com.apexbank.platform.compliance.model.ComplianceReport;
import java.time.LocalDateTime;

public record ComplianceResponse(
    String id,
    String accountId,
    Integer riskScore,
    String reasoning,
    String actionsTaken,
    String draftedSar,
    LocalDateTime createdAt
) {
    public static ComplianceResponse fromEntity(ComplianceReport report) {
        return new ComplianceResponse(
            report.getId(),
            report.getAccountId(),
            report.getRiskScore(),
            report.getReasoning(),
            report.getActionsTaken(),
            report.getDraftedSar(),
            report.getCreatedAt()
        );
    }
}
