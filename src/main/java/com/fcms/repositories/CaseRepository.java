package com.fcms.repositories;

import com.fcms.database.SQLiteDatabase;
import com.fcms.models.Case;

import java.sql.*;
import java.time.LocalDate;
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

                // FIXED
                String dr = rs.getString("dateRegistered");
                c.setDateRegistered(dr != null ? LocalDate.parse(dr) : null);

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
                    String dr = rs.getString("dateRegistered");
                    c.setDateRegistered(dr != null ? LocalDate.parse(dr) : null);
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

    public List<Case> getFilteredCases(LocalDate from, LocalDate to, String location, String type) {
        List<Case> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT caseID, title, type, assignedOfficer, location, dateRegistered, status, priority " +
                        "FROM CaseFile WHERE 1=1 "
        );

        if (from != null)
            sql.append("AND dateRegistered >= ? ");

        if (to != null)
            sql.append("AND dateRegistered <= ? ");

        if (location != null && !location.isEmpty() && !location.equalsIgnoreCase("All"))
            sql.append("AND location = ? ");

        if (type != null && !type.isEmpty() && !type.equalsIgnoreCase("All"))
            sql.append("AND type = ? ");

        sql.append("ORDER BY dateRegistered");

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (from != null)
                stmt.setDate(index++, Date.valueOf(from));

            if (to != null)
                stmt.setDate(index++, Date.valueOf(to));

            if (location != null && !location.isEmpty() && !location.equalsIgnoreCase("All"))
                stmt.setString(index++, location);

            if (type != null && !type.isEmpty() && !type.equalsIgnoreCase("All"))
                stmt.setString(index++, type);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Case c = new Case();
                    c.setId(rs.getString("caseID"));
                    c.setTitle(rs.getString("title"));
                    c.setType(rs.getString("type"));
                    c.setAssignedOfficer(rs.getString("assignedOfficer"));
                    c.setLocation(rs.getString("location"));

                    String dr = rs.getString("dateRegistered");
                    c.setDateRegistered(dr != null ? LocalDate.parse(dr) : null);

                    c.setStatus(rs.getString("status"));
                    c.setPriority(rs.getString("priority"));

                    list.add(c);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getAllLocations() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT DISTINCT location FROM CaseFile WHERE location IS NOT NULL ORDER BY location";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getString("location"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> getAllCaseTypes() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT DISTINCT type FROM CaseFile WHERE type IS NOT NULL ORDER BY type";

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getString("type"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countEvidence(String caseId) {
        String sql = "SELECT COUNT(*) AS cnt FROM Evidence WHERE caseID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, caseId);
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getInt("cnt") : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int countForensicReports(String caseId) {

        String sql = """
        SELECT COUNT(*) AS cnt
        FROM ForensicReport fr
        JOIN ForensicRequest fq ON fr.requestID = fq.requestID
        JOIN Evidence ev ON fq.evidenceID = ev.evidenceID
        WHERE ev.caseID = ?
    """;

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, caseId);
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getInt("cnt") : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void submitCaseToCourt(String caseID, String courtOfficialID) {
        String sql = """
        UPDATE CaseFile
        SET status = 'submitted',
            reviewedBy = ?
        WHERE caseID = ?
    """;

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courtOfficialID);
            stmt.setString(2, caseID);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Case> getCasesByOfficer(String officerId) {
        List<Case> cases = new ArrayList<>();
        String query = "SELECT caseID, title, type, assignedOfficer, location, dateRegistered, status, priority FROM CaseFile WHERE assignedOfficer = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, officerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Case c = new Case();
                    c.setId(rs.getString("caseID"));
                    c.setTitle(rs.getString("title"));
                    c.setType(rs.getString("type"));
                    c.setAssignedOfficer(rs.getString("assignedOfficer"));
                    c.setLocation(rs.getString("location"));

                    String dr = rs.getString("dateRegistered");
                    c.setDateRegistered(dr != null ? LocalDate.parse(dr) : null);

                    c.setStatus(rs.getString("status"));
                    c.setPriority(rs.getString("priority"));
                    cases.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching cases for officer: " + officerId, e);
        }
        return cases;
    }

    public List<Case> findSubmittedForOfficial(String officialId) {
        List<Case> cases = new ArrayList<>();

        String sql = """
        SELECT caseID, title, type, assignedOfficer, location, dateRegistered, status, priority
        FROM CaseFile
        WHERE status = 'submitted'
          AND reviewedBy = ?
        ORDER BY dateRegistered DESC
    """;

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, officialId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Case c = new Case();
                c.setId(rs.getString("caseID"));
                c.setTitle(rs.getString("title"));
                c.setType(rs.getString("type"));
                c.setAssignedOfficer(rs.getString("assignedOfficer"));
                c.setLocation(rs.getString("location"));

                String dr = rs.getString("dateRegistered");
                c.setDateRegistered(dr != null ? LocalDate.parse(dr) : null);

                c.setStatus(rs.getString("status"));
                c.setPriority(rs.getString("priority"));

                cases.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cases;
    }

}