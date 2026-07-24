package com.apexbank.platform.account.service;

import com.apexbank.platform.account.dto.AccountResponse;
import com.apexbank.platform.account.dto.CreateAccountRequest;
import com.apexbank.platform.account.dto.UpdateLimitsRequest;
import java.util.List;

public interface AccountService {
    AccountResponse createAccount(CreateAccountRequest request);
    AccountResponse getAccountById(String id);
    List<AccountResponse> getAllAccounts();
    AccountResponse updateLimits(String id, UpdateLimitsRequest request);
}
