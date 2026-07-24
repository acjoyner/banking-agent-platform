package com.apexbank.platform.compliance.repository;

import com.apexbank.platform.compliance.model.ComplianceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplianceReportRepository extends JpaRepository<ComplianceReport, String> {
    List<ComplianceReport> findByAccountId(String accountId);
}
