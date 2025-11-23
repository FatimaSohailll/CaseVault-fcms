package com.fcms.repositories;

import com.fcms.models.ForensicExpert;
import com.fcms.database.SQLiteDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForensicExpertRepository {

    public List<ForensicExpert> findAll() {
        List<ForensicExpert> experts = new ArrayList<>();
        String sql = "SELECT ua.userID, ua.name, fe.labName, ua.email " +
                "FROM UserAccount ua " +
                "JOIN ForensicExpert fe ON ua.userID = fe.expertID " +
                "WHERE ua.role = 'Forensic Expert' and ua.approved = true";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ForensicExpert expert = new ForensicExpert(
                        rs.getString("userID"),      // expertID
                        rs.getString("name"),        // name from UserAccount
                        rs.getString("labName"),     // labName from ForensicExpert
                        rs.getString("email")        // email from UserAccount
                );
                experts.add(expert);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching forensic experts: " + e.getMessage());
            throw new RuntimeException("Database error while fetching forensic experts", e);
        }

        return experts;
    }

    public ForensicExpert findById(String expertId) {
        String sql = "SELECT ua.userID, ua.name, fe.labName, ua.email " +
                "FROM UserAccount ua " +
                "JOIN ForensicExpert fe ON ua.userID = fe.expertID " +
                "WHERE ua.role = 'Forensic Expert' AND ua.userID = ? AND ua.approved = true";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, expertId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ForensicExpert(
                        rs.getString("userID"),
                        rs.getString("name"),
                        rs.getString("labName"),
                        rs.getString("email")
                );
            }
            return null;

        } catch (SQLException e) {
            System.out.println("Error finding forensic expert by ID: " + e.getMessage());
            throw new RuntimeException("Database error while finding forensic expert", e);
        }
    }

    public boolean exists(String expertId) {
        String sql = "SELECT 1 FROM ForensicExpert WHERE expertID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, expertId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking forensic expert existence: " + e.getMessage());
            throw new RuntimeException("Database error while checking forensic expert existence", e);
        }
    }

    // Method to get experts by specialization (if you add this field to your database)
    public List<ForensicExpert> findBySpecialization(String specialization) {
        List<ForensicExpert> experts = new ArrayList<>();
        String sql = "SELECT ua.userID, ua.name, fe.labName, ua.email " +
                "FROM UserAccount ua " +
                "JOIN ForensicExpert fe ON ua.userID = fe.expertID " +
                "WHERE ua.role = 'Forensic Expert' AND fe.specialization = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, specialization);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ForensicExpert expert = new ForensicExpert(
                        rs.getString("userID"),
                        rs.getString("name"),
                        rs.getString("labName"),
                        rs.getString("email")
                );
                experts.add(expert);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching forensic experts by specialization: " + e.getMessage());
            throw new RuntimeException("Database error while fetching forensic experts by specialization", e);
        }

        return experts;
    }

    // Method to count total experts
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM ForensicExpert";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error counting forensic experts: " + e.getMessage());
            throw new RuntimeException("Database error while counting forensic experts", e);
        }
    }
}
