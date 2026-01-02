package com.fcms.repositories;

import com.fcms.models.ForensicReport;
import com.fcms.database.SQLiteDatabase;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportRepository {

    public void save(ForensicReport report) throws SQLException {
        String sql = "INSERT INTO ForensicReport (reportID, title, filename, notes, completionDate, uploadDate, status, requestID, uploadedBy) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, report.getReportId());
            pstmt.setString(2, report.getTitle());
            pstmt.setString(3, report.getFileName());
            pstmt.setString(4, report.getNotes());
            pstmt.setString(5, report.getCompletionDate().toString());
            pstmt.setString(6, report.getUploadDate().toString());
            pstmt.setString(7, report.getStatus());
            pstmt.setString(8, report.getRequestId());
            pstmt.setString(9, report.getUploadedBy());

            pstmt.executeUpdate();
            System.out.println("Saved forensic report to database: " + report.getReportId());
        }
    }

    public void update(ForensicReport report) throws SQLException {
        String sql = "UPDATE ForensicReport SET title = ?, filename = ?, notes = ?, completionDate = ?, " +
                "uploadDate = ?, status = ?, requestID = ?, uploadedBy = ? WHERE reportID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, report.getTitle());
            pstmt.setString(2, report.getFileName());
            pstmt.setString(3, report.getNotes());
            pstmt.setString(4, report.getCompletionDate().toString());
            pstmt.setString(5, report.getUploadDate().toString());
            pstmt.setString(6, report.getStatus());
            pstmt.setString(7, report.getRequestId());
            pstmt.setString(8, report.getUploadedBy());
            pstmt.setString(9, report.getReportId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Forensic report not found: " + report.getReportId());
            }
            System.out.println("Updated forensic report in database: " + report.getReportId());
        }
    }

    public ForensicReport findById(String reportId) throws SQLException {
        String sql = "SELECT * FROM ForensicReport WHERE reportID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reportId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToReport(rs);
            }
            return null;
        }
    }

    public List<ForensicReport> findByRequestId(String requestId) throws SQLException {
        String sql = "SELECT * FROM ForensicReport WHERE requestID = ?";
        List<ForensicReport> reports = new ArrayList<>();

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, requestId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        }
    }

    public List<ForensicReport> findAll() throws SQLException {
        String sql = "SELECT * FROM ForensicReport";
        List<ForensicReport> reports = new ArrayList<>();

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;
        }
    }

    public boolean existsByRequestId(String requestId) throws SQLException {
        String sql = "SELECT 1 FROM ForensicReport WHERE requestID = ? LIMIT 1";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    private ForensicReport mapResultSetToReport(ResultSet rs) throws SQLException {
        ForensicReport report = new ForensicReport();
        report.setReportId(rs.getString("reportID"));
        report.setTitle(rs.getString("title"));
        report.setFileName(rs.getString("filename"));
        report.setNotes(rs.getString("notes"));

        String completionDate = rs.getString("completionDate");
        if (completionDate != null) {
            report.setCompletionDate(LocalDate.parse(completionDate));
        }

        String uploadDate = rs.getString("uploadDate");
        if (uploadDate != null) {
            report.setUploadDate(LocalDate.parse(uploadDate));
        }


        report.setStatus(rs.getString("status"));
        report.setRequestId(rs.getString("requestID"));
        report.setUploadedBy(rs.getString("uploadedBy"));

        return report;
    }

    // Generate a unique report ID
    public String generateReportId() {
        try {
            String sql = "SELECT reportID FROM ForensicReport ORDER BY reportID DESC LIMIT 1";

            try (Connection conn = SQLiteDatabase.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    // Get the last report ID and increment it
                    String lastReportId = rs.getString("reportID");
                    if (lastReportId != null && lastReportId.startsWith("RP")) {
                        // Extract the numeric part and increment
                        String numericPart = lastReportId.substring(2);
                        try {
                            int number = Integer.parseInt(numericPart);
                            return String.format("RP%05d", number + 1);
                        } catch (NumberFormatException e) {
                            // If parsing fails, start from 1
                            return "RP00001";
                        }
                    }
                }
                // If no reports exist yet, start from 1
                return "RP00001";
            }
        } catch (SQLException e) {
            System.err.println("Error generating report ID: " + e.getMessage());
            // Fallback: return a timestamp-based ID
            return "RP" + System.currentTimeMillis() % 100000;
        }
    }
}