package com.fcms.models;

import java.time.LocalDateTime;

public class ChainOfCustody {
    private String recordId;
    private String evidenceId;
    private LocalDateTime timestamp;
    private String action;
    private String doneBy;

    // Constructors
    public ChainOfCustody() {}

    public ChainOfCustody(String recordId, String evidenceId, String action, String doneBy) {
        this.recordId = recordId;
        this.evidenceId = evidenceId;
        this.timestamp = LocalDateTime.now();
        this.action = action;
        this.doneBy = doneBy;
    }

    // Getters and Setters
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getEvidenceId() { return evidenceId; }
    public void setEvidenceId(String evidenceId) { this.evidenceId = evidenceId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDoneBy() { return doneBy; }
    public void setDoneBy(String doneBy) { this.doneBy = doneBy; }

    @Override
    public String toString() {
        return "ChainOfCustody{" +
                "recordId='" + recordId + '\'' +
                ", evidenceId='" + evidenceId + '\'' +
                ", timestamp=" + timestamp +
                ", action='" + action + '\'' +
                ", doneBy='" + doneBy + '\'' +
                '}';
    }
}