package com.fcms.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ForensicRequest {
    private String id;
    private String requestId;
    private String caseId;
    private String evidenceType;
    private String requestedBy;
    private LocalDate date;
    private String status;
    private String evidenceDetails;
    private String analysisType;
    private String priority;
    private List<String> evidenceIds; // Added missing field

    // Full constructor
    public ForensicRequest(String id, String requestId, String caseId, String evidenceType,
                           String requestedBy, LocalDate date, String status, String evidenceDetails,
                           String analysisType, String priority) {
        this.id = id;
        this.requestId = requestId;
        this.caseId = caseId;
        this.evidenceType = evidenceType;
        this.requestedBy = requestedBy;
        this.date = date;
        this.status = status;
        this.evidenceDetails = evidenceDetails;
        this.analysisType = analysisType;
        this.priority = priority;
        this.evidenceIds = new ArrayList<>(); // Initialize the list
    }

    // Simplified constructor for new requests
    public ForensicRequest(String caseId, String analysisType, List<String> evidenceIds, String requestedBy) {
        this.caseId = caseId;
        this.analysisType = analysisType;
        this.evidenceIds = evidenceIds != null ? new ArrayList<>(evidenceIds) : new ArrayList<>();
        this.requestedBy = requestedBy;
        this.date = LocalDate.now();
        this.status = "Pending";
        this.priority = "Normal";
    }

    // Default constructor
    public ForensicRequest() {
        this.evidenceIds = new ArrayList<>();
        this.date = LocalDate.now();
        this.status = "Pending";
        this.priority = "Normal";
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(String evidenceType) {
        this.evidenceType = evidenceType;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEvidenceDetails() {
        return evidenceDetails;
    }

    public void setEvidenceDetails(String evidenceDetails) {
        this.evidenceDetails = evidenceDetails;
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

    // Evidence IDs methods
    public List<String> getEvidenceIds() {
        return new ArrayList<>(evidenceIds); // Return copy to prevent external modification
    }

    public void setEvidenceIds(List<String> evidenceIds) {
        this.evidenceIds = evidenceIds != null ? new ArrayList<>(evidenceIds) : new ArrayList<>();
    }

    public void addEvidenceId(String evidenceId) {
        if (evidenceId != null && !evidenceIds.contains(evidenceId)) {
            evidenceIds.add(evidenceId);
        }
    }

    public void removeEvidenceId(String evidenceId) {
        evidenceIds.remove(evidenceId);
    }

    public boolean hasEvidence() {
        return !evidenceIds.isEmpty();
    }

    public int getEvidenceCount() {
        return evidenceIds.size();
    }

    // Business logic methods
    public boolean isPending() {
        return "Pending".equalsIgnoreCase(status);
    }

    public boolean isInProgress() {
        return "In Progress".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "Cancelled".equalsIgnoreCase(status);
    }

    public boolean canBeCancelled() {
        return !isCompleted() && !isCancelled();
    }

    // Validation method
    public boolean isValid() {
        return caseId != null && !caseId.trim().isEmpty() &&
                analysisType != null && !analysisType.trim().isEmpty() &&
                !evidenceIds.isEmpty() &&
                requestedBy != null && !requestedBy.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "ForensicRequest{" +
                "requestId='" + requestId + '\'' +
                ", caseId='" + caseId + '\'' +
                ", analysisType='" + analysisType + '\'' +
                ", status='" + status + '\'' +
                ", evidenceCount=" + getEvidenceCount() +
                '}';
    }
}