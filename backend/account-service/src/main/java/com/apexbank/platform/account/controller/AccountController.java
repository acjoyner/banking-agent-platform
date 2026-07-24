package com.apexbank.platform.account.controller;

import com.apexbank.platform.account.dto.AccountResponse;
import com.apexbank.platform.account.dto.CreateAccountRequest;
import com.apexbank.platform.account.dto.UpdateLimitsRequest;
import com.apexbank.platform.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable String id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> responses = accountService.getAllAccounts();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/limits")
    public ResponseEntity<AccountResponse> updateLimits(
            @PathVariable String id,
            @Valid @RequestBody UpdateLimitsRequest request) {
        AccountResponse response = accountService.updateLimits(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/balance")
    public ResponseEntity<AccountResponse> updateBalance(
            @PathVariable String id,
            @RequestParam java.math.BigDecimal amount) {
        AccountResponse response = accountService.updateBalance(id, amount);
        return ResponseEntity.ok(response);
    }
}
