package com.fcms.controllers.policeOfficer;

import com.fcms.services.ParticipantService;
import com.fcms.services.BusinessException;
import com.fcms.models.Participant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;

public class AddParticipantController implements Initializable {

    @FXML private VBox validationError;
    @FXML private Label errorMessageLabel;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField contactField;
    @FXML private ComboBox<String> idTypeComboBox;
    @FXML private TextField idNumberField;
    @FXML private TextField caseIdField;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private ManageParticipantsController mainController;
    private ParticipantService participantService;
    private String currentCaseId = "CS00001";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupFormValidation();
    }

    private void setupComboBoxes() {
        // Only allow roles that match database constraints
        ObservableList<String> roles = FXCollections.observableArrayList(
                "Suspect", "Victim" // Removed "Witness" as it's not in database CHECK constraint
        );
        roleComboBox.setItems(roles);

        ObservableList<String> idTypes = FXCollections.observableArrayList(
                "Driver License", "Passport", "State ID", "National ID", "Other"
        );
        idTypeComboBox.setItems(idTypes);
    }

    private void setupFormValidation() {
        // Real-time form validation
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        contactField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        idTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        idNumberField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        caseIdField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    public void setMainController(ManageParticipantsController mainController) {
        this.mainController = mainController;
    }

    public void setCurrentCaseId(String currentCaseId) {
        this.currentCaseId = currentCaseId;
        // Initialize service with officer ID
        this.participantService = new ParticipantService(currentCaseId);
    }

    @FXML
    private void handleSubmit() {
        if (!validateForm()) {
            return;
        }

        try {
            Participant newParticipant = createParticipant();
            String caseId = caseIdField.getText().trim();

            if (caseId.isEmpty()) {
                // Add participant without case linking (will not be visible until linked to case)
                participantService.addParticipant(newParticipant);
                showSuccessAlert("Participant added successfully! Note: Participant will only appear when linked to a case.");
            } else {
                // Add participant and link to case
                participantService.addParticipantToCase(newParticipant, caseId);
                showSuccessAlert("Participant added successfully to case " + caseId + "!");
            }

            if (mainController != null) {
                mainController.addNewParticipant(newParticipant);
            }

            closeWindow();
        } catch (BusinessException e) {
            showErrorAlert(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();

        // Required field validation
        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("• Name is required\n");
        }

        if (roleComboBox.getValue() == null) {
            errorMessage.append("• Role is required\n");
        }

        if (contactField.getText().trim().isEmpty()) {
            errorMessage.append("• Contact information is required\n");
        }

        if (idTypeComboBox.getValue() == null) {
            errorMessage.append("• ID Type is required\n");
        }

        if (idNumberField.getText().trim().isEmpty()) {
            errorMessage.append("• ID Number is required\n");
        }

        // Optional case ID validation
        String caseId = caseIdField.getText().trim();
        if (!caseId.isEmpty() && !caseId.matches("CS\\d{5}")) {
            errorMessage.append("• Case ID should be in format CS00001\n");
        }

        // Name validation
        String name = nameField.getText().trim();
        if (!name.isEmpty() && !name.matches("^[a-zA-Z\\s]+$")) {
            errorMessage.append("• Name should contain only letters and spaces\n");
        }

        // Contact validation
        String contact = contactField.getText().trim();
        if (!contact.isEmpty() && !contact.matches("^[\\d\\s\\-\\(\\)\\+]+$")) {
            errorMessage.append("• Contact should contain only numbers, spaces, hyphens, and parentheses\n");
        }

        if (errorMessage.length() > 0) {
            showValidationError(errorMessage.toString());
            submitButton.setDisable(true);
            return false;
        }

        clearValidationError();
        submitButton.setDisable(false);
        return true;
    }

    private Participant createParticipant() {
        return new Participant(
                null, // ID will be generated by service
                nameField.getText().trim(),
                roleComboBox.getValue(),
                contactField.getText().trim(),
                idTypeComboBox.getValue(),
                idNumberField.getText().trim()
        );
    }

    private void showValidationError(String message) {
        errorMessageLabel.setText(message);
        validationError.setVisible(true);
    }

    private void clearValidationError() {
        validationError.setVisible(false);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot Add Participant");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Participant Added");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // Helper method to pre-fill case ID if known
    public void setCaseId(String caseId) {
        if (caseId != null && !caseId.trim().isEmpty()) {
            caseIdField.setText(caseId);
            caseIdField.setDisable(true); // Disable editing if pre-filled
        }
    }
}