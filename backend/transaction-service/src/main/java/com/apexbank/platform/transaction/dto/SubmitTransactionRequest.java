package com.apexbank.platform.transaction.dto;

import com.apexbank.platform.transaction.model.Transaction.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SubmitTransactionRequest(
    @NotBlank(message = "Account ID is required")
    String accountId,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive and greater than 0")
    BigDecimal amount,

    @NotNull(message = "Transaction type is required")
    TransactionType transactionType,

    String merchantName,
    String merchantLocation,
    String targetAccountId // Optional: only required if transactionType is TRANSFER
) {}
