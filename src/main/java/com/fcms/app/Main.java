package com.fcms.app;

import com.fcms.controllers.policeOfficer.AddEvidenceController;
import com.fcms.controllers.policeOfficer.ManageParticipantsController;
import com.fcms.controllers.policeOfficer.RequestAnalysisController;
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
import java.util.Locale;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        SQLiteDatabase.initializeDatabase();
        TestDataInserter.insertTestData();
       showLoginScreen();
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/requestAnalysis.fxml"));
//        Parent root = loader.load();
//
//       RequestAnalysisController loginController = loader.getController();
//
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("/css/forms.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("/css/components.css").toExternalForm());
//        //scene.getStylesheets().add(getClass().getResource("/css/manageParticipants.css").toExternalForm());
//        primaryStage.setTitle("CaseVault - Login");
//        primaryStage.setScene(scene);
//        primaryStage.setMinWidth(400);
//        primaryStage.setMinHeight(600);
//        primaryStage.setMaximized(true);
//        primaryStage.show();
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
