package com.fcms.repositories;

import com.fcms.models.users.UserAccount;
import com.fcms.models.users.PoliceOfficer;
import com.fcms.models.users.CourtOfficial;
import com.fcms.models.users.ForensicExpert;
import com.fcms.database.SQLiteDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    // =====================================================
    // FETCH ALL USERS
    // =====================================================
    public static List<UserAccount> getAllUsers() {
        List<UserAccount> list = new ArrayList<>();

        String sql = "SELECT * FROM UserAccount";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String id   = rs.getString("userID");
                String role = rs.getString("role");

                UserAccount u;

                switch (role) {

                    case "Police" -> {
                        PreparedStatement ps = conn.prepareStatement(
                                "SELECT rank, department FROM PoliceOfficer WHERE officerID=?"
                        );
                        ps.setString(1, id);
                        ResultSet r2 = ps.executeQuery();

                        u = new PoliceOfficer(
                                id,
                                rs.getString("username"),
                                rs.getString("name"),
                                rs.getString("email"),
                                null,
                                role,
                                rs.getString("managedBY"),
                                rs.getString("createdAt"),
                                r2.getString("rank"),
                                r2.getString("department")
                        );
                    }

                    case "Court Official" -> {
                        PreparedStatement ps = conn.prepareStatement(
                                "SELECT courtName, designation FROM CourtOfficial WHERE officialID=?"
                        );
                        ps.setString(1, id);
                        ResultSet r2 = ps.executeQuery();

                        u = new CourtOfficial(
                                id,
                                rs.getString("username"),
                                rs.getString("name"),
                                rs.getString("email"),
                                null,
                                role,
                                rs.getString("managedBY"),
                                rs.getString("createdAt"),
                                r2.getString("courtName"),
                                r2.getString("designation")
                        );
                    }

                    default -> { // Forensic Expert
                        PreparedStatement ps = conn.prepareStatement(
                                "SELECT labName FROM ForensicExpert WHERE expertID=?"
                        );
                        ps.setString(1, id);
                        ResultSet r2 = ps.executeQuery();

                        u = new ForensicExpert(
                                id,
                                rs.getString("username"),
                                rs.getString("name"),
                                rs.getString("email"),
                                null,
                                role,
                                rs.getString("managedBY"),
                                rs.getString("createdAt"),
                                r2.getString("labName")
                        );
                    }
                }

                list.add(u);
            }

        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

        return list;
    }

    public static List<UserAccount> getPendingUsers() {
        List<UserAccount> list = new ArrayList<>();

        String sql = "SELECT * FROM UserAccount WHERE managedBY='Pending'";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String id   = rs.getString("userID");
                String role = rs.getString("role");

                UserAccount u;

                switch (role) {

                    case "Police" -> {
                        PreparedStatement ps = conn.prepareStatement(
                                "SELECT rank, department FROM PoliceOfficer WHERE officerID=?"
                        );
                        ps.setString(1, id);
                        ResultSet r2 = ps.executeQuery();

                        u = new PoliceOfficer(
                                id,
                                rs.getString("username"),
                                rs.getString("name"),
                                rs.getString("email"),
                                null,
                                role,
                                rs.getString("managedBY"),
                                rs.getString("createdAt"),
                                r2.getString("rank"),
                                r2.getString("department")
                        );
                    }

                    case "Court Official" -> {
                        PreparedStatement ps = conn.prepareStatement(
                                "SELECT courtName, designation FROM CourtOfficial WHERE officialID=?"
                        );
                        ps.setString(1, id);
                        ResultSet r2 = ps.executeQuery();

                        u = new CourtOfficial(
                                id,
                                rs.getString("username"),
                                rs.getString("name"),
                                rs.getString("email"),
                                null,
                                role,
                                rs.getString("managedBY"),
                                rs.getString("createdAt"),
                                r2.getString("courtName"),
                                r2.getString("designation")
                        );
                    }

                    default -> {
                        PreparedStatement ps = conn.prepareStatement(
                                "SELECT labName FROM ForensicExpert WHERE expertID=?"
                        );
                        ps.setString(1, id);
                        ResultSet r2 = ps.executeQuery();

                        u = new ForensicExpert(
                                id,
                                rs.getString("username"),
                                rs.getString("name"),
                                rs.getString("email"),
                                null,
                                role,
                                rs.getString("managedBY"),
                                rs.getString("createdAt"),
                                r2.getString("labName")
                        );
                    }
                }

                list.add(u);
            }

        } catch (Exception e) {
            System.out.println("Error loading pending users: " + e.getMessage());
        }

        return list;
    }

    // =====================================================
    // INSERT USER
    // =====================================================
    public static void insertUser(UserAccount u) {
        String sql = "INSERT INTO UserAccount " +
                "(userID, username, email, name, password, role, managedBY) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Insert into main table
            stmt.setString(1, u.getUserID());
            stmt.setString(2, u.getUsername());
            stmt.setString(3, u.getEmail());
            stmt.setString(4, u.getName());
            stmt.setString(5, u.getPassword());
            stmt.setString(6, u.getRole());
            stmt.setString(7, u.getManagedBy());

            stmt.executeUpdate();

            // NOW insert into subclass table
            if (u instanceof PoliceOfficer p) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO PoliceOfficer (officerID, rank, department) VALUES (?, ?, ?)"
                );
                ps.setString(1, p.getUserID());
                ps.setString(2, p.getRank());
                ps.setString(3, p.getDepartment());
                ps.executeUpdate();
            }
            else if (u instanceof CourtOfficial c) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO CourtOfficial (officialID, courtName, designation) VALUES (?, ?, ?)"
                );
                ps.setString(1, c.getUserID());
                ps.setString(2, c.getCourtName());
                ps.setString(3, c.getDesignation());
                ps.executeUpdate();
            }
            else if (u instanceof ForensicExpert f) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ForensicExpert (expertID, labName) VALUES (?, ?)"
                );
                ps.setString(1, f.getUserID());
                ps.setString(2, f.getLabName());
                ps.executeUpdate();
            }

            insertHistory("System Admin", "Added new user: " + u.getName());

        } catch (Exception e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
    }

    // =====================================================
    // UPDATE USER
    // =====================================================
    public static void updateUser(UserAccount u) {

        String sql = "UPDATE UserAccount SET username=?, email=?, name=?, password=?, role=?, managedBY=? WHERE userID=?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Update base table
            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getName());
            stmt.setString(4, u.getPassword());
            stmt.setString(5, u.getRole());
            stmt.setString(6, u.getManagedBy());
            stmt.setString(7, u.getUserID());

            stmt.executeUpdate();

            // Update subclass table
            if (u instanceof PoliceOfficer p) {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE PoliceOfficer SET rank=?, department=? WHERE officerID=?"
                );
                ps.setString(1, p.getRank());
                ps.setString(2, p.getDepartment());
                ps.setString(3, p.getUserID());
                ps.executeUpdate();
            }
            else if (u instanceof CourtOfficial c) {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE CourtOfficial SET courtName=?, designation=? WHERE officialID=?"
                );
                ps.setString(1, c.getCourtName());
                ps.setString(2, c.getDesignation());
                ps.setString(3, c.getUserID());
                ps.executeUpdate();
            }
            else if (u instanceof ForensicExpert f) {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE ForensicExpert SET labName=? WHERE expertID=?"
                );
                ps.setString(1, f.getLabName());
                ps.setString(2, f.getUserID());
                ps.executeUpdate();
            }

            insertHistory("System Admin", "Updated user: " + u.getName());

        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    // =====================================================
    // DELETE USER
    // =====================================================
    public static void deleteUser(String userID) {
        String sql = "DELETE FROM UserAccount WHERE userID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userID);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    // =====================================================
    // HISTORY
    // =====================================================
    public static void addHistory(String actor, String action) {
        String sql = "INSERT INTO UserHistory (actor, action) VALUES (?, ?)";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, actor);
            ps.setString(2, action);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getRecentHistory(int limit) {
        List<String[]> history = new ArrayList<>();
        String sql = "SELECT actor, action, timestamp FROM UserHistory ORDER BY historyID DESC LIMIT ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                history.add(new String[]{
                        rs.getString("actor"),
                        rs.getString("action"),
                        rs.getString("timestamp")
                });
            }

        } catch (Exception e) {
            System.out.println("Error loading history: " + e.getMessage());
        }

        return history;
    }

    public static int getUserCount() {
        String sql = "SELECT COUNT(*) AS cnt FROM UserAccount WHERE managedBy != 'Pending'";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            System.out.println("Error counting users: " + e.getMessage());
        }
        return 0;
    }

    public static void insertHistory(String actor, String action) {
        String sql = "INSERT INTO UserHistory (actor, action) VALUES (?, ?)";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, actor);
            stmt.setString(2, action);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error inserting history: " + e.getMessage());
        }
    }

    public static void updateStatus(String userID, String status) {
        String sql = "UPDATE UserAccount SET managedBY=? WHERE userID=?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, userID);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error updating user status: " + e.getMessage());
        }
    }

}
