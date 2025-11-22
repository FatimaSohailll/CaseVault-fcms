package com.fcms.controllers.policeOfficer;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterCaseController {

    @FXML private TextField caseTitleField;
    @FXML private ComboBox<String> caseTypeDropdown;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private Label validationMessage;

    @FXML
    public void initialize() {
        caseTypeDropdown.getItems().addAll("Robbery", "Burglary", "Fraud", "Assault", "Vehicle Theft");
        validationMessage.setVisible(false);
    }

    @FXML private Label titleWarning, typeWarning, descWarning, dateWarning;

    @FXML
    private void handleSaveCase() {
        boolean valid = true;

        titleWarning.setVisible(false);
        typeWarning.setVisible(false);
        descWarning.setVisible(false);
        dateWarning.setVisible(false);

        if (caseTitleField.getText().trim().isEmpty()) {
            titleWarning.setVisible(true);
            valid = false;
        }
        if (caseTypeDropdown.getValue() == null) {
            typeWarning.setVisible(true);
            valid = false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            descWarning.setVisible(true);
            valid = false;
        }
        if (datePicker.getValue() == null) {
            dateWarning.setVisible(true);
            valid = false;
        }

        if (valid) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Case Registered");
            alert.setHeaderText(null);
            alert.setContentText("Case has been successfully registered.");
            alert.showAndWait();
            clearForm();
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
    }
}
