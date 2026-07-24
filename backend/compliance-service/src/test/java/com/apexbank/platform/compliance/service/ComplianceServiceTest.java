package com.apexbank.platform.compliance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.apexbank.platform.compliance.dto.ComplianceResponse;
import com.apexbank.platform.compliance.dto.SubmitComplianceRequest;
import com.apexbank.platform.compliance.model.ComplianceReport;
import com.apexbank.platform.compliance.repository.ComplianceReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ComplianceServiceTest {

    @Mock
    private ComplianceReportRepository reportRepository;

    private ComplianceService complianceService;

    @BeforeEach
    void setUp() {
        this.complianceService = new ComplianceServiceImpl(reportRepository);
    }

    @Test
    void submitReport_Success() {
        // Arrange
        SubmitComplianceRequest request = new SubmitComplianceRequest(
            "ACC-12345678", 85, "Multiple velocity flags and geo-hop transactions",
            "AUTO_FREEZE", "# Suspicious Activity Report..."
        );

        when(reportRepository.save(any(ComplianceReport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ComplianceResponse response = complianceService.submitReport(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).startsWith("COMP-");
        assertThat(response.accountId()).isEqualTo("ACC-12345678");
        assertThat(response.riskScore()).isEqualTo(85);
        assertThat(response.actionsTaken()).isEqualTo("AUTO_FREEZE");
        assertThat(response.draftedSar()).isEqualTo("# Suspicious Activity Report...");

        verify(reportRepository, times(1)).save(any(ComplianceReport.class));
    }

    @Test
    void getReportsByAccountId_Success() {
        // Arrange
        String accountId = "ACC-12345678";
        ComplianceReport mockReport = new ComplianceReport(
            "COMP-9999", accountId, 60, "Excessive transfers", "MONITOR", null
        );

        when(reportRepository.findByAccountId(accountId)).thenReturn(Collections.singletonList(mockReport));

        // Act
        List<ComplianceResponse> responses = complianceService.getReportsByAccountId(accountId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo("COMP-9999");
        assertThat(responses.get(0).riskScore()).isEqualTo(60);

        verify(reportRepository, times(1)).findByAccountId(accountId);
    }
}
