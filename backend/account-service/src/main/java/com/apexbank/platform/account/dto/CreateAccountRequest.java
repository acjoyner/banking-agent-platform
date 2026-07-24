package com.apexbank.platform.account.dto;

import com.apexbank.platform.account.model.Account.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateAccountRequest(
    @NotBlank(message = "Owner name is required")
    String ownerName,

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email,

    @NotNull(message = "Account type is required")
    AccountType accountType,

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "0.0", message = "Initial deposit cannot be negative")
    BigDecimal initialDeposit,

    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "0.0", message = "Credit limit cannot be negative")
    BigDecimal creditLimit,

    @NotNull(message = "Deposit limit is required")
    @DecimalMin(value = "0.0", message = "Deposit limit must be positive")
    BigDecimal depositLimit
) {}
