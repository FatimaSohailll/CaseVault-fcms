package com.fcms.models;

import java.time.LocalDate;

public class ForensicRequest {
    private String requestId;
    private String expertId; // Added this field
    private String status;
    private String requestedBy;
    private String evidenceType;
    private LocalDate requestedDate;
    private String evidenceId;
    private String analysisType;
    private String priority;

    // Full constructor matching database table
    public ForensicRequest(String requestId, String expertId, String status, String requestedBy,
                           String evidenceType, LocalDate requestedDate, String evidenceId,
                           String analysisType, String priority) {
        this.requestId = requestId;
        this.expertId = expertId;
        this.status = status;
        this.requestedBy = requestedBy;
        this.evidenceType = evidenceType;
        this.requestedDate = requestedDate;
        this.evidenceId = evidenceId;
        this.analysisType = analysisType;
        this.priority = priority;
    }

    public ForensicRequest(String analysisType, String evidenceId, String requestedBy, String expertId) {
        this.analysisType = analysisType;
        this.evidenceId = evidenceId;
        this.requestedBy = requestedBy;
        this.expertId = expertId;
        this.requestedDate = LocalDate.now();
        this.status = "pending";
        this.priority = "Medium";
        this.evidenceType = analysisType; // Use analysis type as evidence type by default
    }

    // Default constructor
    public ForensicRequest() {
        this.requestedDate = LocalDate.now();
        this.status = "pending";
        this.priority = "Medium";
    }

    // Getters and setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getExpertId() {
        return expertId;
    }

    public void setExpertId(String expertId) {
        this.expertId = expertId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(String evidenceType) {
        this.evidenceType = evidenceType;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    // For backward compatibility - alias for getRequestedDate()
    public LocalDate getDate() {
        return requestedDate;
    }

    // For backward compatibility - alias for setRequestedDate()
    public void setDate(LocalDate date) {
        this.requestedDate = date;
    }

    public String getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(String evidenceId) {
        this.evidenceId = evidenceId;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    // Business logic methods
    public boolean isPending() {
        return "pending".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }

    // Validation method
    public boolean isValid() {
        return requestId != null && !requestId.trim().isEmpty() &&
                requestedBy != null && !requestedBy.trim().isEmpty() &&
                evidenceId != null && !evidenceId.trim().isEmpty() &&
                status != null && !status.trim().isEmpty() &&
                priority != null && !priority.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "ForensicRequest{" +
                "requestId='" + requestId + '\'' +
                ", expertId='" + expertId + '\'' +
                ", status='" + status + '\'' +
                ", requestedBy='" + requestedBy + '\'' +
                ", evidenceType='" + evidenceType + '\'' +
                ", requestedDate=" + requestedDate +
                ", evidenceId='" + evidenceId + '\'' +
                ", analysisType='" + analysisType + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}