package com.fcms.controllers.auth;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.Map;
import com.fcms.services.SignupService;
import com.fcms.services.SignupService.SignupResult;

public class SignupController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button signupBtn;
    @FXML private Button backToLoginBtn;

    @FXML private VBox roleSpecificContainer;

    @FXML private HBox passwordStrengthContainer;
    @FXML private Rectangle strengthBar1;
    @FXML private Rectangle strengthBar2;
    @FXML private Rectangle strengthBar3;
    @FXML private Label strengthLabel;
    @FXML private Label passwordMatchIcon;

    private Map<String, TextField> roleFields = new HashMap<>();
    private Runnable onNavigateToLogin;
    private Runnable onSubmitSuccess;
    private final SignupService authService;

    public SignupController() {
        this.authService = new SignupService();
    }

    public void setOnNavigateToLogin(Runnable onNavigateToLogin) {
        this.onNavigateToLogin = onNavigateToLogin;
    }

    public void setOnSubmitSuccess(Runnable onSubmitSuccess) {
        this.onSubmitSuccess = onSubmitSuccess;
    }

    @FXML
    public void initialize() {
        System.out.println("SignupController initialized");

        // Set up role options
        roleComboBox.getItems().addAll("Police Officer", "Forensic Expert", "Court Official");

        // Set up role selection listener
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleRoleSelection(newValue);
        });

        // Initialize strength bars
        resetStrengthBars();

        // Set up password strength listener
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordStrength(newValue);
            updatePasswordMatch();
        });

        // Set up confirm password listener
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePasswordMatch();
        });

        // Set up button actions
        if (signupBtn != null) {
            signupBtn.setOnAction(e -> handleSignup());
        }
        if (backToLoginBtn != null) {
            backToLoginBtn.setOnAction(e -> handleBackToLogin());
        }
    }

    private void handleRoleSelection(String role) {
        // Clear previous role-specific fields
        roleSpecificContainer.getChildren().clear();
        roleFields.clear();

        if (role != null) {
            roleSpecificContainer.setVisible(true);

            switch (role) {
                case "Police Officer":
                    createPoliceFields();
                    break;
                case "Forensic Expert":
                    createForensicFields();
                    break;
                case "Court Official":
                    createCourtFields();
                    break;
            }
        } else {
            roleSpecificContainer.setVisible(false);
        }
    }

    private void createPoliceFields() {
        // Rank Field
        Label rankLabel = new Label("Rank");
        rankLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 13; -fx-font-weight: bold;");

        TextField rankField = new TextField();
        rankField.setPromptText("Enter your rank *");
        rankField.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-font-size: 14; -fx-padding: 10 12;");
        roleFields.put("rank", rankField);

        // Department Field
        Label deptLabel = new Label("Department *");
        deptLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 13; -fx-font-weight: bold;");

        TextField deptField = new TextField();
        deptField.setPromptText("Enter your department");
        deptField.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-font-size: 14; -fx-padding: 10 12;");
        roleFields.put("department", deptField);

        roleSpecificContainer.getChildren().addAll(rankLabel, rankField, deptLabel, deptField);
    }

    private void createForensicFields() {
        // Lab Name Field
        Label labLabel = new Label("Lab Name *");
        labLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 13; -fx-font-weight: bold;");

        TextField labField = new TextField();
        labField.setPromptText("Enter your lab name");
        labField.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-font-size: 14; -fx-padding: 10 12;");
        roleFields.put("labName", labField);

        roleSpecificContainer.getChildren().addAll(labLabel, labField);
    }

    private void createCourtFields() {
        // Court Name Field
        Label courtLabel = new Label("Court Name *");
        courtLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 13; -fx-font-weight: bold;");

        TextField courtField = new TextField();
        courtField.setPromptText("Enter court name");
        courtField.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-font-size: 14; -fx-padding: 10 12;");
        roleFields.put("courtName", courtField);

        // Designation Field
        Label designationLabel = new Label("Designation *");
        designationLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 13; -fx-font-weight: bold;");

        TextField designationField = new TextField();
        designationField.setPromptText("Enter your designation");
        designationField.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-font-size: 14; -fx-padding: 10 12;");
        roleFields.put("designation", designationField);

        roleSpecificContainer.getChildren().addAll(courtLabel, courtField, designationLabel, designationField);
    }

    private void resetStrengthBars() {
        strengthBar1.setFill(Color.web("#d1d5db"));
        strengthBar2.setFill(Color.web("#d1d5db"));
        strengthBar3.setFill(Color.web("#d1d5db"));
        passwordStrengthContainer.setVisible(false);
    }

    private void updatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            passwordStrengthContainer.setVisible(false);
            resetStrengthBars();
            return;
        }

        passwordStrengthContainer.setVisible(true);

        int strength = calculatePasswordStrength(password);

        switch (strength) {
            case 0:
                setStrengthBars(Color.RED, Color.web("#d1d5db"), Color.web("#d1d5db"), "Weak");
                strengthLabel.setStyle("-fx-text-fill: #dc2626;");
                break;
            case 1:
                setStrengthBars(Color.ORANGE, Color.ORANGE, Color.web("#d1d5db"), "Medium");
                strengthLabel.setStyle("-fx-text-fill: #ea580c;");
                break;
            case 2:
                setStrengthBars(Color.GREEN, Color.GREEN, Color.GREEN, "Strong");
                strengthLabel.setStyle("-fx-text-fill: #16a34a;");
                break;
        }
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() >= 8) strength++;

        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = !password.matches("[A-Za-z0-9 ]*");

        int complexityPoints = 0;
        if (hasUpper) complexityPoints++;
        if (hasLower) complexityPoints++;
        if (hasDigit) complexityPoints++;
        if (hasSpecial) complexityPoints++;

        if (complexityPoints >= 3) strength++;
        if (password.length() >= 12 && complexityPoints >= 3) strength++;

        return Math.min(strength, 2);
    }

    private void setStrengthBars(Color bar1Color, Color bar2Color, Color bar3Color, String strengthText) {
        strengthBar1.setFill(bar1Color);
        strengthBar2.setFill(bar2Color);
        strengthBar3.setFill(bar3Color);
        strengthLabel.setText(strengthText);
    }

    private void updatePasswordMatch() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            passwordMatchIcon.setVisible(false);
            return;
        }

        passwordMatchIcon.setVisible(true);
        if (password.equals(confirmPassword)) {
            passwordMatchIcon.setText("✓");
            passwordMatchIcon.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 16; -fx-font-weight: bold;");
        } else {
            passwordMatchIcon.setText("✗");
            passwordMatchIcon.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 16; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleSignup() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = userField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        // UI-specific validation
        if (!validateForm(fullName, email, username, password, confirmPassword, role)) {
            return;
        }

        // Collect role-specific data
        Map<String, String> roleSpecificData = collectRoleSpecificData(role);

        // Call service layer
        SignupResult result = authService.registerUser(fullName, email, username, password, role, roleSpecificData);

        if (result.isSuccess()) {
            showSuccessAlert(result.getMessage());
            clearForm();
            if (onSubmitSuccess != null) {
                onSubmitSuccess.run();
            }
        } else {
            showAlert("Error", result.getMessage());
        }
    }

    private boolean validateForm(String fullName, String email, String username,
                                 String password, String confirmPassword, String role) {
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || role == null) {
            showAlert("Error", "Please fill in all required fields.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return false;
        }

        return true;
    }

    private Map<String, String> collectRoleSpecificData(String role) {
        Map<String, String> data = new HashMap<>();

        switch (role) {
            case "Police Officer":
                data.put("rank", roleFields.get("rank").getText().trim());
                data.put("department", roleFields.get("department").getText().trim());
                break;
            case "Forensic Expert":
                data.put("labName", roleFields.get("labName").getText().trim());
                break;
            case "Court Official":
                data.put("courtName", roleFields.get("courtName").getText().trim());
                data.put("designation", roleFields.get("designation").getText().trim());
                break;
        }

        return data;
    }

    @FXML
    private void handleBackToLogin() {
        System.out.println("Back to login clicked");
        if (onNavigateToLogin != null) {
            onNavigateToLogin.run();
        } else {
            System.err.println("onNavigateToLogin is null!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearForm() {
        fullNameField.clear();
        emailField.clear();
        userField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.setValue(null);

        roleFields.values().forEach(TextField::clear);
        roleSpecificContainer.getChildren().clear();
        roleSpecificContainer.setVisible(false);

        resetStrengthBars();
        passwordMatchIcon.setVisible(false);
    }

    public String getFullName() {
        return fullNameField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getRole() {
        return roleComboBox.getValue();
    }

    public Map<String, TextField> getRoleFields() {
        return new HashMap<>(roleFields);
    }
}