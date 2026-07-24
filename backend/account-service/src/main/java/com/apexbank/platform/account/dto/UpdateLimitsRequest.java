package com.apexbank.platform.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateLimitsRequest(
    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "0.0", message = "Credit limit cannot be negative")
    BigDecimal creditLimit,

    @NotNull(message = "Deposit limit is required")
    @DecimalMin(value = "0.0", message = "Deposit limit must be positive")
    BigDecimal depositLimit
) {}
