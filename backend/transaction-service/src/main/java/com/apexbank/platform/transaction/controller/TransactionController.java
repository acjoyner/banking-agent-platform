package com.apexbank.platform.transaction.controller;

import com.apexbank.platform.transaction.dto.SubmitTransactionRequest;
import com.apexbank.platform.transaction.dto.TransactionResponse;
import com.apexbank.platform.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> submitTransaction(@Valid @RequestBody SubmitTransactionRequest request) {
        TransactionResponse response = transactionService.submitTransaction(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccountId(
            @PathVariable String accountId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<TransactionResponse> response = transactionService.getTransactionsByAccountId(accountId, pageable);
        return ResponseEntity.ok(response);
    }
}
