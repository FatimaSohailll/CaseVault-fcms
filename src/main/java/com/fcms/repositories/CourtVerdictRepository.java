package com.fcms.repositories;

import com.fcms.database.SQLiteDatabase;
import com.fcms.models.CourtVerdict;

import java.sql.*;
import java.time.LocalDate;

public class CourtVerdictRepository {

    // ----------------------------------------------------------
    // SAVE VERDICT
    // ----------------------------------------------------------
    public static boolean saveVerdict(CourtVerdict v) {
        String sql = """
                INSERT INTO CourtVerdict
                (verdictID, outcome, sentence, dateIssued, notes, caseID, issuedBy)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, v.getVerdictID());
            ps.setString(2, v.getOutcome());
            ps.setString(3, v.getSentence());
            ps.setString(4, v.getDateIssued().toString());
            ps.setString(5, v.getNotes());
            ps.setString(6, v.getCaseID());
            ps.setString(7, v.getIssuedBy());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving verdict: " + e.getMessage());
            return false;
        }
    }

    // ----------------------------------------------------------
    // CHECK IF CASE ALREADY HAS A VERDICT (caseID is UNIQUE)
    // ----------------------------------------------------------
    public static boolean hasVerdict(String caseID) {
        String sql = "SELECT verdictID FROM CourtVerdict WHERE caseID = ?";

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, caseID);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            return false;
        }
    }

    // ----------------------------------------------------------
    // GET VERDICT BY CASE
    // ----------------------------------------------------------
    public static CourtVerdict getVerdict(String caseID) {
        String sql = """
                SELECT verdictID, outcome, sentence, dateIssued, notes, caseID, issuedBy
                FROM CourtVerdict WHERE caseID = ?
                """;

        try (Connection conn = SQLiteDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, caseID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new CourtVerdict(
                        rs.getString("verdictID"),
                        rs.getString("outcome"),
                        rs.getString("sentence"),
                        LocalDate.parse(rs.getString("dateIssued")),
                        rs.getString("notes"),
                        rs.getString("caseID"),
                        rs.getString("issuedBy")
                );
            }
        } catch (SQLException ignored) {}
        return null;
    }
}
