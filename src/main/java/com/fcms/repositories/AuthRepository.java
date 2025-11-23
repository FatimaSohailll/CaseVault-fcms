package com.fcms.repositories;

import com.fcms.database.SQLiteDatabase;
import com.fcms.models.CreateUserAccount;
import java.sql.*;

public class AuthRepository {

    public boolean saveUser(String userID, String username, String email, String fullName,
                            String password, String role, String managedBy, boolean approved) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = SQLiteDatabase.getConnection();
            String sql = "INSERT INTO UserAccount (userID, username, email, name, password, role, managedBY, approved) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userID);
            stmt.setString(2, username);
            stmt.setString(3, email);
            stmt.setString(4, fullName);
            stmt.setString(5, password);
            stmt.setString(6, role);
            stmt.setString(7, managedBy);
            stmt.setBoolean(8, approved);

            return stmt.executeUpdate() > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public boolean savePoliceOfficer(String officerID, String rank, String department) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = SQLiteDatabase.getConnection();
            String sql = "INSERT INTO PoliceOfficer (officerID, rank, department) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, officerID);
            stmt.setString(2, rank);
            stmt.setString(3, department);

            return stmt.executeUpdate() > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public boolean saveForensicExpert(String expertID, String labName) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = SQLiteDatabase.getConnection();
            String sql = "INSERT INTO ForensicExpert (expertID, labName) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, expertID);
            stmt.setString(2, labName);

            return stmt.executeUpdate() > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public boolean saveCourtOfficial(String officialID, String courtName, String designation) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = SQLiteDatabase.getConnection();
            String sql = "INSERT INTO CourtOfficial (officialID, courtName, designation) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, officialID);
            stmt.setString(2, courtName);
            stmt.setString(3, designation);

            return stmt.executeUpdate() > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public String getNextUserID(String role) throws SQLException {
        String prefix = getRolePrefix(role);

        // Get the maximum existing ID for this role
        String sql = "SELECT userID FROM UserAccount WHERE userID LIKE ? ORDER BY userID DESC LIMIT 1";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String lastID = rs.getString("userID");
                try {
                    int lastNumber = Integer.parseInt(lastID.substring(2));
                    int nextNumber = lastNumber + 1;
                    return String.format("%s%05d", prefix, nextNumber);
                } catch (NumberFormatException e) {
                    return prefix + "00001";
                }
            } else {
                return prefix + "00001";
            }
        }
    }

    public boolean isUsernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM UserAccount WHERE username = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM UserAccount WHERE email = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private String getRolePrefix(String role) {
        switch (role) {
            case "Police Officer": return "PO";
            case "Forensic Expert": return "EX";
            case "Court Official": return "CR";
            default: return "US";
        }
    }
    public CreateUserAccount getUserCredentials(String username) throws SQLException {
        String sql = "SELECT userID, username, password, role, approved FROM UserAccount WHERE username = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new CreateUserAccount(
                        rs.getString("userID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("approved")
                );
            }
            return null;
        }
    }
    public boolean updatePassword(String userID, String newPassword) throws SQLException {
        String sql = "UPDATE UserAccount SET password = ? WHERE userID = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, userID);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean userExists(String userID) throws SQLException {
        String sql = "SELECT 1 FROM UserAccount WHERE userID = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public String getUserEmail(String userID) throws SQLException {
        String sql = "SELECT email FROM UserAccount WHERE userID = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("email") : null;
        }
    }
}