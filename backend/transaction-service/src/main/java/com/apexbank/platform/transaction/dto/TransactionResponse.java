package com.apexbank.platform.transaction.dto;

import com.apexbank.platform.transaction.model.Transaction;
import com.apexbank.platform.transaction.model.Transaction.TransactionType;
import com.apexbank.platform.transaction.model.Transaction.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
    String id,
    String accountId,
    BigDecimal amount,
    TransactionType transactionType,
    String merchantName,
    String merchantLocation,
    TransactionStatus status,
    LocalDateTime createdAt
) {
    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAccountId(),
            transaction.getAmount(),
            transaction.getTransactionType(),
            transaction.getMerchantName(),
            transaction.getMerchantLocation(),
            transaction.getStatus(),
            transaction.getCreatedAt()
        );
    }
}
