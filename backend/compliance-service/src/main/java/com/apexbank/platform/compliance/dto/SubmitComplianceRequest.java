package com.apexbank.platform.compliance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitComplianceRequest(
    @NotBlank(message = "Account ID is required")
    String accountId,

    @NotNull(message = "Risk score is required")
    @Min(value = 0, message = "Risk score cannot be less than 0")
    @Max(value = 100, message = "Risk score cannot be greater than 100")
    Integer riskScore,

    @NotBlank(message = "Reasoning is required")
    String reasoning,

    @NotBlank(message = "Actions taken is required")
    String actionsTaken, // 'AUTO_FREEZE', 'MONITOR', 'NONE'

    String draftedSar // Markdown formatted suspicious activity report
) {}
