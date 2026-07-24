package com.apexbank.platform.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.apexbank.platform.account.dto.AccountResponse;
import com.apexbank.platform.account.dto.CreateAccountRequest;
import com.apexbank.platform.account.dto.UpdateLimitsRequest;
import com.apexbank.platform.account.exception.BadRequestException;
import com.apexbank.platform.account.exception.ResourceNotFoundException;
import com.apexbank.platform.account.model.Account;
import com.apexbank.platform.account.model.Account.AccountType;
import com.apexbank.platform.account.model.Account.RiskLevel;
import com.apexbank.platform.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        this.accountService = new AccountServiceImpl(accountRepository);
    }

    @Test
    void createAccount_Success() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(
            "John Doe", "john@example.com", AccountType.CHECKING,
            BigDecimal.valueOf(1000.00), BigDecimal.valueOf(5000.00), BigDecimal.valueOf(2000.00)
        );

        when(accountRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account saved = invocation.getArgument(0);
            return saved;
        });

        // Act
        AccountResponse response = accountService.createAccount(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).startsWith("ACC-");
        assertThat(response.ownerName()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.balance()).isEqualByComparingTo("1000.00");
        assertThat(response.creditLimit()).isEqualByComparingTo("5000.00");
        assertThat(response.depositLimit()).isEqualByComparingTo("2000.00");
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.LOW);

        verify(accountRepository, times(1)).findByEmail(request.email());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_ThrowsBadRequest_WhenEmailExists() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest(
            "John Doe", "john@example.com", AccountType.CHECKING,
            BigDecimal.valueOf(1000.00), BigDecimal.valueOf(5000.00), BigDecimal.valueOf(2000.00)
        );

        Account existing = new Account();
        existing.setEmail("john@example.com");

        when(accountRepository.findByEmail(request.email())).thenReturn(Optional.of(existing));

        // Act & Assert
        assertThatThrownBy(() -> accountService.createAccount(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("already exists");

        verify(accountRepository, times(1)).findByEmail(request.email());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateLimits_RecalculatesRiskLevel_ToHigh() {
        // Arrange
        String accountId = "ACC-12345678";
        Account existing = new Account(
            accountId, "John Doe", "john@example.com", AccountType.CHECKING,
            BigDecimal.valueOf(1000.00), BigDecimal.valueOf(5000.00), BigDecimal.valueOf(2000.00), RiskLevel.LOW
        );

        UpdateLimitsRequest request = new UpdateLimitsRequest(
            BigDecimal.valueOf(60000.00), // credit limit over 50k sets HIGH risk
            BigDecimal.valueOf(5000.00)
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AccountResponse response = accountService.updateLimits(accountId, request);

        // Assert
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(response.creditLimit()).isEqualByComparingTo("60000.00");

        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void updateLimits_ThrowsNotFound_WhenAccountDoesNotExist() {
        // Arrange
        String accountId = "ACC-NONEXISTENT";
        UpdateLimitsRequest request = new UpdateLimitsRequest(
            BigDecimal.valueOf(1000.00), BigDecimal.valueOf(1000.00)
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.updateLimits(accountId, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("not found");

        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, never()).save(any(Account.class));
    }
}
