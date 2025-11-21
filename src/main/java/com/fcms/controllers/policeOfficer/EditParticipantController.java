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

public class EditParticipantController implements Initializable {

    @FXML private VBox validationError;
    @FXML private Label errorMessageLabel;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField contactField;
    @FXML private ComboBox<String> idTypeComboBox;
    @FXML private TextField idNumberField;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private ManageParticipantsController mainController;
    private Participant participant;
    private ParticipantService participantService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.participantService = new ParticipantService();
        setupComboBoxes();
    }

    private void setupComboBoxes() {
        ObservableList<String> roles = FXCollections.observableArrayList(
                "Suspect", "Victim", "Witness"
        );
        roleComboBox.setItems(roles);

        ObservableList<String> idTypes = FXCollections.observableArrayList(
                "Driver License", "Passport", "State ID", "National ID", "Other"
        );
        idTypeComboBox.setItems(idTypes);
    }

    public void setMainController(ManageParticipantsController mainController) {
        this.mainController = mainController;
    }

    public void setParticipantData(Participant participant) {
        this.participant = participant;
        populateFormData();
    }

    private void populateFormData() {
        nameField.setText(participant.getName());
        roleComboBox.setValue(participant.getRole());
        contactField.setText(participant.getContact());
        idTypeComboBox.setValue(participant.getIdType());
        idNumberField.setText(participant.getIdNumber());
    }

    @FXML
    private void handleSubmit() {
        if (!validateForm()) {
            return;
        }

        try {
            updateParticipantData();
            participantService.updateParticipant(participant);

            if (mainController != null) {
                mainController.updateParticipant(participant);
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
        if (nameField.getText().isEmpty() ||
                roleComboBox.getValue() == null ||
                contactField.getText().isEmpty() ||
                idTypeComboBox.getValue() == null ||
                idNumberField.getText().isEmpty()) {

            showValidationError("All fields are required");
            return false;
        }

        clearValidationError();
        return true;
    }

    private void updateParticipantData() {
        participant.setName(nameField.getText());
        participant.setRole(roleComboBox.getValue());
        participant.setContact(contactField.getText());
        participant.setIdType(idTypeComboBox.getValue());
        participant.setIdNumber(idNumberField.getText());
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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}