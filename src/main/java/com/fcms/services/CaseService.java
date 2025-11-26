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
    private final ForensicRequestRepository forensicRequestRepository;
    private String caseID;

    public CaseService(String caseID) {
        this.caseID = caseID;
        this.caseRepository = new CaseRepository();
        this.evidenceRepository = new EvidenceRepository();
        this.participantRepository = new ParticipantRepository();
        this.forensicRequestRepository = new ForensicRequestRepository(null);
    }

    public CaseService() {
        this.caseRepository = new CaseRepository();
        this.evidenceRepository = new EvidenceRepository();
        this.participantRepository = new ParticipantRepository();
        this.forensicRequestRepository = new ForensicRequestRepository(null);

    }

    // ---------------- Validation ----------------
    public String validateCaseInput(String title, String type, String description, LocalDate date) {
        if (title == null || title.isEmpty()) return "Title is required";
        if (type == null || type.isEmpty()) return "Type is required";
        if (description == null || description.isEmpty()) return "Description is required";
        if (date == null) return "Date is required";
        return null;
    }

    // ---------------- Case ID Generator ----------------
    public int getNextCaseNumber() {
        String sql = "SELECT MAX(caseID) AS maxId FROM CaseFile";
        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next() && rs.getString("maxId") != null) {
                String maxId = rs.getString("maxId"); // e.g. CS00012
                int num = Integer.parseInt(maxId.substring(2)); // strip "CS"
                return num + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // start at CS00001
    }

    public String generateCaseId() {
        int nextNumber = getNextCaseNumber();
        return String.format("CS%05d", nextNumber);
    }

    // ---------------- Counts ----------------
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

    public List<Case> findRecentByAssignedOfficer(String assigned, int limit) {
        String sql = "SELECT caseID, title, type, assignedOfficer, location, dateRegistered, status, priority " +
                "FROM CaseFile WHERE assignedOfficer = ? ORDER BY dateRegistered DESC LIMIT ?";
        List<Case> list = new ArrayList<>();
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assigned);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Case c = new Case();
                    c.setId(rs.getString("caseID"));
                    c.setTitle(rs.getString("title"));
                    c.setType(rs.getString("type"));
                    c.setAssignedOfficer(rs.getString("assignedOfficer"));
                    c.setLocation(rs.getString("location"));
                    Date dr = rs.getDate("dateRegistered");
                    c.setDateRegistered(dr != null ? dr.toLocalDate() : null);
                    c.setStatus(rs.getString("status"));
                    c.setPriority(rs.getString("priority"));
                    list.add(c);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int countPendingAnalysisForOfficer(String assigned) {
        return forensicRequestRepository.countPendingForOfficer(assigned);
    }

    // ---------------- Case Management ----------------
    public void registerCase(Case newCase) {
        caseRepository.save(newCase);
        System.out.println("Case registered: " + newCase.getId() + " (" + newCase.getTitle() + ")");
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
