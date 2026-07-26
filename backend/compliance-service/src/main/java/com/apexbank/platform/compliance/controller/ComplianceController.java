package com.apexbank.platform.compliance.controller;

import com.apexbank.platform.compliance.dto.ComplianceResponse;
import com.apexbank.platform.compliance.dto.SubmitComplianceRequest;
import com.apexbank.platform.compliance.service.ComplianceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/compliance/reports")
public class ComplianceController {

    private final ComplianceService complianceService;

    @Autowired
    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @PostMapping
    public ResponseEntity<ComplianceResponse> submitReport(@Valid @RequestBody SubmitComplianceRequest request) {
        ComplianceResponse response = complianceService.submitReport(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/audit/{accountId}")
    public ResponseEntity<ComplianceResponse> runAudit(
            @PathVariable String accountId,
            @RequestParam(required = false) String transactionId,
            @RequestParam(required = false, defaultValue = "MANUAL") String triggerType) {
        ComplianceResponse response = complianceService.runAudit(accountId, transactionId, triggerType);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<ComplianceResponse>> getReportsByAccountId(@PathVariable String accountId) {
        List<ComplianceResponse> responses = complianceService.getReportsByAccountId(accountId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<ComplianceResponse>> getAllReports() {
        List<ComplianceResponse> responses = complianceService.getAllReports();
        return ResponseEntity.ok(responses);
    }
}
