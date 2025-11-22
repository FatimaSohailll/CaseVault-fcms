package com.fcms.models;

import java.io.File;
import java.time.LocalDate;

public class ForensicReport {
    private String reportId;
    private String requestId;
    private String title;
    private LocalDate completionDate;
    private String fileName;
    private File file;
    private String notes;
    private String status;
    private LocalDate uploadDate;
    private String uploadedBy;

    // Constructors
    public ForensicReport() {
        this.uploadDate = LocalDate.now();
        this.status = "Draft";
    }

    public ForensicReport(String reportId, String requestId, String title, LocalDate completionDate) {
        this();
        this.reportId = reportId;
        this.requestId = requestId;
        this.title = title;
        this.completionDate = completionDate;
    }

    public ForensicReport(String reportId, String requestId, String title, LocalDate completionDate,
                          String fileName, String notes) {
        this(reportId, requestId, title, completionDate);
        this.fileName = fileName;
        this.notes = notes;
    }

    // Getters and Setters
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    // Business methods
    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(status);
    }

    public boolean hasFile() {
        return fileName != null && !fileName.trim().isEmpty();
    }

    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public boolean isPdf() {
        return "pdf".equalsIgnoreCase(getFileExtension());
    }

    public void setFileData(File file) {
        if (file != null) {
            this.file = file;
            this.fileName = file.getName();
        }
    }

    public boolean isWordDocument() {
        String ext = getFileExtension();
        return "doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext);
    }

    // Validation methods
    public boolean isValid() {
        return reportId != null && !reportId.trim().isEmpty() &&
                requestId != null && !requestId.trim().isEmpty() &&
                title != null && !title.trim().isEmpty() &&
                completionDate != null;
    }

    public boolean isOverdue() {
        return completionDate != null && completionDate.isBefore(LocalDate.now()) && !isCompleted();
    }

    // Utility methods
    @Override
    public String toString() {
        return "ForensicReport{" +
                "reportId='" + reportId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", title='" + title + '\'' +
                ", completionDate=" + completionDate +
                ", fileName='" + fileName + '\'' +
                ", status='" + status + '\'' +
                ", uploadDate=" + uploadDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForensicReport that = (ForensicReport) o;
        return reportId != null ? reportId.equals(that.reportId) : that.reportId == null;
    }

    @Override
    public int hashCode() {
        return reportId != null ? reportId.hashCode() : 0;
    }

    // Builder pattern for easy object creation
    public static class Builder {
        private String reportId;
        private String requestId;
        private String title;
        private LocalDate completionDate;
        private String fileName;
        private File file;
        private String notes;
        private String status;
        private LocalDate uploadDate;
        private String uploadedBy;

        public Builder reportId(String reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder completionDate(LocalDate completionDate) {
            this.completionDate = completionDate;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder uploadDate(LocalDate uploadDate) {
            this.uploadDate = uploadDate;
            return this;
        }

        public Builder uploadedBy(String uploadedBy) {
            this.uploadedBy = uploadedBy;
            return this;
        }

        public ForensicReport build() {
            ForensicReport report = new ForensicReport();
            report.reportId = this.reportId;
            report.requestId = this.requestId;
            report.title = this.title;
            report.completionDate = this.completionDate;
            report.fileName = this.fileName;
            report.file = this.file;
            report.notes = this.notes;
            report.status = this.status;
            report.uploadDate = this.uploadDate != null ? this.uploadDate : LocalDate.now();
            report.uploadedBy = this.uploadedBy;
            return report;
        }
    }

    // Static factory methods
    public static Builder builder() {
        return new Builder();
    }

    public static ForensicReport createDraft(String requestId, String title) {
        return new ForensicReport(null, requestId, title, LocalDate.now().plusDays(7));
    }

    public static ForensicReport createCompletedReport(String reportId, String requestId,
                                                       String title, LocalDate completionDate, String fileName) {
        ForensicReport report = new ForensicReport(reportId, requestId, title, completionDate);
        report.setFileName(fileName);
        report.setStatus("Completed");
        return report;
    }
}