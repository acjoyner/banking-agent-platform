package com.apexbank.platform.transaction.service;

import com.apexbank.platform.transaction.dto.SubmitTransactionRequest;
import com.apexbank.platform.transaction.dto.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    TransactionResponse submitTransaction(SubmitTransactionRequest request);
    Page<TransactionResponse> getTransactionsByAccountId(String accountId, Pageable pageable);
    TransactionResponse rollbackTransaction(String transactionId);
}
