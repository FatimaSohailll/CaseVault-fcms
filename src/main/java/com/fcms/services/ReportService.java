package com.fcms.services;

import com.fcms.models.ForensicRequest;
import com.fcms.models.ForensicReport;
import com.fcms.repositories.ReportRepository;
import java.io.File;
import java.time.LocalDate;

public class ReportService {
    private ReportRepository reportRepository;

    public ReportService() {
        this.reportRepository = new ReportRepository();
    }

    // Business logic for uploading forensic report
    public void uploadReport(ForensicReport report, File file) throws BusinessException {
        // Business validation
        if (report.getRequestId() == null || report.getRequestId().trim().isEmpty()) {
            throw new BusinessException("Request ID is required");
        }
        if (report.getTitle() == null || report.getTitle().trim().isEmpty()) {
            throw new BusinessException("Report title is required");
        }
        if (report.getCompletionDate() == null) {
            throw new BusinessException("Completion date is required");
        }
        if (file == null) {
            throw new BusinessException("Report file is required");
        }

        // Business rule: Completion date cannot be in future
        if (report.getCompletionDate().isAfter(LocalDate.now())) {
            throw new BusinessException("Completion date cannot be in the future");
        }

        // Business rule: File size limit (10MB)
        if (file.length() > 10 * 1024 * 1024) {
            throw new BusinessException("File size must be less than 10MB");
        }

        // Business rule: Validate file type
        if (!isValidReportFileType(file)) {
            throw new BusinessException("Invalid file type. Allowed types: PDF, DOC, DOCX");
        }

        // Business rule: Generate report ID
        report.setReportId(generateReportId());
        report.setFileName(file.getName());
        report.setFile(file);

        // Delegate to repository
        reportRepository.save(report);
    }

    // Business logic for linking report to request
    public void linkReportToRequest(String reportId, String requestId) throws BusinessException {
        ForensicReport report = reportRepository.findById(reportId);
        if (report == null) {
            throw new BusinessException("Report not found");
        }

        // Business rule: Update request status
        ForensicRequestService requestService = new ForensicRequestService();
        requestService.updateRequestStatus(requestId, "Completed");

        report.setRequestId(requestId);
        reportRepository.update(report);
    }

    // Business logic for retrieving report by ID
    public ForensicReport getReportById(String reportId) throws BusinessException {
        ForensicReport report = reportRepository.findById(reportId);
        if (report == null) {
            throw new BusinessException("Report not found");
        }
        return report;
    }

    // Business rule: Validate report file type
    private boolean isValidReportFileType(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx");
    }

    // Business rule: Generate report ID
    private String generateReportId() {
        // Implementation depends on your ID generation strategy
        return "REP-" + System.currentTimeMillis();
    }
}