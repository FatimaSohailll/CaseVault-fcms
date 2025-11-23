package com.fcms.repositories;

import com.fcms.models.ChainOfCustody;
import com.fcms.database.SQLiteDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChainofCustodyRepository {

    public ChainofCustodyRepository() {
    }

    public void save(ChainOfCustody record) {
        String sql = "INSERT INTO ChainOfCustody (recordID, evidenceID, timestamp, action, doneBy) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, record.getRecordId());
            pstmt.setString(2, record.getEvidenceId());
            pstmt.setString(3, record.getTimestamp().toString());
            pstmt.setString(4, record.getAction());
            pstmt.setString(5, record.getDoneBy());

            pstmt.executeUpdate();
            System.out.println("Saved chain of custody record: " + record.getRecordId());

        } catch (SQLException e) {
            System.out.println("Error saving chain of custody record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<ChainOfCustody> findByEvidenceId(String evidenceId) {
        List<ChainOfCustody> records = new ArrayList<>();
        String sql = "SELECT * FROM ChainOfCustody WHERE evidenceID = ? ORDER BY timestamp DESC";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, evidenceId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChainOfCustody record = mapResultSetToChainOfCustody(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching chain of custody records by evidence ID: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    public List<ChainOfCustody> findAll() {
        List<ChainOfCustody> records = new ArrayList<>();
        String sql = "SELECT * FROM ChainOfCustody ORDER BY timestamp DESC";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ChainOfCustody record = mapResultSetToChainOfCustody(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching all chain of custody records: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    public ChainOfCustody findById(String recordId) {
        String sql = "SELECT * FROM ChainOfCustody WHERE recordID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, recordId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToChainOfCustody(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error finding chain of custody record by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<ChainOfCustody> findByAction(String action) {
        List<ChainOfCustody> records = new ArrayList<>();
        String sql = "SELECT * FROM ChainOfCustody WHERE action = ? ORDER BY timestamp DESC";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, action);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChainOfCustody record = mapResultSetToChainOfCustody(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching chain of custody records by action: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    public List<ChainOfCustody> findByDoneBy(String doneBy) {
        List<ChainOfCustody> records = new ArrayList<>();
        String sql = "SELECT * FROM ChainOfCustody WHERE doneBy = ? ORDER BY timestamp DESC";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doneBy);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChainOfCustody record = mapResultSetToChainOfCustody(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching chain of custody records by person: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    public List<ChainOfCustody> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<ChainOfCustody> records = new ArrayList<>();
        String sql = "SELECT * FROM ChainOfCustody WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate.toString());
            pstmt.setString(2, endDate.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChainOfCustody record = mapResultSetToChainOfCustody(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching chain of custody records by date range: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    public void update(ChainOfCustody record) {
        String sql = "UPDATE ChainOfCustody SET evidenceID = ?, timestamp = ?, action = ?, doneBy = ? WHERE recordID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, record.getEvidenceId());
            pstmt.setString(2, record.getTimestamp().toString());
            pstmt.setString(3, record.getAction());
            pstmt.setString(4, record.getDoneBy());
            pstmt.setString(5, record.getRecordId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Updated chain of custody record: " + record.getRecordId());
            } else {
                System.out.println("No chain of custody record found with ID: " + record.getRecordId());
            }

        } catch (SQLException e) {
            System.out.println("Error updating chain of custody record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(String recordId) {
        String sql = "DELETE FROM ChainOfCustody WHERE recordID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, recordId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Deleted chain of custody record: " + recordId);
            } else {
                System.out.println("No chain of custody record found with ID: " + recordId);
            }

        } catch (SQLException e) {
            System.out.println("Error deleting chain of custody record: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getCountByEvidence(String evidenceId) {
        String sql = "SELECT COUNT(*) as count FROM ChainOfCustody WHERE evidenceID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, evidenceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.out.println("Error counting chain of custody records by evidence: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public boolean exists(String recordId) {
        String sql = "SELECT 1 FROM ChainOfCustody WHERE recordID = ? LIMIT 1";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, recordId);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking if chain of custody record exists: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public String getNextRecordId() {
        String sql = "SELECT recordID FROM ChainOfCustody ORDER BY recordID DESC LIMIT 1";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("recordID");
                if (lastId != null && lastId.startsWith("COC")) {
                    try {
                        int lastNumber = Integer.parseInt(lastId.substring(3));
                        return "COC" + String.format("%04d", lastNumber + 1);
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing chain of custody record ID: " + lastId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting next chain of custody record ID: " + e.getMessage());
            e.printStackTrace();
        }

        // Default starting ID if no records exist
        return "COC0001";
    }

    public List<String> getAllActions() {
        List<String> actions = new ArrayList<>();
        String sql = "SELECT DISTINCT action FROM ChainOfCustody ORDER BY action";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                actions.add(rs.getString("action"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching chain of custody actions: " + e.getMessage());
            e.printStackTrace();
        }

        return actions;
    }

    public List<String> getAllPersonnel() {
        List<String> personnel = new ArrayList<>();
        String sql = "SELECT DISTINCT doneBy FROM ChainOfCustody ORDER BY doneBy";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                personnel.add(rs.getString("doneBy"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching chain of custody personnel: " + e.getMessage());
            e.printStackTrace();
        }

        return personnel;
    }

    private ChainOfCustody mapResultSetToChainOfCustody(ResultSet rs) throws SQLException {
        ChainOfCustody record = new ChainOfCustody();
        record.setRecordId(rs.getString("recordID"));
        record.setEvidenceId(rs.getString("evidenceID"));

        // Handle timestamp
        String timestampStr = rs.getString("timestamp");
        if (timestampStr != null) {
            try {
                record.setTimestamp(LocalDateTime.parse(timestampStr.replace(' ', 'T')));
            } catch (Exception e) {
                System.out.println("Error parsing timestamp: " + timestampStr);
                record.setTimestamp(LocalDateTime.now());
            }
        } else {
            record.setTimestamp(LocalDateTime.now());
        }

        record.setAction(rs.getString("action"));
        record.setDoneBy(rs.getString("doneBy"));

        return record;
    }
}