package com.fcms.repositories;

import com.fcms.models.Participant;
import com.fcms.database.SQLiteDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParticipantRepository {
    private String officerId;

    public ParticipantRepository(String officerId) {
        this.officerId = officerId;
    }

    public ParticipantRepository() {
        this.officerId = null; // For cases where no filtering is needed
    }

    public void save(Participant participant) {
        String sql = "INSERT INTO Participant (participantID, name, role, contact, idType, idNumber) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, participant.getId());
            pstmt.setString(2, participant.getName());
            pstmt.setString(3, participant.getRole().toLowerCase()); // Convert to lowercase for DB constraint
            pstmt.setString(4, participant.getContact());
            pstmt.setString(5, participant.getIdType());
            pstmt.setString(6, participant.getIdNumber());

            pstmt.executeUpdate();
            System.out.println("Saved participant to database: " + participant.getId());

        } catch (SQLException e) {
            System.out.println("Error saving participant: " + e.getMessage());
            throw new RuntimeException("Database error while saving participant", e);
        }
    }

    // NEW METHOD: Save participant and link to case in one transaction
    public void save(Participant participant, String caseId) {
        Connection conn = null;
        try {
            conn = SQLiteDatabase.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First save the participant
            String participantSql = "INSERT INTO Participant (participantID, name, role, contact, idType, idNumber) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(participantSql)) {
                pstmt.setString(1, participant.getId());
                pstmt.setString(2, participant.getName());
                pstmt.setString(3, participant.getRole().toLowerCase());
                pstmt.setString(4, participant.getContact());
                pstmt.setString(5, participant.getIdType());
                pstmt.setString(6, participant.getIdNumber());
                pstmt.executeUpdate();
            }

            // Then link to case if caseId is provided
            if (caseId != null && !caseId.trim().isEmpty()) {
                String caseLinkSql = "INSERT INTO CaseParticipants (caseID, participantID) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(caseLinkSql)) {
                    pstmt.setString(1, caseId);
                    pstmt.setString(2, participant.getId());
                    pstmt.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
            System.out.println("Saved participant to database and linked to case: " + participant.getId());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException rollbackEx) {
                    System.out.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            System.out.println("Error saving participant with case link: " + e.getMessage());
            throw new RuntimeException("Database error while saving participant with case link", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException closeEx) {
                    System.out.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public void update(Participant participant) {
        String sql = "UPDATE Participant SET name = ?, role = ?, contact = ?, idType = ?, idNumber = ? WHERE participantID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, participant.getName());
            pstmt.setString(2, participant.getRole().toLowerCase()); // Convert to lowercase for DB constraint
            pstmt.setString(3, participant.getContact());
            pstmt.setString(4, participant.getIdType());
            pstmt.setString(5, participant.getIdNumber());
            pstmt.setString(6, participant.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Participant not found: " + participant.getId());
            }
            System.out.println("Updated participant in database: " + participant.getId());

        } catch (SQLException e) {
            System.out.println("Error updating participant: " + e.getMessage());
            throw new RuntimeException("Database error while updating participant", e);
        }
    }

    public List<Participant> findAll() {
        List<Participant> participants = new ArrayList<>();
        String sql;

        if (officerId != null) {
            // Get participants from cases assigned to this officer
            sql = "SELECT DISTINCT p.* FROM Participant p " +
                    "JOIN CaseParticipants cp ON p.participantID = cp.participantID " +
                    "JOIN CaseFile cf ON cp.caseID = cf.caseID " +
                    "WHERE cf.assignedOfficer = ?";
        } else {
            // Get all participants (no filtering)
            sql = "SELECT * FROM Participant";
        }

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (officerId != null) {
                pstmt.setString(1, officerId);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Participant participant = mapResultSetToParticipant(rs);
                participants.add(participant);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching participants: " + e.getMessage());
            throw new RuntimeException("Database error while fetching participants", e);
        }

        return participants;
    }

    public Participant findById(String id) {
        String sql = "SELECT * FROM Participant WHERE participantID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToParticipant(rs);
            }
            return null;

        } catch (SQLException e) {
            System.out.println("Error finding participant by ID: " + e.getMessage());
            throw new RuntimeException("Database error while finding participant", e);
        }
    }

    public boolean exists(String id) {
        String sql = "SELECT 1 FROM Participant WHERE participantID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking participant existence: " + e.getMessage());
            throw new RuntimeException("Database error while checking participant existence", e);
        }
    }

    // Method to add participant to a case
    public void addParticipantToCase(String participantId, String caseId) {
        String sql = "INSERT INTO CaseParticipants (caseID, participantID) VALUES (?, ?)";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, caseId);
            pstmt.setString(2, participantId);
            pstmt.executeUpdate();
            System.out.println("Added participant " + participantId + " to case " + caseId);

        } catch (SQLException e) {
            System.out.println("Error adding participant to case: " + e.getMessage());
            throw new RuntimeException("Database error while adding participant to case", e);
        }
    }

    // Method to get participants by case ID
    public List<Participant> findByCaseId(String caseId) {
        List<Participant> participants = new ArrayList<>();
        String sql = "SELECT p.* FROM Participant p " +
                "JOIN CaseParticipants cp ON p.participantID = cp.participantID " +
                "WHERE cp.caseID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, caseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Participant participant = mapResultSetToParticipant(rs);
                participants.add(participant);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching participants by case: " + e.getMessage());
            throw new RuntimeException("Database error while fetching participants by case", e);
        }

        return participants;
    }

    // NEW METHOD: Check if participant is already in a case
    public boolean isParticipantInCase(String participantId, String caseId) {
        String sql = "SELECT 1 FROM CaseParticipants WHERE participantID = ? AND caseID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, participantId);
            pstmt.setString(2, caseId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking participant-case relationship: " + e.getMessage());
            throw new RuntimeException("Database error while checking participant-case relationship", e);
        }
    }

    // NEW METHOD: Check if case exists and is assigned to officer
    public boolean isCaseAssignedToOfficer(String caseId, String officerId) {
        String sql = "SELECT 1 FROM CaseFile WHERE caseID = ? AND assignedOfficer = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, caseId);
            pstmt.setString(2, officerId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking case assignment: " + e.getMessage());
            throw new RuntimeException("Database error while checking case assignment", e);
        }
    }

    // NEW METHOD: Check if case exists
    public boolean caseExists(String caseId) {
        String sql = "SELECT 1 FROM CaseFile WHERE caseID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, caseId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking case existence: " + e.getMessage());
            throw new RuntimeException("Database error while checking case existence", e);
        }
    }

    private Participant mapResultSetToParticipant(ResultSet rs) throws SQLException {
        // Extract all values from the ResultSet first
        String participantId = rs.getString("participantID");
        String name = rs.getString("name");
        String role = rs.getString("role");
        String contact = rs.getString("contact");
        String idType = rs.getString("idType");
        String idNumber = rs.getString("idNumber");

        // Convert role to proper case for display
        String displayRole = role.substring(0, 1).toUpperCase() + role.substring(1);

        // Create Participant object using the constructor with all parameters
        Participant participant = new Participant(participantId, name, displayRole, contact, idType, idNumber);

        return participant;
    }

    // Generate a unique participant ID
    public String generateParticipantId() {
        return "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Getter for officer ID
    public String getOfficerId() {
        return officerId;
    }

    // Setter for officer ID
    public void setOfficerId(String officerId) {
        this.officerId = officerId;
    }
}