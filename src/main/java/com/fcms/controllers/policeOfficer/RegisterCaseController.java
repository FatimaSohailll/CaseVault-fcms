package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.models.users.PoliceOfficer;
import com.fcms.repositories.UserRepository;
import com.fcms.services.CaseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;

import java.time.LocalDate;
import java.util.List;
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
    @FXML private ComboBox<PoliceOfficer> officerDropdown; // changed to PoliceOfficer

    private final CaseService caseService = new CaseService();
    @FXML private Label priorityWarning;

    @FXML
    public void initialize() {
        System.out.println("RegisterCaseController.initialize() called");

        // Populate static dropdowns
        caseTypeDropdown.getItems().addAll(
                "Robbery", "Burglary", "Fraud", "Assault", "Vehicle Theft", "Cybercrime", "Domestic Violence"
        );

        priorityDropdown.getItems().addAll("High", "Medium", "Low");
        priorityDropdown.setValue("Low"); // default selection

        // Load officers from DB via repository
        try {
            List<PoliceOfficer> officers = UserRepository.getAllPoliceOfficers();
            officerDropdown.getItems().setAll(officers);

            // Friendly display: "Name • ID"
            officerDropdown.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(PoliceOfficer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName() + " • " + item.getUserID());
                }
            });
            officerDropdown.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(PoliceOfficer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getName() + " • " + item.getUserID());
                }
            });

            if (officers.isEmpty()) {
                officerDropdown.setPromptText("No officers available");
            }
        } catch (Exception e) {
            e.printStackTrace();
            validationMessage.setText("Failed to load officers: " + e.getMessage());
            validationMessage.setVisible(true);
        }

        validationMessage.setVisible(false);
    }

    @FXML
    private void handleSaveCase() {
        hideWarnings();

        String title = caseTitleField.getText().trim();
        String type = caseTypeDropdown.getValue();
        String description = descriptionField.getText().trim();
        LocalDate date = datePicker.getValue();

        // Validate via service (keeps your existing validation logic)
        String validationError = caseService.validateCaseInput(title, type, description, date);
        if (validationError != null) {
            showValidationError(validationError);
            return;
        }

        // Generate a unique case ID
        String caseId = "CS-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 4);

        // Get selected officer id (nullable)
        PoliceOfficer selectedOfficer = officerDropdown.getValue();
        String assignedOfficerId = selectedOfficer == null ? null : selectedOfficer.getUserID();

        // Normalize priority to match DB CHECK (your schema expects lowercase)
        String priority = priorityDropdown.getValue() == null ? "low" : priorityDropdown.getValue().toLowerCase();

        // Build Case object (uses your existing Case model constructor)
        Case newCase = new Case(
                caseId,
                title,
                type,
                assignedOfficerId,
                locationField.getText().trim(),
                date,
                "open",
                description,
                priority,
                timeField.getText().trim()
        );

        newCase.setDescription(description);
        newCase.setPriority(priority);

        // Persist and handle errors
        try {
            caseService.registerCase(newCase);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Case Registered");
            alert.setHeaderText(null);
            alert.setContentText("Case " + caseId + " has been successfully registered.");
            alert.showAndWait();

            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Registration Failed");
            err.setHeaderText("Could not register case");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
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
        timeField.clear();
        locationField.clear();
        priorityDropdown.setValue("Low");
        officerDropdown.setValue(null);
        hideWarnings();
        validationMessage.setVisible(false);
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
