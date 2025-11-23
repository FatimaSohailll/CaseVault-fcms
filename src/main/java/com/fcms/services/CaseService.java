package com.fcms.services;

import com.fcms.models.Case;
import com.fcms.models.Evidence;
import com.fcms.models.Participant;
import com.fcms.repositories.CaseRepository;
import com.fcms.repositories.EvidenceRepository;
import com.fcms.repositories.ParticipantRepository;

import java.time.LocalDate;
import java.util.List;

public class CaseService {

    private final CaseRepository caseRepository;
    private final EvidenceRepository evidenceRepository;
    private final ParticipantRepository participantRepository;

    private String caseID;

    public CaseService(String caseID) {
        this.caseID = caseID;
        this.caseRepository = new CaseRepository();
        this.evidenceRepository = new EvidenceRepository();
        this.participantRepository = new ParticipantRepository();
    }

    public CaseService() {
        this.caseRepository = new CaseRepository();
        this.evidenceRepository = new EvidenceRepository();
        this.participantRepository = new ParticipantRepository();
    }

    // ---------------- Validation ----------------
    public String validateCaseInput(String title, String type, String description, LocalDate date) {
        if (title == null || title.isEmpty()) return "Title is required";
        if (type == null || type.isEmpty()) return "Type is required";
        if (description == null || description.isEmpty()) return "Description is required";
        if (date == null) return "Date is required";
        return null;
    }

    // ---------------- Case Management ----------------
    public void registerCase(Case newCase) {
        caseRepository.save(newCase);
        System.out.println("Case registered: " + newCase.getTitle());
    }

    public List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    public Case getCaseById(String id) {
        return caseRepository.findById(id);
    }

    public void updateCase(Case updatedCase) {
        caseRepository.update(updatedCase);
        System.out.println("Case updated: " + updatedCase.getId());
    }

    public void closeCase(String caseId, String reason, String report) {
        caseRepository.closeCase(caseId, reason, report);
        System.out.println("Case " + caseId + " closed. Reason: " + reason);
        System.out.println("Final Report: " + report);
    }

    public void deleteCase(String caseId) {
        caseRepository.delete(caseId);
        System.out.println("Case deleted: " + caseId);
    }

    public boolean caseExists(String caseId) {
        return caseRepository.exists(caseId);
    }

    // ---------------- Evidence Management ----------------
    public void addEvidenceToCase(String caseId, Evidence evidence) {
        evidence.setCaseId(caseId);
        evidenceRepository.save(evidence);
    }

    public List<Evidence> getEvidenceForCase(String caseId) {
        return evidenceRepository.findByCaseId(caseId);
    }

    // ---------------- Participant Management ----------------
    public void addParticipantToCase(String caseId, Participant participant) {
        participantRepository.save(participant);
        // TODO: also insert into CaseParticipants junction table
        System.out.println("Linked participant " + participant.getId() + " to case " + caseId);
    }

    public List<Participant> getParticipantsForCase(String caseId) {
        // TODO: Actually query junction table
        return participantRepository.findAll();
    }

    public void submitCaseToCourt(String caseId, String courtOfficialId) {
        caseRepository.submitCaseToCourt(caseId, courtOfficialId);
    }

    public List<Case> getCasesByOfficer(String currentOfficerId){
        return caseRepository.getCasesByOfficer(currentOfficerId);
    }

    /* ============================================================
       NEW METHODS — USED BY SubmitToCourtController
       ============================================================ */

    public int countEvidenceForCase(String caseId) {
        return caseRepository.countEvidence(caseId);
    }

    public int countForensicReportsForCase(String caseId) {
        return caseRepository.countForensicReports(caseId);
    }


}
