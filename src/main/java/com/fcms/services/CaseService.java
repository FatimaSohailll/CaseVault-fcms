package com.fcms.services;

import com.fcms.database.SQLiteDatabase;
import com.fcms.models.Case;
import com.fcms.models.Evidence;
import com.fcms.models.Participant;
import com.fcms.repositories.CaseRepository;
import com.fcms.repositories.EvidenceRepository;
import com.fcms.repositories.ForensicRequestRepository;
import com.fcms.repositories.ParticipantRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CaseService {

    private final CaseRepository caseRepository;
    private final EvidenceRepository evidenceRepository;
    private final ParticipantRepository participantRepository;
    private final ForensicRequestRepository forensicRequestRepository = new ForensicRequestRepository(null);
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
    public int countByAssignedOfficer(String assigned) {
        String sql = "SELECT COUNT(1) AS cnt FROM CaseFile WHERE assignedOfficer = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assigned);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countByAssignedOfficerAndStatus(String assigned, String status) {
        String sql = "SELECT COUNT(1) AS cnt FROM CaseFile WHERE assignedOfficer = ? AND LOWER(status) = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assigned);
            ps.setString(2, status.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countClosedThisMonthForOfficer(String assigned, int year, int month) {
        String sql = """
            SELECT COUNT(1) AS cnt
            FROM CaseFile
            WHERE assignedOfficer = ?
              AND LOWER(status) = 'closed'
              AND strftime('%Y', dateRegistered) = ?
              AND strftime('%m', dateRegistered) = ?
            """;
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assigned);
            ps.setString(2, String.valueOf(year));
            ps.setString(3, String.format("%02d", month));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countPendingAnalysisForOfficer(String assigned) {
        ForensicRequestRepository repo = new ForensicRequestRepository(null); // or new ForensicRequestRepository()
        return repo.countPendingForOfficer(assigned);
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

    public boolean closeCase(String caseId, String reason, String report) {
        boolean ok = caseRepository.closeCase(caseId, reason, report);
        if (ok) {
            System.out.println("CaseService: case closed: " + caseId);
        } else {
            System.out.println("CaseService: failed to close case: " + caseId);
        }
        return ok;
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

    public List<Case> getSubmittedCasesForOfficial(String officialId) {
        return caseRepository.findSubmittedForOfficial(officialId);
    }
}
