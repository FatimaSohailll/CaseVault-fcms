BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "CaseFile" (
	"caseID"	TEXT,
	"title"	TEXT NOT NULL,
	"description"	TEXT,
	"location"	TEXT,
	"type"	TEXT,
	"status"	TEXT DEFAULT 'open' CHECK("status" IN ('open', 'closed', 'archived', 'submitted')),
	"priority"	TEXT CHECK("priority" IN ('high', 'low', 'medium')),
	"assignedOfficer"	TEXT,
	"dateRegistered"	DATE NOT NULL,
	"reviewedBy"	TEXT,
	"close_reason"	TEXT,
	"final_report"	TEXT,
	PRIMARY KEY("caseID"),
	FOREIGN KEY("assignedOfficer") REFERENCES "PoliceOfficer"("officerID"),
	FOREIGN KEY("reviewedBy") REFERENCES "CourtOfficial"("officialID")
);
CREATE TABLE IF NOT EXISTS "CaseParticipants" (
	"caseID"	TEXT NOT NULL,
	"participantID"	TEXT NOT NULL,
	PRIMARY KEY("caseID","participantID"),
	FOREIGN KEY("caseID") REFERENCES "CaseFile"("caseID") ON DELETE CASCADE,
	FOREIGN KEY("participantID") REFERENCES "Participant"("participantID") ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "ChainOfCustody" (
	"recordID"	TEXT,
	"timestamp"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	"action"	TEXT NOT NULL,
	"doneBy"	TEXT NOT NULL,
	"evidenceID"	TEXT NOT NULL,
	PRIMARY KEY("recordID"),
	FOREIGN KEY("evidenceID") REFERENCES "Evidence"("evidenceID")
);
CREATE TABLE IF NOT EXISTS "CourtOfficial" (
	"officialID"	TEXT,
	"courtName"	TEXT NOT NULL,
	"designation"	TEXT,
	PRIMARY KEY("officialID"),
	FOREIGN KEY("officialID") REFERENCES "UserAccount"("userID") ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "CourtVerdict" (
	"verdictID"	TEXT,
	"outcome"	TEXT NOT NULL CHECK("outcome" IN ('guilty', 'not guilty')),
	"sentence"	TEXT,
	"dateIssued"	DATE,
	"notes"	TEXT,
	"caseID"	TEXT UNIQUE,
	"issuedBy"	TEXT NOT NULL,
	PRIMARY KEY("verdictID"),
	FOREIGN KEY("caseID") REFERENCES "CaseFile"("caseID") ON DELETE CASCADE,
	FOREIGN KEY("issuedBy") REFERENCES "CourtOfficial"("officialID")
);
CREATE TABLE IF NOT EXISTS "Evidence" (
	"evidenceID"	TEXT,
	"type"	TEXT NOT NULL,
	"description"	TEXT,
	"filename"	TEXT,
	"location"	TEXT,
	"collectionDate"	DATE,
	"caseID"	TEXT NOT NULL,
	PRIMARY KEY("evidenceID"),
	FOREIGN KEY("caseID") REFERENCES "CaseFile"("caseID") ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "ForensicExpert" (
	"expertID"	TEXT,
	"labName"	TEXT,
	PRIMARY KEY("expertID"),
	FOREIGN KEY("expertID") REFERENCES "UserAccount"("userID") ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "ForensicReport" (
	"reportID"	TEXT,
	"title"	TEXT NOT NULL,
	"filename"	TEXT,
	"notes"	TEXT,
	"completionDate"	DATE,
	"uploadDate"	DATE,
	"status"	TEXT,
	"requestID"	TEXT NOT NULL UNIQUE,
	"uploadedBy"	TEXT NOT NULL,
	PRIMARY KEY("reportID"),
	FOREIGN KEY("requestID") REFERENCES "ForensicRequest"("requestID"),
	FOREIGN KEY("uploadedBy") REFERENCES "ForensicExpert"("expertID")
);
CREATE TABLE IF NOT EXISTS "ForensicRequest" (
	"requestID"	TEXT,
	"expertID"	TEXT NOT NULL,
	"status"	TEXT DEFAULT 'pending' CHECK("status" IN ('pending', 'completed')),
	"requestedBy"	TEXT NOT NULL,
	"evidenceType"	TEXT,
	"requestedDate"	DATE,
	"evidenceID"	TEXT NOT NULL,
	"analysisType"	TEXT,
	"priority"	TEXT NOT NULL CHECK("priority" IN ('Urgent', 'High', 'Medium', 'low')),
	PRIMARY KEY("requestID"),
	FOREIGN KEY("evidenceID") REFERENCES "Evidence"("evidenceID"),
	FOREIGN KEY("expertID") REFERENCES "ForensicExpert"("expertID"),
	FOREIGN KEY("requestedBy") REFERENCES "PoliceOfficer"("officerID")
);
CREATE TABLE IF NOT EXISTS "Participant" (
	"participantID"	TEXT,
	"name"	TEXT NOT NULL,
	"role"	TEXT NOT NULL CHECK("role" IN ('victim', 'suspect')),
	"contact"	TEXT,
	"idType"	TEXT,
	"idNumber"	TEXT,
	PRIMARY KEY("participantID")
);
CREATE TABLE IF NOT EXISTS "PoliceOfficer" (
	"officerID"	TEXT,
	"rank"	TEXT,
	"department"	TEXT,
	PRIMARY KEY("officerID"),
	FOREIGN KEY("officerID") REFERENCES "UserAccount"("userID") ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "SystemAdmin" (
	"adminID"	TEXT,
	"name"	TEXT NOT NULL,
	"password"	TEXT NOT NULL,
	PRIMARY KEY("adminID")
);
CREATE TABLE IF NOT EXISTS "UserAccount" (
	"userID"	TEXT,
	"username"	TEXT NOT NULL UNIQUE,
	"email"	TEXT NOT NULL UNIQUE,
	"name"	TEXT NOT NULL,
	"password"	TEXT NOT NULL,
	"role"	TEXT NOT NULL CHECK("role" IN ('Police Officer', 'Court Official', 'Forensic Expert')),
	"managedBY"	TEXT NOT NULL,
	"approved"	BOOL NOT NULL,
	"createdAt"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY("userID")
);
CREATE TABLE IF NOT EXISTS "UserHistory" (
	"historyID"	INTEGER,
	"actor"	TEXT NOT NULL,
	"action"	TEXT NOT NULL,
	"timestamp"	DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY("historyID" AUTOINCREMENT)
);
COMMIT;
