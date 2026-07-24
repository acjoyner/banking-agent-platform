package com.apexbank.platform.transaction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.apexbank.platform.transaction.dto.SubmitTransactionRequest;
import com.apexbank.platform.transaction.dto.TransactionResponse;
import com.apexbank.platform.transaction.exception.BadRequestException;
import com.apexbank.platform.transaction.model.Transaction;
import com.apexbank.platform.transaction.model.Transaction.TransactionType;
import com.apexbank.platform.transaction.model.Transaction.TransactionStatus;
import com.apexbank.platform.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RestTemplate restTemplate;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        this.transactionService = new TransactionServiceImpl(transactionRepository, restTemplate);
        ReflectionTestUtils.setField(transactionService, "accountServiceUrl", "http://localhost:8081");
    }

    @Test
    void submitTransaction_Deposit_Success() {
        // Arrange
        SubmitTransactionRequest request = new SubmitTransactionRequest(
            "ACC-12345678", BigDecimal.valueOf(500.00), TransactionType.DEPOSIT,
            "Target Merchant", "New York", null
        );

        doNothing().when(restTemplate).put(anyString(), eq(null));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponse response = transactionService.submitTransaction(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).startsWith("TX-");
        assertThat(response.status()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(response.amount()).isEqualByComparingTo("500.00");
        assertThat(response.transactionType()).isEqualTo(TransactionType.DEPOSIT);

        verify(restTemplate, times(1)).put(anyString(), eq(null));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void submitTransaction_Withdrawal_FlagsTransaction_WhenOverThreshold() {
        // Arrange
        SubmitTransactionRequest request = new SubmitTransactionRequest(
            "ACC-12345678", BigDecimal.valueOf(15000.00), TransactionType.WITHDRAWAL,
            "Target Merchant", "New York", null
        );

        doNothing().when(restTemplate).put(anyString(), eq(null));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponse response = transactionService.submitTransaction(request);

        // Assert
        assertThat(response.status()).isEqualTo(TransactionStatus.FLAGGED); // > $10,000 flags transaction
        assertThat(response.amount()).isEqualByComparingTo("15000.00");

        verify(restTemplate, times(1)).put(anyString(), eq(null));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void submitTransaction_ThrowsBadRequest_WhenAccountServiceFails() {
        // Arrange
        SubmitTransactionRequest request = new SubmitTransactionRequest(
            "ACC-12345678", BigDecimal.valueOf(500.00), TransactionType.WITHDRAWAL,
            "Target Merchant", "New York", null
        );

        HttpClientErrorException exception = HttpClientErrorException.create(
            org.springframework.http.HttpStatus.BAD_REQUEST, "Insufficient funds",
            org.springframework.http.HttpHeaders.EMPTY, "Insufficient funds".getBytes(StandardCharsets.UTF_8), null
        );

        doThrow(exception).when(restTemplate).put(anyString(), eq(null));

        // Act & Assert
        assertThatThrownBy(() -> transactionService.submitTransaction(request))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Account operation failed");

        verify(restTemplate, times(1)).put(anyString(), eq(null));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
