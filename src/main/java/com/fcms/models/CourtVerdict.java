package com.fcms.models;

import java.time.LocalDate;

public class CourtVerdict {

    private String verdictID;
    private String outcome;     // guilty / not guilty
    private String sentence;
    private LocalDate dateIssued;
    private String notes;
    private String caseID;
    private String issuedBy;

    public CourtVerdict(String verdictID, String outcome, String sentence,
                        LocalDate dateIssued, String notes, String caseID, String issuedBy) {
        this.verdictID = verdictID;
        this.outcome = outcome;
        this.sentence = sentence;
        this.dateIssued = dateIssued;
        this.notes = notes;
        this.caseID = caseID;
        this.issuedBy = issuedBy;
    }

    public String getVerdictID() { return verdictID; }
    public String getOutcome() { return outcome; }
    public String getSentence() { return sentence; }
    public LocalDate getDateIssued() { return dateIssued; }
    public String getNotes() { return notes; }
    public String getCaseID() { return caseID; }
    public String getIssuedBy() { return issuedBy; }
}