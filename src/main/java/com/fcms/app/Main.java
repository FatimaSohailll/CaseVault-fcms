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
import com.fcms.database.SQLiteDatabase;
import com.fcms.models.UserSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class Main extends Application {

    private Stage primaryStage;
    private SceneManager sceneManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        SQLiteDatabase.initializeDatabase();
        insertTestData();

        showLoginScreen();
    }

    // ======================================================
    // SHOW DASHBOARD (AFTER LOGIN)
    // ======================================================
    private void showDashboard() {
        UserSession session = UserSession.getInstance();
        String role = session.getRole();

        try {
            loadMasterLayout(); // ALWAYS load master layout first

            switch (role) {
                case "Police Officer" ->
                        sceneManager.switchContent("/fxml/policeOfficer/policeDashboard.fxml");

                case "Forensic Expert" ->
                        sceneManager.switchContent("/fxml/forensicExpert/expertDashboard.fxml");

                case "Court Official" ->
                        sceneManager.switchContent("/fxml/courtOfficial/courtDashboard.fxml");

                case "System Admin" ->
                        sceneManager.switchContent("/fxml/systemAdmin/adminDashboard.fxml");

                default ->
                        sceneManager.switchContent("/fxml/policeOfficer/policeDashboard.fxml");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load dashboard for role: " + role);
        }
    }

    // ======================================================
    // MASTER LAYOUT LOADER (Sidebar + Topbar)
    // ======================================================
    private void loadMasterLayout() throws IOException {
        // create once per login
        sceneManager = new SceneManager(primaryStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/master.fxml"));

        // one factory to inject sceneManager into all controllers used in master.fxml and its includes
        loader.setControllerFactory(type -> {
            try {
                if (type == com.fcms.controllers.components.MasterLayoutController.class) {
                    var c = new com.fcms.controllers.components.MasterLayoutController();
                    c.setSceneManager(sceneManager);
                    return c;
                } else if (type == com.fcms.controllers.components.SidebarController.class) {
                    var c = new com.fcms.controllers.components.SidebarController();
                    c.setSceneManager(sceneManager);
                    return c;
                } else if (type == com.fcms.controllers.components.TopbarController.class) {
                    var c = new com.fcms.controllers.components.TopbarController();
                    c.setSceneManager(sceneManager);
                    return c;
                }
                // default for any other controller
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/auth.css").toExternalForm());

        primaryStage.setTitle("CaseVault Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    // ======================================================
    // LOGIN SCREEN
    // ======================================================
    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setOnNavigateToSignup(this::showSignupScreen);
            loginController.setOnLoginSuccess(this::showDashboard);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/auth.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

            primaryStage.setTitle("CaseVault - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ======================================================
    // SIGNUP SCREEN
    // ======================================================
    private void showSignupScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/signup.fxml"));
            Parent root = loader.load();

            SignupController controller = loader.getController();
            controller.setOnNavigateToLogin(this::showLoginScreen);
            controller.setOnSubmitSuccess(() -> {
                showAlert("Success", "Signup request sent for admin approval.");
                showLoginScreen();
            });

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/auth.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

            primaryStage.setTitle("CaseVault - Sign Up");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // ======================================================
    // INSERT TEST DATA (unchanged)
    // ======================================================
    private void insertTestData() {
        try (Connection conn = SQLiteDatabase.getConnection();
             Statement stmt = conn.createStatement()) {

            // your full test insert code...
            System.out.println("Test data inserted.");

        } catch (Exception e) {
            System.out.println("Error inserting test data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
