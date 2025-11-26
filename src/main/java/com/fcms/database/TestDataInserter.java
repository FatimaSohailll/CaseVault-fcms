package com.fcms.database;

import java.sql.Connection;
import java.sql.Statement;

public class TestDataInserter {
    public static void insertTestData() {
        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Inserting dummy data...");

            // -------------------------
            // 1. SYSTEM ADMIN
            // -------------------------
            stmt.execute("""
                INSERT OR IGNORE INTO SystemAdmin (adminID, name, password)
                VALUES ('A00001', 'System Administrator', 'admin123')
            """);

            // -------------------------
            // 2. USER ACCOUNTS (9 total + 3 unapproved)
            // -------------------------

            // Police Officers PO00001–PO00003 (approved)
            for (int i = 1; i <= 3; i++) {
                String id = String.format("PO%05d", i);
                stmt.execute(String.format("""
                    INSERT OR IGNORE INTO UserAccount
                    (userID, username, email, name, password, role, managedBY, approved)
                    VALUES ('%s', 'police%s', 'police%s@mail.com', 'Police Officer %s', 'pass123',
                            'Police Officer', 'A00001', 1)
                """, id, i, i, i));

                stmt.execute(String.format("""
                    INSERT OR IGNORE INTO PoliceOfficer (officerID, rank, department)
                    VALUES ('%s', 'Sergeant', 'Field Unit %s')
                """, id, i));
            }

            // Forensic Experts EX00001–EX00003 (approved)
            for (int i = 1; i <= 3; i++) {
                String id = String.format("EX%05d", i);
                stmt.execute(String.format("""
                    INSERT OR IGNORE INTO UserAccount
                    (userID, username, email, name, password, role, managedBY, approved)
                    VALUES ('%s', 'expert%s', 'expert%s@mail.com',
                            'Forensic Expert %s', 'pass123', 'Forensic Expert', 'A00001', 1)
                """, id, i, i, i));

                stmt.execute(String.format("""
                    INSERT OR IGNORE INTO ForensicExpert (expertID, labName)
                    VALUES ('%s', 'Central Forensics Lab')
                """, id));
            }

            // Court Officials CR00001–CR00003 (approved)
            for (int i = 1; i <= 3; i++) {
                String id = String.format("CR%05d", i);
                stmt.execute(String.format("""
                    INSERT OR IGNORE INTO UserAccount
                    (userID, username, email, name, password, role, managedBY, approved)
                    VALUES ('%s', 'court%s', 'court%s@mail.com',
                            'Court Official %s', 'pass123', 'Court Official', 'A00001', 1)
                """, id, i, i, i));

                stmt.execute(String.format("""
                    INSERT OR IGNORE INTO CourtOfficial (officialID, courtName, designation)
                    VALUES ('%s', 'City Court %s', 'Judge')
                """, id, i));
            }

            // -------------------------
            // 2B. UNAPPROVED USERS
            // -------------------------

            // Police Officer unapproved
            stmt.execute("""
                INSERT OR IGNORE INTO UserAccount
                (userID, username, email, name, password, role, managedBY, approved)
                VALUES ('PO99999', 'policePending', 'pendingPO@mail.com', 'Pending Police Officer',
                        'pass123', 'Police Officer', 'A00001', 0)
            """);
            stmt.execute("INSERT OR IGNORE INTO PoliceOfficer VALUES ('PO99999','Constable','Pending Unit')");

            // Forensic Expert unapproved
            stmt.execute("""
                INSERT OR IGNORE INTO UserAccount
                (userID, username, email, name, password, role, managedBY, approved)
                VALUES ('EX99999', 'expertPending', 'pendingEX@mail.com', 'Pending Expert',
                        'pass123', 'Forensic Expert', 'A00001', 0)
            """);
            stmt.execute("INSERT OR IGNORE INTO ForensicExpert VALUES ('EX99999','Pending Lab')");

            // Court Official unapproved
            stmt.execute("""
                INSERT OR IGNORE INTO UserAccount
                (userID, username, email, name, password, role, managedBY, approved)
                VALUES ('CR99999', 'courtPending', 'pendingCR@mail.com', 'Pending Court Official',
                        'pass123', 'Court Official', 'A00001', 0)
            """);
            stmt.execute("""
                INSERT OR IGNORE INTO CourtOfficial
                VALUES ('CR99999','City Court Pending','Assistant Judge')
            """);

            // -------------------------
            // 3. CASE FILES (30)
            // -------------------------
            String[] types = {"Theft", "Assault", "Fraud", "Robbery", "Harassment"};
            String[] locations = {"Downtown", "Northside", "Southside", "East District", "West End"};
            String[] priorities = {"high", "medium", "low"};

            for (int i = 1; i <= 30; i++) {

                String caseID = String.format("CS%05d", i);

                String assignedOfficer = String.format("PO%05d", ((i % 3) + 1));
                String type = types[i % types.length];
                String location = locations[i % locations.length];
                String priority = priorities[i % priorities.length];

                // Submit every 5th case to court
                boolean submitted = (i % 5 == 0);
                String status = submitted ? "submitted" : "open";
                String reviewedBy = submitted ? String.format("CR%05d", ((i % 3) + 1)) : null;

                stmt.execute(String.format("""
                    INSERT OR IGNORE INTO CaseFile
                    (caseID, title, description, location, type, status, priority,
                     assignedOfficer, dateRegistered, reviewedBy)
                    VALUES ('%s', 'Case Title %s', 'Description for case %s',
                            '%s', '%s', '%s', '%s', '%s', '2025-11-%02d', %s)
                """,
                        caseID, i, i, location, type, status, priority, assignedOfficer,
                        ((i % 28) + 1),
                        (reviewedBy == null ? "NULL" : "'" + reviewedBy + "'")
                ));

                // -------------------------
                // 4. Participants (0–2 per case)
                // -------------------------
                for (int p = 0; p < (i % 3); p++) {
                    String pid = String.format("PA%05d", (i * 2 + p));
                    String role = (p % 2 == 0) ? "victim" : "suspect";

                    stmt.execute(String.format("""
                        INSERT OR IGNORE INTO Participant
                        (participantID, name, role, contact)
                        VALUES ('%s', 'Participant %s_%s', '%s', '03XXXXXXXXX')
                    """, pid, i, p, role));

                    stmt.execute(String.format("""
                        INSERT OR IGNORE INTO CaseParticipants (caseID, participantID)
                        VALUES ('%s', '%s')
                    """, caseID, pid));
                }

                // -------------------------
                // 5. Evidence (0–2 per case)
                // -------------------------
                for (int ev = 0; ev < (i % 3); ev++) {
                    String eid = String.format("EV%05d", (i * 3 + ev));

                    stmt.execute(String.format("""
                        INSERT OR IGNORE INTO Evidence
                        (evidenceID, type, description, filename, location, collectionDate, caseID)
                        VALUES ('%s', 'Physical', 'Evidence description %s_%s',
                                'file_%s.png', '%s', '2025-11-%02d', '%s')
                    """, eid, i, ev, eid, location, ((i % 28) + 1), caseID));
                }

                // -------------------------
                // 6. Forensic Requests + Reports (0–2 per case)
                // -------------------------
                for (int r = 0; r < (i % 3); r++) {
                    String rid = String.format("RQ%05d", (i * 4 + r));
                    String eid = String.format("EV%05d", (i * 3 + r));
                    String expert = String.format("EX%05d", ((r % 3) + 1));
                    String police = assignedOfficer;

                    stmt.execute(String.format("""
                        INSERT OR IGNORE INTO ForensicRequest
                        (requestID, expertID, status, requestedBy, evidenceType,
                         requestedDate, evidenceID, analysisType, priority)
                        VALUES ('%s', '%s', 'completed', '%s', 'Physical',
                                '2025-11-%02d', '%s', 'Full Analysis', 'Medium')
                    """, rid, expert, police, ((i % 28) + 1), eid));

                    stmt.execute(String.format("""
                        INSERT OR IGNORE INTO ForensicReport
                        (reportID, title, filename, notes, completionDate, uploadDate,
                         status, requestID, uploadedBy)
                        VALUES ('RP%05d', 'Report %s', 'report_%s.pdf', 'Notes...',
                                '2025-11-26', '2025-11-27', 'completed', '%s', '%s')
                    """, (i * 4 + r), caseID, caseID, rid, expert));
                }
            }

            System.out.println("Dummy data inserted.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error inserting dummy data: " + e.getMessage());
        }
    }
}
