package com.fcms.controllers.auth;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.fcms.services.LoginService;
import com.fcms.services.LoginService.LoginResult;
import com.fcms.models.UserSession;

public class LoginController {

    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Button signupBtn;

    private Runnable onNavigateToSignup;
    private Runnable onLoginSuccess;
    private final LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    public void setOnNavigateToSignup(Runnable onNavigateToSignup) {
        this.onNavigateToSignup = onNavigateToSignup;
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    public void initialize() {
        System.out.println("LoginController initialized");

        // Set up button actions
        if (signupBtn != null) {
            signupBtn.setOnAction(e -> handleSignup());
        }
        if (loginBtn != null) {
            loginBtn.setOnAction(e -> handleLogin());
        }

        // Optional: Add Enter key support for login
        setupEnterKeySupport();
    }

    private void setupEnterKeySupport() {
        // Login on Enter key press in password field
        passwordField.setOnAction(e -> handleLogin());

        // Also support Enter in username field
        userField.setOnAction(e -> {
            if (!passwordField.getText().isEmpty()) {
                handleLogin();
            } else {
                passwordField.requestFocus();
            }
        });
    }

    @FXML
    private void handleLogin() {
        String userID = userField.getText().trim();
        String password = passwordField.getText();

        System.out.println("Login attempt for user: " + userID);

        // Validate input
        if (userID.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both Username and Password");
            return;
        }

        // Authenticate using service layer
        LoginResult result = loginService.authenticate(userID, password);

        if (result.isSuccess()) {
            System.out.println("Login successful for user: " + userID + " with role: " + result.getRole());

            // Store user session information (you might want to create a SessionManager class)
            storeUserSession(result.getUserID(), result.getRole());

            // Navigate to main dashboard
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            System.out.println("Login failed: " + result.getMessage());
            showAlert("Login Failed", result.getMessage());

            // Clear password field on failed attempt
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    private void storeUserSession(String userID, String role) {
            UserSession session = UserSession.getInstance();
            session.setCurrentUser(userID, role);
            System.out.println("User session created - ID: " + userID + ", Role: " + role);
    }

    @FXML
    private void handleSignup() {
        System.out.println("Signup button clicked");
        if (onNavigateToSignup != null) {
            onNavigateToSignup.run();
        } else {
            System.err.println("onNavigateToSignup is null!");
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to clear form
    public void clearForm() {
        userField.clear();
        passwordField.clear();
    }

    // Method to set focus to user ID field (useful when navigating back to login)
    public void focusUserIdField() {
        userField.requestFocus();
    }
}