package com.fcms.repositories;

import com.fcms.models.Evidence;
import com.fcms.database.SQLiteDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public void update(Evidence evidence) {
        String sql = "UPDATE Evidence SET type = ?, description = ?, filename = ?, location = ?, collectionDate = ?, caseID = ? WHERE evidenceID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, evidence.getType());
            pstmt.setString(2, evidence.getDescription());
            pstmt.setString(3, evidence.getFileName());
            pstmt.setString(4, evidence.getLocation());

            // Handle date as string to avoid parsing issues
            if (evidence.getCollectionDateTime() != null && !evidence.getCollectionDateTime().isEmpty()) {
                String dateTime = evidence.getCollectionDateTime();
                // Extract just the date part (before the space)
                String datePart = dateTime.contains(" ") ? dateTime.split(" ")[0] : dateTime;
                pstmt.setString(5, datePart);
            } else {
                pstmt.setString(5, LocalDate.now().toString());
            }

            pstmt.setString(6, evidence.getCaseId());
            pstmt.setString(7, evidence.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Updated evidence in database: " + evidence.getId());
            } else {
                System.out.println("No evidence found with ID: " + evidence.getId());
            }

        } catch (SQLException e) {
            System.out.println("Error updating evidence: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(String evidenceId) {
        String sql = "DELETE FROM Evidence WHERE evidenceID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, evidenceId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Deleted evidence from database: " + evidenceId);
            } else {
                System.out.println("No evidence found with ID: " + evidenceId);
            }

        } catch (SQLException e) {
            System.out.println("Error deleting evidence: " + e.getMessage());
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

    public List<Evidence> search(String searchTerm) {
        List<Evidence> evidenceList = new ArrayList<>();
        String sql = "SELECT * FROM Evidence WHERE evidenceID LIKE ? OR type LIKE ? OR description LIKE ? OR location LIKE ? OR filename LIKE ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);
            pstmt.setString(4, likeTerm);
            pstmt.setString(5, likeTerm);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Evidence evidence = mapResultSetToEvidence(rs);
                evidenceList.add(evidence);
            }

        } catch (SQLException e) {
            System.out.println("Error searching evidence: " + e.getMessage());
            e.printStackTrace();
        }

        return evidenceList;
    }

    public List<Evidence> findByType(String evidenceType) {
        List<Evidence> evidenceList = new ArrayList<>();
        String sql = "SELECT * FROM Evidence WHERE type = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, evidenceType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Evidence evidence = mapResultSetToEvidence(rs);
                evidenceList.add(evidence);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching evidence by type: " + e.getMessage());
            e.printStackTrace();
        }

        return evidenceList;
    }

    public List<Evidence> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Evidence> evidenceList = new ArrayList<>();
        String sql = "SELECT * FROM Evidence WHERE collectionDate BETWEEN ? AND ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Convert LocalDateTime to string format for SQLite
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDateStr = startDate.format(formatter);
            String endDateStr = endDate.format(formatter);

            pstmt.setString(1, startDateStr);
            pstmt.setString(2, endDateStr);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Evidence evidence = mapResultSetToEvidence(rs);
                evidenceList.add(evidence);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching evidence by date range: " + e.getMessage());
            e.printStackTrace();
        }

        return evidenceList;
    }

    public int getCountByCase(String caseId) {
        String sql = "SELECT COUNT(*) as count FROM Evidence WHERE caseID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, caseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.out.println("Error counting evidence by case: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public boolean exists(String evidenceId) {
        String sql = "SELECT 1 FROM Evidence WHERE evidenceID = ? LIMIT 1";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, evidenceId);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking if evidence exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getAllEvidenceTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT type FROM Evidence ORDER BY type";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                types.add(rs.getString("type"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching evidence types: " + e.getMessage());
            e.printStackTrace();
        }

        return types;
    }

    public List<Evidence> findRecentEvidence(int limit) {
        List<Evidence> evidenceList = new ArrayList<>();
        String sql = "SELECT * FROM Evidence ORDER BY collectionDate DESC LIMIT ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Evidence evidence = mapResultSetToEvidence(rs);
                evidenceList.add(evidence);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching recent evidence: " + e.getMessage());
            e.printStackTrace();
        }

        return evidenceList;
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

    // Method to get the next available evidence ID
    public String getNextEvidenceId() {
        String sql = "SELECT evidenceID FROM Evidence ORDER BY evidenceID DESC LIMIT 1";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("evidenceID");
                if (lastId != null && lastId.startsWith("EV")) {
                    try {
                        int lastNumber = Integer.parseInt(lastId.substring(2));
                        return "EV" + String.format("%04d", lastNumber + 1);
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing evidence ID: " + lastId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting next evidence ID: " + e.getMessage());
            e.printStackTrace();
        }

        // Default starting ID if no evidence exists
        return "EV0001";
    }
}