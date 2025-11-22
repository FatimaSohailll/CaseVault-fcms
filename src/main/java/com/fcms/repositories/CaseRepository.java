package com.fcms.repositories;

import com.fcms.database.SQLiteDatabase;
import com.fcms.models.Case;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaseRepository {

    public List<Case> findAll() {
        List<Case> cases = new ArrayList<>();
        String query = "SELECT caseID, title, type, assignedOfficer, location, dateRegistered, status, priority FROM CaseFile ORDER BY dateRegistered";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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
                cases.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cases;
    }

    public Case findById(String id) {
        String query = "SELECT caseID, title, type, assignedOfficer, location, dateRegistered, status, priority FROM CaseFile WHERE caseID = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Case c) {
        String query = "INSERT INTO CaseFile (caseID, title, type, assignedOfficer, location, dateRegistered, status, priority) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, c.getId());
            stmt.setString(2, c.getTitle());
            stmt.setString(3, c.getType());
            stmt.setString(4, c.getAssignedOfficer());
            stmt.setString(5, c.getLocation());
            stmt.setDate(6, Date.valueOf(c.getDateRegistered()));
            stmt.setString(7, c.getStatus());
            stmt.setString(8, c.getPriority().toLowerCase());

            stmt.executeUpdate();
            System.out.println("Saved case: " + c.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Case c) {
        String query = "UPDATE CaseFile SET title = ?, type = ?, assignedOfficer = ?, location = ?, dateRegistered = ?, status = ?, priority = ? WHERE caseID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getType());
            stmt.setString(3, c.getAssignedOfficer());
            stmt.setString(4, c.getLocation());
            stmt.setDate(5, Date.valueOf(c.getDateRegistered()));
            stmt.setString(6, c.getStatus());
            stmt.setString(7, c.getPriority());
            stmt.setString(8, c.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Updated case: " + c.getId());
            } else {
                throw new RuntimeException("Case not found: " + c.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeCase(String caseId, String reason, String report) {
        String sql = "UPDATE CaseFile SET status = ?, close_reason = ?, final_report = ? WHERE caseID = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "closed");
            stmt.setString(2, reason);
            stmt.setString(3, report);
            stmt.setString(4, caseId);

            stmt.executeUpdate();
            System.out.println("Closed case: " + caseId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(String id) {
        String query = "SELECT 1 FROM CaseFile WHERE caseID = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void delete(String id) {
        String query = "DELETE FROM CaseFile WHERE caseID = ?";
        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
            System.out.println("Deleted case: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
