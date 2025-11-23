package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.services.CaseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.UUID;

public class RegisterCaseController {

    @FXML private TextField caseTitleField;
    @FXML private ComboBox<String> caseTypeDropdown;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private Label validationMessage;
    @FXML private ComboBox<String> priorityDropdown;

    @FXML private Label titleWarning, typeWarning, descWarning, dateWarning;
    @FXML private TextField timeField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> officerDropdown;

    private final CaseService caseService = new CaseService();
    @FXML private Label priorityWarning;

    @FXML
    public void initialize() {
        caseTypeDropdown.getItems().addAll(
                "Robbery", "Burglary", "Fraud", "Assault", "Vehicle Theft", "Cybercrime", "Domestic Violence"
        );

        priorityDropdown.getItems().addAll("High", "Medium", "Low");
        priorityDropdown.setValue("Low"); // default selection
        officerDropdown.getItems().addAll("PO-001"); // Later: fetch from DB

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

        // Generate a unique case ID (UUID or custom format)
        String caseId = "CS-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 4);

        // Create and save case
        Case newCase = new Case(
                caseId,
                title,
                type,
                officerDropdown.getValue(),           // dynamically selected officer
                locationField.getText().trim(),       // user-entered location
                date,
                "open",                               // normalized status
                descriptionField.getText().trim(),    // case description
                priorityDropdown.getValue(),          // selected priority
                timeField.getText().trim()            // time of incident
        );


        newCase.setDescription(descriptionField.getText().trim());
        newCase.setPriority(priorityDropdown.getValue());

        caseService.registerCase(newCase);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Case Registered");
        alert.setHeaderText(null);
        alert.setContentText("Case " + caseId + " has been successfully registered.");
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
        priorityWarning.setVisible(false);
    }


    private void showValidationError(String error) {
        validationMessage.setText(error);
        validationMessage.setVisible(true);

        switch (error) {
            case "Title is required"      -> titleWarning.setVisible(true);
            case "Type is required"       -> typeWarning.setVisible(true);
            case "Description is required"-> descWarning.setVisible(true);
            case "Date is required"       -> dateWarning.setVisible(true);
            case "Priority is required"   -> priorityWarning.setVisible(true);
        }
    }

}
