package com.apexbank.platform.account.service;

import com.apexbank.platform.account.dto.AccountResponse;
import com.apexbank.platform.account.dto.CreateAccountRequest;
import com.apexbank.platform.account.dto.UpdateLimitsRequest;
import com.apexbank.platform.account.exception.BadRequestException;
import com.apexbank.platform.account.exception.ResourceNotFoundException;
import com.apexbank.platform.account.model.Account;
import com.apexbank.platform.account.model.Account.RiskLevel;
import com.apexbank.platform.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        if (accountRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Account with email " + request.email() + " already exists");
        }

        // Generate custom Account ID: e.g., ACC-XXXXXXXX
        String id = "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Account account = new Account(
            id,
            request.ownerName(),
            request.email(),
            request.accountType(),
            request.initialDeposit(),
            request.creditLimit(),
            request.depositLimit(),
            RiskLevel.LOW // Default risk level is LOW
        );

        Account saved = accountRepository.save(account);
        return AccountResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(String id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account with ID " + id + " not found"));
        return AccountResponse.fromEntity(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
            .map(AccountResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccountResponse updateLimits(String id, UpdateLimitsRequest request) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account with ID " + id + " not found"));

        account.setCreditLimit(request.creditLimit());
        account.setDepositLimit(request.depositLimit());

        // Perform simple risk assessment trigger
        if (request.creditLimit().compareTo(java.math.BigDecimal.valueOf(50000)) > 0) {
            account.setRiskLevel(RiskLevel.HIGH);
        } else if (request.creditLimit().compareTo(java.math.BigDecimal.valueOf(20000)) > 0) {
            account.setRiskLevel(RiskLevel.MEDIUM);
        } else {
            account.setRiskLevel(RiskLevel.LOW);
        }

        Account updated = accountRepository.save(account);
        return AccountResponse.fromEntity(updated);
    }
}
