package com.fcms.repositories;

import com.fcms.models.ForensicRequest;
import com.fcms.database.SQLiteDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ForensicRequestRepository {
    private String expertId;

    public ForensicRequestRepository(String expertId) {
        this.expertId = expertId;
    }

    public void save(ForensicRequest request) throws SQLException {
        String sql = "INSERT INTO ForensicRequest (requestID, expertID, status, requestedBy, evidenceType, requestedDate, evidenceID, analysisType, priority) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, request.getRequestId());
            pstmt.setString(2, request.getExpertId());
            pstmt.setString(3, request.getStatus());
            pstmt.setString(4, request.getRequestedBy());
            pstmt.setString(5, request.getEvidenceType());
            pstmt.setDate(6, Date.valueOf(request.getRequestedDate()));
            pstmt.setString(7, request.getEvidenceId());
            pstmt.setString(8, request.getAnalysisType());
            pstmt.setString(9, request.getPriority());

            pstmt.executeUpdate();
            System.out.println("Saved forensic request to database: " + request.getRequestId());
        }
    }

    public void update(ForensicRequest request) throws SQLException {
        String sql = "UPDATE ForensicRequest SET expertID = ?, status = ?, requestedBy = ?, evidenceType = ?, " +
                "requestedDate = ?, evidenceID = ?, analysisType = ?, priority = ? WHERE requestID = ? AND expertID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, request.getExpertId());
            pstmt.setString(2, request.getStatus());
            pstmt.setString(3, request.getRequestedBy());
            pstmt.setString(4, request.getEvidenceType());
            pstmt.setDate(5, Date.valueOf(request.getRequestedDate()));
            pstmt.setString(6, request.getEvidenceId());
            pstmt.setString(7, request.getAnalysisType());
            pstmt.setString(8, request.getPriority());
            pstmt.setString(9, request.getRequestId());
            pstmt.setString(10, expertId); // Filter by expert ID

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Forensic request not found: " + request.getRequestId());
            }
            System.out.println("Updated forensic request in database: " + request.getRequestId());
        }
    }

    public List<ForensicRequest> findAll() throws SQLException {
        String sql = "SELECT * FROM ForensicRequest WHERE expertID = ?";
        List<ForensicRequest> requests = new ArrayList<>();

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, expertId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
        }
    }

    public ForensicRequest findById(String requestId) throws SQLException {
        String sql = "SELECT * FROM ForensicRequest WHERE requestID = ? AND expertID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, requestId);
            pstmt.setString(2, expertId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRequest(rs);
            }
            return null;
        }
    }

    public List<ForensicRequest> findByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM ForensicRequest WHERE status = ? AND expertID = ?";
        List<ForensicRequest> requests = new ArrayList<>();

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, expertId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
        }
    }

    // Analytics methods for dashboard - get counts by status for this expert
    public int getPendingCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM ForensicRequest WHERE status = 'pending' AND expertID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, expertId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    public int getCompletedCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM ForensicRequest WHERE status = 'completed' AND expertID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, expertId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM ForensicRequest WHERE expertID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, expertId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    // Method to get requests by expert ID (for service layer flexibility)
    public List<ForensicRequest> findByExpertId(String specificExpertId) throws SQLException {
        String sql = "SELECT * FROM ForensicRequest WHERE expertID = ?";
        List<ForensicRequest> requests = new ArrayList<>();

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, specificExpertId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
        }
    }

    private ForensicRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        ForensicRequest request = new ForensicRequest();
        request.setRequestId(rs.getString("requestID"));
        request.setExpertId(rs.getString("expertID"));
        request.setStatus(rs.getString("status"));
        request.setRequestedBy(rs.getString("requestedBy"));
        request.setEvidenceType(rs.getString("evidenceType"));

        // Handle date parsing safely
        try {
            Date requestedDate = rs.getDate("requestedDate");
            if (requestedDate != null) {
                request.setRequestedDate(requestedDate.toLocalDate());
            } else {
                // If date is null, set to current date
                request.setRequestedDate(LocalDate.now());
            }
        } catch (Exception e) {
            // If there's any date parsing error, use current date
            System.err.println("Warning: Error parsing date from database, using current date: " + e.getMessage());
            request.setRequestedDate(LocalDate.now());
        }

        request.setEvidenceId(rs.getString("evidenceID"));
        request.setAnalysisType(rs.getString("analysisType"));
        request.setPriority(rs.getString("priority"));

        return request;
    }

    // Generate a unique request ID
    public String generateRequestId() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = SQLiteDatabase.getConnection();

            // Get the maximum request ID from the database
            String sql = "SELECT MAX(requestID) as maxId FROM ForensicRequest WHERE requestID LIKE 'FR%'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String maxId = rs.getString("maxId");
                if (maxId != null && maxId.startsWith("FR")) {
                    // Extract numeric part and increment
                    String numericPart = maxId.substring(2);
                    try {
                        int nextNumber = Integer.parseInt(numericPart) + 1;
                        return String.format("FR%05d", nextNumber);
                    } catch (NumberFormatException e) {
                        // If parsing fails, start from 1
                        return "FR00001";
                    }
                }
            }

            // No existing requests, start from 1
            return "FR00001";

        } catch (SQLException e) {
            System.err.println("Error generating request ID: " + e.getMessage());
            e.printStackTrace();
            // Fallback ID generation
            return generateFallbackRequestId();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateFallbackRequestId() {
        // Fallback method using timestamp
        long timestamp = System.currentTimeMillis() % 100000;
        return "FR" + String.format("%05d", timestamp);
    }
    // Getter for expert ID
    public String getExpertId() {
        return expertId;
    }

    // Setter for expert ID
    public void setExpertId(String expertId) {
        this.expertId = expertId;
    }

}