package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.services.CaseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class RegisterCaseController {

    @FXML private TextField caseTitleField;
    @FXML private ComboBox<String> caseTypeDropdown;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private Label validationMessage;

    @FXML private Label titleWarning, typeWarning, descWarning, dateWarning;

    private final CaseService caseService = new CaseService();

    @FXML
    public void initialize() {
        caseTypeDropdown.getItems().addAll("Robbery", "Burglary", "Fraud", "Assault", "Vehicle Theft");
        validationMessage.setVisible(false);
    }

    @FXML
    private void handleSaveCase() {
        hideWarnings();

        String title = caseTitleField.getText().trim();
        String type = caseTypeDropdown.getValue();
        String description = descriptionField.getText().trim();
        LocalDate date = datePicker.getValue();

        // Validate via service
        String validationError = caseService.validateCaseInput(title, type, description, date);
        if (validationError != null) {
            showValidationError(validationError);
            return;
        }

        // Create and save case
        Case newCase = new Case("AUTO-ID", title, type, "Officer Placeholder", "Unknown", date, "Open");
        caseService.registerCase(newCase);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Case Registered");
        alert.setHeaderText(null);
        alert.setContentText("Case has been successfully registered.");
        alert.showAndWait();

        clearForm();
    }

    @FXML
    private void handleCancel() {
        clearForm();
        validationMessage.setVisible(false);
    }

    private void clearForm() {
        caseTitleField.clear();
        caseTypeDropdown.setValue(null);
        descriptionField.clear();
        datePicker.setValue(null);
    }

    private void hideWarnings() {
        titleWarning.setVisible(false);
        typeWarning.setVisible(false);
        descWarning.setVisible(false);
        dateWarning.setVisible(false);
    }

    private void showValidationError(String error) {
        validationMessage.setText(error);
        validationMessage.setVisible(true);

        switch (error) {
            case "Title is required" -> titleWarning.setVisible(true);
            case "Type is required" -> typeWarning.setVisible(true);
            case "Description is required" -> descWarning.setVisible(true);
            case "Date is required" -> dateWarning.setVisible(true);
        }
    }
}
