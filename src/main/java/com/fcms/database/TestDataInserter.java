package com.fcms.database;

import java.sql.Connection;
import java.sql.Statement;

public class TestDataInserter {

    public static void insertTestData() {
        String sql = """
           INSERT OR IGNORE INTO UserAccount (userID, username, email, name, password, role, managedBy, approved) VALUES
            ('PO00003', 'Fatima455', 'fatima@gmail.com', 'Fatima', 'fatima@455', 'Police Officer', 'A00001', true),
                  ('EX00001', 'Fatima1', 'fatima0@gmail.com', 'Fatima', 'fatima@455', 'Forensic Expert', 'A00001', true),
                  ('EX00002', 'Fatima2', 'fatima1@gmail.com', 'Fatima', 'fatima@455', 'Forensic Expert', 'A00001', true),
                  ('EX00003', 'Fatima3', 'fatima2@gmail.com', 'Fatima', 'fatima@455', 'Forensic Expert', 'A00001', true),
                  ('EX00004', 'Fatima4', 'fatima4@gmail.com', 'Fatima', 'fatima@455', 'Forensic Expert', 'A00001', true);
           
            -- Insert police officer details
            INSERT OR IGNORE INTO PoliceOfficer (officerID, rank, department) VALUES
            ('PO00003', 'Detective', 'Narcotics Division'),
            ('PO00004', 'Detective', 'Crime Scene Unit'),
            ('PO00005', 'Officer', 'Patrol Division'),
            ('PO00006', 'Detective', 'Cyber Crimes Unit');
            
            -- Insert a system admin (for managedBY reference)
            INSERT OR IGNORE INTO SystemAdmin (adminID, name, password) VALUES
            ('A00001', 'System Administrator', 'admin123');
            
            -- Insert some sample case files (with correct priority values)
            INSERT OR IGNORE INTO CaseFile (caseID, title, description, location, type, status, priority, assignedOfficer, dateRegistered) VALUES
            ('CS00001', 'Downtown Homicide', 'Homicide case with DNA evidence', 'Downtown District', 'Homicide', 'open', 'high', 'PO00001', '2025-11-15'),
            ('CS00002', 'Bank Robbery', 'Bank robbery with fingerprint evidence', 'Financial District', 'Robbery', 'open', 'high', 'PO00002', '2025-11-14'),
            ('CS00003', 'Gang Shooting', 'Gang-related shooting with ballistic evidence', 'East Side', 'Violent Crime', 'open', 'high', 'PO00003', '2025-11-16'),
            ('CS00004', 'Drug Overdose', 'Suspicious death with toxicology evidence', 'West District', 'Narcotics', 'closed', 'medium', 'PO00004', '2025-11-10'),
            ('CS00005', 'Cyber Fraud', 'Online fraud case with digital evidence', 'Citywide', 'Cyber Crime', 'open', 'high', 'PO00001', '2025-11-13'),
            ('CS00006', 'Cold Case Review', 'Cold case with new DNA evidence', 'Central District', 'Cold Case', 'open', 'low', 'PO00005', '2025-11-17'),
            ('CS00007', 'Burglary Series', 'Multiple burglaries with fingerprint evidence', 'North District', 'Property Crime', 'closed', 'medium', 'PO00002', '2025-11-09'),
            ('CS00008', 'Armed Robbery', 'Armed robbery with ballistic evidence', 'South District', 'Robbery', 'open', 'high', 'PO00006', '2025-11-18');
            
            -- Insert sample participants
            INSERT OR IGNORE INTO participant (participantID, name, role, contact, idType, idNumber) VALUES
            ('P-001', 'John Anderson', 'Suspect', '555-0123', 'Driver License', 'DL-8765432'),
            ('P-002', 'Sarah Mitchell', 'Victim', '555-0124', 'Passport', 'PS-1234567'),
            ('P-003', 'Michael Chen', 'Witness', '555-0125', 'State ID', 'ID-9876543'),
            ('P-004', 'Emily Rodriguez', 'Suspect', '555-0126', 'Driver License', 'DL-4567891'),
            ('P-005', 'David Thompson', 'Victim', '555-0127', 'Passport', 'PS-7654321'),
            ('P-006', 'Jennifer Walsh', 'Witness', '555-0128', 'State ID', 'ID-1239876'),
            ('P-007', 'Robert Johnson', 'Suspect', '555-0129', 'Driver License', 'DL-6543219'),
            ('P-008', 'Lisa Garcia', 'Victim', '555-0130', 'Passport', 'PS-9876123'),
            ('P-009', 'James Wilson', 'Witness', '555-0131', 'State ID', 'ID-4561237'),
            ('P-010', 'Maria Martinez', 'Suspect', '555-0132', 'Driver License', 'DL-7891234');
            
            -- Insert case participants (linking participants to cases)
            INSERT OR IGNORE INTO caseParticipants (caseID, participantID) VALUES
            ('CS00001', 'P-001'),
            ('CS00001', 'P-002'),
            ('CS00001', 'P-003'),
            ('CS00002', 'P-004'),
            ('CS00002', 'P-005'),
            ('CS00003', 'P-006'),
            ('CS00003', 'P-007'),
            ('CS00004', 'P-008'),
            ('CS00004', 'P-009'),
            ('CS00005', 'P-010'),
            ('CS00005', 'P-001'),
            ('CS00006', 'P-002');
            
            -- Insert some sample evidence
            INSERT OR IGNORE INTO Evidence (evidenceID, type, description, filename, location, collectionDate, caseID) VALUES
            ('EV00001', 'DNA', 'Blood sample from crime scene', 'dna_sample_001.pdf', 'Evidence Room A', '2025-11-15', 'CS00001'),
            ('EV00002', 'Fingerprint', 'Latent prints from counter', 'fingerprints_002.pdf', 'Evidence Room B', '2025-11-14', 'CS00002'),
            ('EV00003', 'Ballistics', '9mm bullet casing', 'bullet_casing_003.pdf', 'Evidence Room C', '2025-11-16', 'CS00003'),
            ('EV00004', 'Toxicology', 'Blood samples for screening', 'blood_samples_004.pdf', 'Evidence Room D', '2025-11-10', 'CS00004'),
            ('EV00005', 'Digital', 'Mobile device for analysis', 'mobile_device_005.pdf', 'Evidence Room E', '2025-11-13', 'CS00005'),
            ('EV00006', 'DNA', 'Hair sample from cold case', 'hair_sample_006.pdf', 'Evidence Room A', '2025-11-17', 'CS00006'),
            ('EV00007', 'Fingerprint', 'Prints from burglary scene', 'fingerprints_007.pdf', 'Evidence Room B', '2025-11-09', 'CS00007'),
            ('EV00008', 'Ballistics', 'Firearm for ballistic testing', 'firearm_008.pdf', 'Evidence Room C', '2025-11-18', 'CS00008'),
            ('EV00009', 'DNA', 'Blood sample from victim P00001', 'dna_victim_009.pdf', 'Evidence Room A', '2025-11-15', 'CS00001'),
            ('EV00010', 'Fingerprint', 'Prints from suspect P00005', 'fingerprint_suspect_010.pdf', 'Evidence Room B', '2025-11-14', 'CS00002'),
            ('EV00011', 'Digital', 'Laptop from suspect P00013', 'laptop_suspect_011.pdf', 'Evidence Room E', '2025-11-13', 'CS00005'),
            ('EV00012', 'Ballistics', 'Weapon from suspect P00019', 'weapon_suspect_012.pdf', 'Evidence Room C', '2025-11-18', 'CS00008');
            
            INSERT OR IGNORE INTO ForensicExpert VALUES
            ('EX00003', 'zenith'),
            ('EX00001', 'highstarts'),
            ('EX00002', 'lably'),
            ('EX00004', 'hehehe');
                   
            -- Insert sample records into ForensicRequest table with correct priority values
            INSERT OR IGNORE INTO ForensicRequest (requestID, expertID, status, requestedBy, evidenceType, requestedDate, evidenceID, analysisType, priority) VALUES
            ('FR00001', 'EX00003', 'pending', 'PO00001', 'DNA', '2025-11-20', 'EV00001', 'DNA Analysis', 'High'),
            ('FR00002', 'EX00003', 'pending', 'PO00002', 'Fingerprint', '2025-11-19', 'EV00002', 'Fingerprint Analysis', 'Urgent'),
            ('FR00003', 'EX00003', 'pending', 'PO00003', 'Ballistics', '2025-11-21', 'EV00003', 'Ballistics Analysis', 'Medium'),
            ('FR00004', 'EX00003', 'completed', 'PO00004', 'Toxicology', '2025-11-18', 'EV00004', 'Toxicology Screening', 'Medium'),
            ('FR00005', 'EX00003', 'pending', 'PO00001', 'Digital', '2025-11-19', 'EV00005', 'Digital Forensics', 'High'),
            ('FR00006', 'EX00003', 'pending', 'PO00005', 'DNA', '2025-11-22', 'EV00006', 'DNA Analysis', 'low'),
            ('FR00007', 'EX00003', 'completed', 'PO00002', 'Fingerprint', '2025-11-17', 'EV00007', 'Fingerprint Analysis', 'Medium'),
            ('FR00008', 'EX00003', 'pending',  'PO00006', 'Ballistics', '2025-11-21', 'EV00008', 'Ballistics Analysis', 'Urgent'),
            ('FR00009', 'EX00001', 'pending', 'PO00001', 'DNA', '2025-11-20', 'EV00009', 'DNA Analysis', 'High'),
            ('FR00010', 'EX00001', 'pending', 'PO00002', 'Fingerprint', '2025-11-19', 'EV00010', 'Fingerprint Analysis', 'Urgent'),
            ('FR00011', 'EX00001', 'pending', 'PO00001', 'Digital', '2025-11-19', 'EV00011', 'Digital Forensics', 'High'),
            ('FR00012', 'EX00003', 'pending', 'PO00006', 'Ballistics', '2025-11-21', 'EV00012', 'Ballistics Analysis', 'Urgent');
           
               
                   """;

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Test data inserted successfully!");

        } catch (Exception e) {
            System.out.println("Error inserting test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}