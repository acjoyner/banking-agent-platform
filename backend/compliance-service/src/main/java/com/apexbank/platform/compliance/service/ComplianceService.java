package com.apexbank.platform.compliance.service;

import com.apexbank.platform.compliance.dto.ComplianceResponse;
import com.apexbank.platform.compliance.dto.SubmitComplianceRequest;
import java.util.List;

public interface ComplianceService {
    ComplianceResponse submitReport(SubmitComplianceRequest request);
    List<ComplianceResponse> getReportsByAccountId(String accountId);
    List<ComplianceResponse> getAllReports();
}
