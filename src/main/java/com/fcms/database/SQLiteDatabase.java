package com.fcms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDatabase {
    private static final String DB_URL = "jdbc:sqlite:casevault.db";
    private static boolean initialized = false;

    public static void initializeDatabase() {
        if (initialized) {
            System.out.println("Database already initialized");
            return; // Already initialized
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // This will only create tables if they don't exist
            stmt.execute("PRAGMA foreign_keys = ON");
            createTables(stmt);

            System.out.println("Database ready!");
            initialized = true;

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void createTables(Statement stmt) throws SQLException {
        // SYSTEM ADMIN
        stmt.execute("CREATE TABLE IF NOT EXISTS SystemAdmin (" +
                "adminID TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "password TEXT NOT NULL)");

        // USER ACCOUNT
        stmt.execute("CREATE TABLE IF NOT EXISTS UserAccount (" +
                "userID TEXT PRIMARY KEY, " +
                "username TEXT NOT NULL UNIQUE, " +
                "email TEXT NOT NULL UNIQUE, " +
                "name TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK (role IN ('Police','Court Official','Forensic Expert')), " +
                "managedBY TEXT NOT NULL, " +
                "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS UserHistory (" +
                "historyID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "actor TEXT NOT NULL, " +            // who did it (Admin)
                "action TEXT NOT NULL, " +           // what happened ("Added new user")
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // POLICE OFFICER
        stmt.execute("CREATE TABLE IF NOT EXISTS PoliceOfficer (" +
                "officerID TEXT PRIMARY KEY, " +
                "rank TEXT, " +
                "department TEXT, " +
                "FOREIGN KEY (officerID) REFERENCES UserAccount(userID) ON DELETE CASCADE)");

        // COURT OFFICIAL
        stmt.execute("CREATE TABLE IF NOT EXISTS CourtOfficial (" +
                "officialID TEXT PRIMARY KEY, " +
                "courtName TEXT NOT NULL, " +
                "designation TEXT, " +
                "FOREIGN KEY (officialID) REFERENCES UserAccount(userID) ON DELETE CASCADE)");

        // FORENSIC EXPERT
        stmt.execute("CREATE TABLE IF NOT EXISTS ForensicExpert (" +
                "expertID TEXT PRIMARY KEY, " +
                "labName TEXT, " +
                "FOREIGN KEY (expertID) REFERENCES UserAccount(userID) ON DELETE CASCADE)");

        // CASE FILE
        stmt.execute("CREATE TABLE IF NOT EXISTS CaseFile (" +
                "caseID TEXT PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "location TEXT, " +
                "type TEXT, " +
                "status TEXT DEFAULT 'open' CHECK (status IN ('open', 'closed', 'archived')), " +
                "priority TEXT CHECK (priority IN ('high', 'low', 'medium')), " +
                "assignedOfficer TEXT, " +
                "dateRegistered DATE NOT NULL, " +
                "reviewedBy TEXT, " +
                "FOREIGN KEY (assignedOfficer) REFERENCES PoliceOfficer(officerID), " +
                "FOREIGN KEY (reviewedBy) REFERENCES CourtOfficial(officialID))");

        // PARTICIPANT
        stmt.execute("CREATE TABLE IF NOT EXISTS Participant (" +
                "participantID TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK (role IN ('victim', 'suspect')), " +
                "contact TEXT)");

        // CASE PARTICIPANTS (Junction table)
        stmt.execute("CREATE TABLE IF NOT EXISTS CaseParticipants (" +
                "caseID TEXT NOT NULL, " +
                "participantID TEXT NOT NULL, " +
                "PRIMARY KEY (caseID, participantID), " +
                "FOREIGN KEY (caseID) REFERENCES CaseFile(caseID) ON DELETE CASCADE, " +
                "FOREIGN KEY (participantID) REFERENCES Participant(participantID) ON DELETE CASCADE)");

        // EVIDENCE
        stmt.execute("CREATE TABLE IF NOT EXISTS Evidence (" +
                "evidenceID TEXT PRIMARY KEY, " +
                "type TEXT NOT NULL, " +
                "description TEXT, " +
                "filename TEXT, " +
                "location TEXT, " +
                "collectionDate DATE, " +
                "caseID TEXT NOT NULL, " +
                "FOREIGN KEY (caseID) REFERENCES CaseFile(caseID) ON DELETE CASCADE)");

        // CHAIN OF CUSTODY
        stmt.execute("CREATE TABLE IF NOT EXISTS ChainOfCustody (" +
                "recordID TEXT PRIMARY KEY, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "action TEXT NOT NULL, " +
                "doneBy TEXT NOT NULL)");

        // CUSTODY RECORD EVIDENCE (Junction table)
        stmt.execute("CREATE TABLE IF NOT EXISTS CustodyRecordEvidence (" +
                "recordID TEXT NOT NULL, " +
                "evidenceID TEXT NOT NULL, " +
                "PRIMARY KEY (recordID, evidenceID), " +
                "FOREIGN KEY (recordID) REFERENCES ChainOfCustody(recordID) ON DELETE CASCADE, " +
                "FOREIGN KEY (evidenceID) REFERENCES Evidence(evidenceID) ON DELETE CASCADE)");

        // FORENSIC REQUEST
        stmt.execute("CREATE TABLE IF NOT EXISTS ForensicRequest (" +
                "requestID TEXT PRIMARY KEY, " +
                "status TEXT DEFAULT 'pending' CHECK (status IN ('pending','completed')), " +
                "caseID TEXT NOT NULL, " +
                "requestedBy TEXT NOT NULL, " +
                "evidenceType TEXT, " +
                "requestedDate DATE, " +
                "evidenceID TEXT NOT NULL, " +
                "analysisType TEXT, " +
                "priority TEXT NOT NULL CHECK (priority IN ('Urgent', 'High', 'Medium', 'low')), " +
                "FOREIGN KEY (evidenceID) REFERENCES Evidence(evidenceID), " +
                "FOREIGN KEY (caseID) REFERENCES CaseFile(caseID), " +
                "FOREIGN KEY (requestedBy) REFERENCES PoliceOfficer(officerID))");

        // FORENSIC REPORT
        stmt.execute("CREATE TABLE IF NOT EXISTS ForensicReport (" +
                "reportID TEXT PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "filename TEXT, " +
                "notes TEXT, " +
                "completionDate DATE, " +
                "uploadDate DATE, " +
                "status TEXT, " +
                "requestID TEXT UNIQUE NOT NULL, " +
                "uploadedBy TEXT NOT NULL, " +
                "FOREIGN KEY (requestID) REFERENCES ForensicRequest(requestID), " +
                "FOREIGN KEY (uploadedBy) REFERENCES ForensicExpert(expertID))");

        // COURT VERDICT
        stmt.execute("CREATE TABLE IF NOT EXISTS CourtVerdict (" +
                "verdictID TEXT PRIMARY KEY, " +
                "outcome TEXT NOT NULL CHECK (outcome IN ('guilty', 'not guilty')), " +
                "sentence TEXT, " +
                "dateIssued DATE, " +
                "notes TEXT, " +
                "caseID TEXT UNIQUE, " +
                "issuedBy TEXT NOT NULL, " +
                "FOREIGN KEY (caseID) REFERENCES CaseFile(caseID) ON DELETE CASCADE, " +
                "FOREIGN KEY (issuedBy) REFERENCES CourtOfficial(officialID))");
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        return conn;
    }

}