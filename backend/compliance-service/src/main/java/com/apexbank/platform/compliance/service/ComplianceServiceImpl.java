package com.apexbank.platform.compliance.service;

import com.apexbank.platform.compliance.dto.ComplianceResponse;
import com.apexbank.platform.compliance.dto.SubmitComplianceRequest;
import com.apexbank.platform.compliance.model.ComplianceReport;
import com.apexbank.platform.compliance.repository.ComplianceReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceReportRepository reportRepository;

    @Autowired
    public ComplianceServiceImpl(ComplianceReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    @Transactional
    public ComplianceResponse submitReport(SubmitComplianceRequest request) {
        // Generate custom Compliance Report ID: COMP-XXXXXXXX
        String id = "COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ComplianceReport report = new ComplianceReport(
            id,
            request.accountId(),
            request.riskScore(),
            request.reasoning(),
            request.actionsTaken(),
            request.draftedSar()
        );

        ComplianceReport saved = reportRepository.save(report);
        return ComplianceResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplianceResponse> getReportsByAccountId(String accountId) {
        return reportRepository.findByAccountId(accountId).stream()
            .map(ComplianceResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplianceResponse> getAllReports() {
        return reportRepository.findAll().stream()
            .map(ComplianceResponse::fromEntity)
            .collect(Collectors.toList());
    }
}
