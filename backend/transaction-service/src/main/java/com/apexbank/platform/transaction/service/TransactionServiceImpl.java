package com.apexbank.platform.transaction.service;

import com.apexbank.platform.transaction.dto.SubmitTransactionRequest;
import com.apexbank.platform.transaction.dto.TransactionResponse;
import com.apexbank.platform.transaction.exception.BadRequestException;
import com.apexbank.platform.transaction.model.Transaction;
import com.apexbank.platform.transaction.model.Transaction.TransactionType;
import com.apexbank.platform.transaction.model.Transaction.TransactionStatus;
import com.apexbank.platform.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    @Value("${app.services.account-url}")
    private String accountServiceUrl;

    @Value("${app.services.compliance-url}")
    private String complianceServiceUrl;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public TransactionResponse submitTransaction(SubmitTransactionRequest request) {
        BigDecimal adjustment = request.amount();
        
        // Validate request type constraints
        if (request.transactionType() == TransactionType.WITHDRAWAL || request.transactionType() == TransactionType.TRANSFER) {
            adjustment = request.amount().negate();
        }

        // If transfer, verify target account is provided
        if (request.transactionType() == TransactionType.TRANSFER && (request.targetAccountId() == null || request.targetAccountId().isBlank())) {
            throw new BadRequestException("Target account ID is required for bank transfers");
        }

        // 1. Call Account Service to update source account balance
        try {
            String url = accountServiceUrl + "/api/accounts/" + request.accountId() + "/balance?amount=" + adjustment;
            restTemplate.put(url, null);
        } catch (HttpClientErrorException ex) {
            throw new BadRequestException("Account operation failed: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new BadRequestException("Could not connect to Account Service: " + ex.getMessage());
        }

        // 2. If transfer, credit the target account
        if (request.transactionType() == TransactionType.TRANSFER) {
            try {
                String targetUrl = accountServiceUrl + "/api/accounts/" + request.targetAccountId() + "/balance?amount=" + request.amount();
                restTemplate.put(targetUrl, null);
            } catch (HttpClientErrorException ex) {
                // Rollback source account credit (re-add withdrawal amount)
                try {
                    String rollbackUrl = accountServiceUrl + "/api/accounts/" + request.accountId() + "/balance?amount=" + request.amount();
                    restTemplate.put(rollbackUrl, null);
                } catch (Exception rollbackEx) {
                    // System inconsistency flagged (needs production/audit logs)
                }
                throw new BadRequestException("Transfer target account operation failed: " + ex.getResponseBodyAsString());
            } catch (Exception ex) {
                throw new BadRequestException("Could not connect to Account Service for transfer target: " + ex.getMessage());
            }
        }

        // 3. Safety Trigger: Automatically flag transactions over $10,000
        TransactionStatus status = TransactionStatus.COMPLETED;
        if (request.amount().compareTo(BigDecimal.valueOf(10000.00)) > 0) {
            status = TransactionStatus.FLAGGED;
        }

        // Generate Transaction ID: TX-XXXXXXXX
        String id = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Transaction transaction = new Transaction(
            id,
            request.accountId(),
            request.amount(),
            request.transactionType(),
            request.merchantName() != null ? request.merchantName() : "ApexBank Internal",
            request.merchantLocation() != null ? request.merchantLocation() : "System",
            status
        );

        Transaction saved = transactionRepository.save(transaction);

        if (status == TransactionStatus.FLAGGED) {
            final String accountId = request.accountId();
            final String transactionId = id;
            new Thread(() -> {
                try {
                    String auditUrl = complianceServiceUrl + "/api/compliance/reports/audit/" + accountId + "?transactionId=" + transactionId + "&triggerType=AUTOMATIC";
                    restTemplate.postForObject(auditUrl, null, String.class);
                } catch (Exception e) {
                    System.err.println("Failed to trigger automated compliance audit: " + e.getMessage());
                }
            }).start();
        }

        return TransactionResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByAccountId(String accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable)
            .map(TransactionResponse::fromEntity);
    }

    @Override
    @Transactional
    public TransactionResponse rollbackTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new BadRequestException("Transaction not found"));

        if (transaction.getStatus() == TransactionStatus.ROLLED_BACK) {
            throw new BadRequestException("Transaction is already rolled back");
        }

        BigDecimal rollbackAdjustment = transaction.getAmount();

        if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
            rollbackAdjustment = transaction.getAmount().negate();
        } else if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
            rollbackAdjustment = transaction.getAmount();
        } else {
            throw new BadRequestException("Direct transfer rollback is not supported; execute manual reversal instead");
        }

        try {
            String url = accountServiceUrl + "/api/accounts/" + transaction.getAccountId() + "/balance?amount=" + rollbackAdjustment;
            restTemplate.put(url, null);
        } catch (Exception ex) {
            throw new BadRequestException("Could not connect to Account Service for rollback: " + ex.getMessage());
        }

        transaction.setStatus(TransactionStatus.ROLLED_BACK);
        Transaction saved = transactionRepository.save(transaction);
        return TransactionResponse.fromEntity(saved);
    }
}
