package com.fcms.repositories;

import com.fcms.models.Evidence;
import com.fcms.database.SQLiteDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EvidenceRepository {
    private List<Evidence> evidenceList;
    private String caseId;

    public EvidenceRepository() {
        this.evidenceList = new ArrayList<>();
    }

    public EvidenceRepository(String caseId) {
        this.caseId = caseId;
        this.evidenceList = new ArrayList<>();
    }

    public void save(Evidence evidence) {
        String sql = "INSERT INTO Evidence (evidenceID, type, description, filename, location, collectionDate, caseID) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, evidence.getId());
            pstmt.setString(2, evidence.getType());
            pstmt.setString(3, evidence.getDescription());
            pstmt.setString(4, evidence.getFileName());
            pstmt.setString(5, evidence.getLocation());

            // Handle date as string to avoid parsing issues
            if (evidence.getCollectionDateTime() != null && !evidence.getCollectionDateTime().isEmpty()) {
                String dateTime = evidence.getCollectionDateTime();
                // Extract just the date part (before the space)
                String datePart = dateTime.contains(" ") ? dateTime.split(" ")[0] : dateTime;
                pstmt.setString(6, datePart);
            } else {
                pstmt.setString(6, LocalDate.now().toString());
            }

            pstmt.setString(7, evidence.getCaseId());

            pstmt.executeUpdate();
            System.out.println("Saved evidence to database: " + evidence.getId());

        } catch (SQLException e) {
            System.out.println("Error saving evidence: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Evidence> findAll() {
        List<Evidence> evidenceList = new ArrayList<>();
        String sql = "SELECT * FROM Evidence";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Evidence evidence = mapResultSetToEvidence(rs);
                evidenceList.add(evidence);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching all evidence: " + e.getMessage());
            e.printStackTrace();
        }

        return evidenceList;
    }

    public List<Evidence> findByCaseId(String caseId) {
        List<Evidence> evidenceList = new ArrayList<>();
        String sql = "SELECT * FROM Evidence WHERE caseID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, caseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Evidence evidence = mapResultSetToEvidence(rs);
                evidenceList.add(evidence);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching evidence by case: " + e.getMessage());
            e.printStackTrace();
        }

        return evidenceList;
    }

    public Evidence findById(String id) {
        String sql = "SELECT * FROM Evidence WHERE evidenceID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEvidence(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error finding evidence by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private Evidence mapResultSetToEvidence(ResultSet rs) throws SQLException {
        Evidence evidence = new Evidence();
        evidence.setId(rs.getString("evidenceID"));
        evidence.setType(rs.getString("type"));
        evidence.setDescription(rs.getString("description"));
        evidence.setFileName(rs.getString("filename"));
        evidence.setLocation(rs.getString("location"));

        // Handle date as string to avoid parsing issues
        String collectionDate = rs.getString("collectionDate");
        if (collectionDate != null) {
            evidence.setCollectionDateTime(collectionDate);
        } else {
            evidence.setCollectionDateTime(LocalDate.now().toString());
        }

        evidence.setCaseId(rs.getString("caseID"));
        return evidence;
    }

    // Add method to clear existing problematic data if needed
    public void clearAllEvidence() {
        String sql = "DELETE FROM Evidence";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Cleared all evidence data");

        } catch (SQLException e) {
            System.out.println("Error clearing evidence: " + e.getMessage());
            e.printStackTrace();
        }
    }
}