package com.apexbank.platform.account.dto;

import com.apexbank.platform.account.model.Account;
import com.apexbank.platform.account.model.Account.AccountType;
import com.apexbank.platform.account.model.Account.RiskLevel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
    String id,
    String ownerName,
    String email,
    AccountType accountType,
    BigDecimal balance,
    BigDecimal creditLimit,
    BigDecimal depositLimit,
    RiskLevel riskLevel,
    LocalDateTime createdAt
) {
    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getOwnerName(),
            account.getEmail(),
            account.getAccountType(),
            account.getBalance(),
            account.getCreditLimit(),
            account.getDepositLimit(),
            account.getRiskLevel(),
            account.getCreatedAt()
        );
    }
}
