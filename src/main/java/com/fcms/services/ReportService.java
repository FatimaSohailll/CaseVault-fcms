package com.fcms.services;

import com.fcms.models.ForensicReport;
import com.fcms.repositories.ReportRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportService {
    private ReportRepository reportRepository;
    private ForensicRequestService requestService;

    String expertID;

    // Configure this path according to your application structure
    private static final String UPLOAD_DIR = "uploads/reports/";

    public ReportService(String expertId) {
        this.expertID = expertId;
        this.reportRepository = new ReportRepository();
        this.requestService = new ForensicRequestService(expertId);
        createUploadDirectory();
    }

    public void uploadReport(ForensicReport report, File file) throws BusinessException {
        try {
            // Business validation
            validateReport(report, file);

            // Generate report ID
            report.setReportId(reportRepository.generateReportId());

            // Handle file upload
            String savedFileName = saveUploadedFile(file, report.getReportId());
            report.setFileName(savedFileName);
            report.setStatus("submitted");
            report.setUploadDate(LocalDate.now());

            // Set uploaded by (you need to get this from session/context)
            report.setUploadedBy(expertID);

            // Save to database
            reportRepository.save(report);

        } catch (SQLException e) {
            throw new BusinessException("Database error while uploading report: " + e.getMessage());
        } catch (IOException e) {
            throw new BusinessException("File upload error: " + e.getMessage());
        }
    }

    public void linkReportToRequest(String reportId, String requestId) throws BusinessException {
        try {
            ForensicReport report = reportRepository.findById(reportId);
            if (report == null) {
                throw new BusinessException("Report not found");
            }

            // Update request status
            requestService.updateRequestStatus(requestId, "completed");

            // The request ID is already set during report creation, but we can update if needed
            report.setRequestId(requestId);
            reportRepository.update(report);

        } catch (SQLException e) {
            throw new BusinessException("Database error while linking report to request: " + e.getMessage());
        }
    }

    public ForensicReport getReportById(String reportId) throws BusinessException {
        try {
            ForensicReport report = reportRepository.findById(reportId);
            if (report == null) {
                throw new BusinessException("Report not found");
            }
            return report;
        } catch (SQLException e) {
            throw new BusinessException("Database error while retrieving report: " + e.getMessage());
        }
    }

    public File getReportFile(String reportId) throws BusinessException {
        try {
            ForensicReport report = reportRepository.findById(reportId);
            if (report == null) {
                throw new BusinessException("Report not found");
            }

            if (report.getFileName() == null) {
                throw new BusinessException("No file associated with this report");
            }

            Path filePath = Path.of(UPLOAD_DIR, report.getFileName());
            if (!Files.exists(filePath)) {
                throw new BusinessException("Report file not found on server");
            }

            return filePath.toFile();

        } catch (SQLException e) {
            throw new BusinessException("Database error while retrieving report file: " + e.getMessage());
        }
    }

    private void validateReport(ForensicReport report, File file) throws BusinessException {
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
    }

    private String saveUploadedFile(File file, String reportId) throws IOException {
        String fileExtension = getFileExtension(file.getName());
        String savedFileName = reportId + "." + fileExtension;
        Path targetPath = Path.of(UPLOAD_DIR, savedFileName);

        Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return savedFileName;
    }

    private void createUploadDirectory() {
        try {
            Path uploadPath = Path.of(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not create upload directory: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    private boolean isValidReportFileType(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx");
    }

}