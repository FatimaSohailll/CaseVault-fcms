package com.fcms.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.fcms.controllers.auth.LoginController;
import com.fcms.controllers.auth.SignupController;
import com.fcms.database.*;
import com.fcms.controllers.components.SidebarController;
import com.fcms.controllers.policeOfficer.MainLayoutController;
import com.fcms.models.UserSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Locale;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        SQLiteDatabase.initializeDatabase();

        String sql = """

                -- 1. SYSTEM ADMIN (must be first)
           INSERT OR IGNORE INTO SystemAdmin (adminID, name, password)
           VALUES ('A00001', 'System Administrator', 'admin123');

           -----------------------------------------------------------
           -- 2. USER ACCOUNTS (for police + forensic experts)
           -----------------------------------------------------------
           INSERT OR IGNORE INTO UserAccount
           (userID, username, email, name, password, role, managedBY, approved)
           VALUES
           ('PO00001', 'po00001', 'po00001@police.gov', 'Officer PO00001', 'pass123', 'Police Officer', 'A00001', 1),
           ('PO00002', 'po00002', 'po00002@police.gov', 'Officer PO00002', 'pass123', 'Police Officer', 'A00001', 1),
           ('PO00003', 'po00003', 'po00003@police.gov', 'Detective PO00003', 'pass123', 'Police Officer', 'A00001', 1),
           ('PO00004', 'po00004', 'po00004@police.gov', 'Detective PO00004', 'pass123', 'Police Officer', 'A00001', 1),
           ('PO00005', 'po00005', 'po00005@police.gov', 'Officer PO00005', 'pass123', 'Police Officer', 'A00001', 1),
           ('PO00006', 'po00006', 'po00006@police.gov', 'Detective PO00006', 'pass123', 'Police Officer', 'A00001', 1),

           ('EX00001', 'expert1', 'expert1@lab.com', 'Forensic Expert 1', 'pass123', 'Forensic Expert', 'A00001', 1),
           ('EX00003', 'expert3', 'expert3@lab.com', 'Forensic Expert 3', 'pass123', 'Forensic Expert', 'A00001', 1);

           -----------------------------------------------------------
           -- 3. POLICE OFFICER DETAILS (must match userIDs)
           -----------------------------------------------------------
           INSERT OR IGNORE INTO PoliceOfficer (officerID, rank, department) VALUES
           ('PO00001', 'Officer', 'Patrol Division'),
           ('PO00002', 'Detective', 'Robbery Division'),
           ('PO00003', 'Detective', 'Narcotics Division'),
           ('PO00004', 'Detective', 'Crime Scene Unit'),
           ('PO00005', 'Officer', 'Patrol Division'),
           ('PO00006', 'Detective', 'Cyber Crimes Unit');

           -----------------------------------------------------------
           -- 4. FORENSIC EXPERT DETAILS
           -----------------------------------------------------------
           INSERT OR IGNORE INTO ForensicExpert (expertID, labName) VALUES
           ('EX00001', 'Central Forensics Lab'),
           ('EX00003', 'Central Forensics Lab');

           -----------------------------------------------------------
           -- 5. CASE FILES (officer IDs now valid)
           -----------------------------------------------------------
           INSERT OR IGNORE INTO CaseFile
           (caseID, title, description, location, type, status, priority, assignedOfficer, dateRegistered)
           VALUES
           ('CS00001', 'Downtown Homicide', 'Homicide case with DNA evidence', 'Downtown District', 'Homicide', 'open', 'high', 'PO00001', '2025-11-15'),
           ('CS00002', 'Bank Robbery', 'Bank robbery with fingerprint evidence', 'Financial District', 'Robbery', 'open', 'high', 'PO00002', '2025-11-14'),
           ('CS00003', 'Gang Shooting', 'Gang-related shooting with ballistic evidence', 'East Side', 'Violent Crime', 'open', 'high', 'PO00003', '2025-11-16'),
           ('CS00004', 'Drug Overdose', 'Suspicious death with toxicology evidence', 'West District', 'Narcotics', 'closed', 'medium', 'PO00004', '2025-11-10'),
           ('CS00005', 'Cyber Fraud', 'Online fraud case with digital evidence', 'Citywide', 'Cyber Crime', 'open', 'high', 'PO00001', '2025-11-13'),
           ('CS00006', 'Cold Case Review', 'Cold case with new DNA evidence', 'Central District', 'Cold Case', 'open', 'low', 'PO00005', '2025-11-17'),
           ('CS00007', 'Burglary Series', 'Multiple burglaries with fingerprint evidence', 'North District', 'Property Crime', 'closed', 'medium', 'PO00002', '2025-11-09'),
           ('CS00008', 'Armed Robbery', 'Armed robbery with ballistic evidence', 'South District', 'Robbery', 'open', 'high', 'PO00006', '2025-11-18');

           -----------------------------------------------------------
           -- 6. EVIDENCE (case IDs now valid)
           -----------------------------------------------------------
           INSERT OR IGNORE INTO Evidence (evidenceID, type, description, filename, location, collectionDate, caseID) VALUES
           ('EV00001','DNA','Blood sample from crime scene','dna_sample_001.pdf','Evidence Room A','2025-11-15','CS00001'),
           ('EV00002','Fingerprint','Latent prints from counter','fingerprints_002.pdf','Evidence Room B','2025-11-14','CS00002'),
           ('EV00003','Ballistics','9mm bullet casing','bullet_casing_003.pdf','Evidence Room C','2025-11-16','CS00003'),
           ('EV00004','Toxicology','Blood samples','blood_samples_004.pdf','Evidence Room D','2025-11-10','CS00004'),
           ('EV00005','Digital','Mobile device','mobile_device_005.pdf','Evidence Room E','2025-11-13','CS00005'),
           ('EV00006','DNA','Hair sample','hair_sample_006.pdf','Evidence Room A','2025-11-17','CS00006'),
           ('EV00007','Fingerprint','Prints','fingerprints_007.pdf','Evidence Room B','2025-11-09','CS00007'),
           ('EV00008','Ballistics','Firearm','firearm_008.pdf','Evidence Room C','2025-11-18','CS00008'),
           ('EV00009','DNA','Blood sample','dna_victim_009.pdf','Evidence Room A','2025-11-15','CS00001'),
           ('EV00010','Fingerprint','Suspect prints','fingerprint_suspect_010.pdf','Evidence Room B','2025-11-14','CS00002'),
           ('EV00011','Digital','Laptop','laptop_suspect_011.pdf','Evidence Room E','2025-11-13','CS00005'),
           ('EV00012','Ballistics','Weapon','weapon_suspect_012.pdf','Evidence Room C','2025-11-18','CS00008');

           -----------------------------------------------------------
           -- 7. FORENSIC REQUESTS (now all FK references valid)
           -----------------------------------------------------------
           INSERT OR IGNORE INTO ForensicRequest
           (requestID, expertID, status, requestedBy, evidenceType, requestedDate, evidenceID, analysisType, priority)
           VALUES
           ('FR00001','EX00003','pending','PO00001','DNA','2025-11-20','EV00001','DNA Analysis','High'),
           ('FR00002','EX00003','pending','PO00002','Fingerprint','2025-11-19','EV00002','Fingerprint Analysis','Urgent'),
           ('FR00003','EX00003','pending','PO00003','Ballistics','2025-11-21','EV00003','Ballistics Analysis','Medium'),
           ('FR00004','EX00003','completed','PO00004','Toxicology','2025-11-18','EV00004','Toxicology Screening','Medium'),
           ('FR00005','EX00003','pending','PO00001','Digital','2025-11-19','EV00005','Digital Forensics','High'),
           ('FR00006','EX00003','pending','PO00005','DNA','2025-11-22','EV00006','DNA Analysis','low'),
           ('FR00007','EX00003','completed','PO00002','Fingerprint','2025-11-17','EV00007','Fingerprint Analysis','Medium'),
           ('FR00008','EX00003','pending','PO00006','Ballistics','2025-11-21','EV00008','Ballistics Analysis','Urgent'),
           ('FR00009','EX00001','pending','PO00001','DNA','2025-11-20','EV00009','DNA Analysis','High'),
           ('FR00010','EX00001','pending','PO00002','Fingerprint','2025-11-19','EV00010','Fingerprint Analysis','Urgent'),
           ('FR00011','EX00001','pending','PO00001','Digital','2025-11-19','EV00011','Digital Forensics','High'),
           ('FR00012','EX00003','pending','PO00006','Ballistics','2025-11-21','EV00012','Ballistics Analysis','Urgent');

           """;

        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Test data inserted successfully!");

        } catch (Exception e) {
            System.out.println("Error inserting test data: " + e.getMessage());
            e.printStackTrace();
        }

        showLoginScreen();
    }

    private void showDashboard() {
        UserSession session = UserSession.getInstance();
        String rawRole = session.getRole();
        String role = (rawRole == null) ? "" : rawRole.trim();

        System.out.println("Navigating to dashboard for role (raw): " + rawRole);
        System.out.println("Navigating to dashboard for role (normalized): " + role);

        // Normalize common variants to avoid mismatches
        String roleLower = role.toLowerCase(Locale.ROOT);

        if (roleLower.equals("police") || roleLower.equals("police officer")) {
            loadPoliceMainLayout();
            return;
        }

        // Other roles use partner dashboards
        try {
            String fxmlFile = getDashboardFXML(role);
            System.out.println("Loading FXML for role: " + role + " -> " + fxmlFile);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            setupDashboardController(loader.getController());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

            primaryStage.setTitle("CaseVault - " + role + " Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load dashboard for role: " + role);
        }
    }

    private void loadPoliceMainLayout() {
        try {
            SceneManager sceneManager = new SceneManager(primaryStage);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/mainLayout.fxml"));

            // Inject SceneManager into SidebarController
            loader.setControllerFactory(type -> {
                try {
                    if (type == SidebarController.class) {
                        SidebarController sc = new SidebarController();
                        sc.setSceneManager(sceneManager);
                        return sc;
                    }
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent root = loader.load();
            MainLayoutController dashboardController = loader.getController();
            System.out.println("Loaded controller: " + dashboardController.getClass().getSimpleName());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());

            primaryStage.setTitle("CaseVault - Police Officer Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Police Officer main layout.");
        }
    }

    private String getDashboardFXML(String role) {
        // Police is handled by loadPoliceMainLayout(), so no police case here
        switch (role) {
            case "Forensic Expert":
            case "forensic expert":
                return "/fxml/forensicExpert/expertDashboard.fxml";
            case "Court Official":
            case "court official":
                return "/fxml/courtOfficial/courtDashboard.fxml"; // Adjust path as needed
            default:
                // Fallback to policeOfficer dashboard if an unknown role sneaks in
                return "/fxml/policeOfficer/policeDashboard.fxml";
        }
    }

    private void setupDashboardController(Object controller) {
        if (controller != null) {
            System.out.println("Dashboard controller type: " + controller.getClass().getSimpleName());
        } else {
            System.out.println("Dashboard controller is null for this FXML.");
        }
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setOnNavigateToSignup(this::showSignupScreen);
            loginController.setOnNavigateToForgotPassword(this::showForgotPasswordScreen);
            loginController.setOnLoginSuccess(this::showDashboard);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

            primaryStage.setTitle("CaseVault - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(600);
            primaryStage.setMaximized(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading login screen: " + e.getMessage());
        }
    }

    private void showSignupScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/signup.fxml"));
            Parent root = loader.load();

            SignupController signupController = loader.getController();
            signupController.setOnNavigateToLogin(this::showLoginScreen);
            signupController.setOnSubmitSuccess(() -> {
                showAlert("Success", "Your account request has been submitted for admin review.");
                showLoginScreen();
            });

            Scene scene = new Scene(root);
            primaryStage.setTitle("CaseVault - Sign Up");
            primaryStage.setScene(scene);
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
            primaryStage.setMinWidth(420);
            primaryStage.setMinHeight(650);
            primaryStage.setMaximized(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showForgotPasswordScreen() {
        showAlert("Info", "Forgot password feature coming soon!");
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
