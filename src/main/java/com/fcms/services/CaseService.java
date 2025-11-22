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

    public void closeCase(String caseId, String reason, String finalReport) {
        Case c = caseRepository.findById(caseId);
        if (c != null) {
            c.setStatus("Closed - " + reason);
            caseRepository.update(c); // persist change
            System.out.println("Case " + caseId + " closed. Reason: " + reason);
            System.out.println("Final Report: " + finalReport);
        }
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
        // You may need a junction table CaseParticipants in DB
        participantRepository.save(participant);
        System.out.println("Linked participant " + participant.getId() + " to case " + caseId);
    }

    public List<Participant> getParticipantsForCase(String caseId) {
        // If you implement CaseParticipants table, query it here
        return participantRepository.findAll(); // placeholder
    }
}