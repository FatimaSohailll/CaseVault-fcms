package com.fcms.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.fcms.controllers.components.SidebarController;
import com.fcms.controllers.policeOfficer.MainLayoutController;
import com.fcms.database.SQLiteDatabase;
import com.fcms.controllers.auth.LoginController;
import com.fcms.controllers.auth.SignupController;
import com.fcms.database.*;
import com.fcms.controllers.policeOfficer.PoliceDashboardController;
import com.fcms.controllers.forensicExpert.ExpertDashboardController;
import com.fcms.models.UserSession;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize DB
        SQLiteDatabase.initializeDatabase();

        // Create SceneManager
        SceneManager sceneManager = new SceneManager(primaryStage);

        // Prepare loader for dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/mainLayout.fxml"));

        // Controller factory: inject SceneManager into SidebarController when it's constructed
        loader.setControllerFactory(type -> {
            try {
                if (type == SidebarController.class) {
                    SidebarController sc = new SidebarController();
                    sc.setSceneManager(sceneManager);
                    return sc;
                }
                // default construction for other controllers (e.g., PoliceDashboardController)
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = loader.load();

        // Optional: get dashboard controller if you need it for other tasks
        MainLayoutController dashboardController = loader.getController();

        // Create scene and attach CSS
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());

        // Configure stage
        primaryStage.setTitle("CaseVault - Police Officer Dashboard");
        //primaryStage.setResizable(true);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
        this.primaryStage = primaryStage;
        SQLiteDatabase.initializeDatabase();
        //TestDataInserter.insertTestData();

        showLoginScreen();
    }

    private void showDashboard() {
        UserSession session = UserSession.getInstance();
        String role = session.getRole();

        System.out.println("Navigating to dashboard for role: " + role);

        try {
            String fxmlFile = getDashboardFXML(role);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Set up the dashboard controller
            setupDashboardController(loader.getController());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

            primaryStage.setTitle("CaseVault - " + role + " Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.setMaximized(true); // Dashboard can be maximized
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load dashboard for role: " + role);
        }
    }

    private String getDashboardFXML(String role) {
        switch (role) {
            case "Police Officer":
                return "/fxml/policeOfficer/policeDashboard.fxml";
            case "Forensic Expert":
                return "/fxml/forensicExpert/expertDashboard.fxml";
            case "Court Official":
                return "/fxml/courtOfficial/courtDashboard.fxml"; // Adjust path as needed
            default:
                return "/fxml/policeOfficer/policeDashboard.fxml"; // fallback
        }
    }

    private void setupDashboardController(Object controller) {
        // You can set up logout handlers or other common dashboard setup here
        // For now, we'll just log the controller type
        System.out.println("Dashboard controller type: " + controller.getClass().getSimpleName());
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();

            // Set up navigation callbacks
            loginController.setOnNavigateToSignup(this::showSignupScreen);
            loginController.setOnNavigateToForgotPassword(this::showForgotPasswordScreen);
            loginController.setOnLoginSuccess(this::showDashboard);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

            primaryStage.setTitle("CaseVault - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(600);
            primaryStage.setMaximized(false); // Login screen should not be maximized
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
            primaryStage.setMinWidth(420);  // Reduced from 450
            primaryStage.setMinHeight(650); // Reduced from 700
            primaryStage.setMaximized(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showForgotPasswordScreen() {
        System.out.println("Navigate to forgot password screen");
        // TODO: Implement forgot password screen
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