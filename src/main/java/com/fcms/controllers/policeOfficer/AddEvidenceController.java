package com.fcms.controllers.policeOfficer;

import com.fcms.services.EvidenceService;
import com.fcms.services.BusinessException;
import com.fcms.models.Evidence;
import com.fcms.models.Case;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.layout.*;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddEvidenceController implements Initializable {

    @FXML private Label selectedCaseLabel;
    @FXML private ComboBox<String> evidenceTypeCombo;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker collectionDatePicker;
    @FXML private TextField collectionTimeField;
    @FXML private TextField locationField;
    @FXML private Button fileUploadButton;
    @FXML private Label fileNameLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ListView<Evidence> existingEvidenceList;

    private ObservableList<Evidence> existingEvidence = FXCollections.observableArrayList();
    private Case selectedCase;
    private File selectedFile;
    private EvidenceService evidenceService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize with a default case ID for now
        this.evidenceService = new EvidenceService("CS00001"); // Default case ID
        initializeExistingEvidence();
        initializeComboBoxes();
        setupEventHandlers();
        setupFormDefaults();
        populateCaseDetails();
    }

    public void setSelectedCase(Case selectedCase) {
        this.selectedCase = selectedCase;
        // Update EvidenceService with the actual case ID if available
        if (selectedCase != null) {
            this.evidenceService = new EvidenceService(selectedCase.getId());
        }
        populateCaseDetails();
        // Refresh existing evidence for this case
        initializeExistingEvidence();
    }

    private void initializeExistingEvidence() {
        try {
            // Load existing evidence through service
            existingEvidence.setAll(evidenceService.getEvidenceByCase("CS00001"));
            existingEvidenceList.setItems(existingEvidence);
            existingEvidenceList.setCellFactory(param -> new EvidenceListCell());
        } catch (Exception e) {
            showAlert("Error", "Failed to load existing evidence: " + e.getMessage());
        }
    }

    private void initializeComboBoxes() {
        evidenceTypeCombo.setItems(FXCollections.observableArrayList(
                "Physical Evidence", "Digital Evidence", "Documentary Evidence",
                "Testimonial Evidence", "Biological Evidence"
        ));
    }

    private void setupEventHandlers() {
        fileUploadButton.setOnAction(e -> handleFileUpload());
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }

    private void setupFormDefaults() {
        collectionDatePicker.setValue(LocalDate.now());
        collectionTimeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void populateCaseDetails() {
        if (selectedCase != null) {
            selectedCaseLabel.setText("Selected Case: " + selectedCase.getId() + " • " + selectedCase.getTitle());
        } else {
            selectedCaseLabel.setText("Selected Case: CASE-001 (Default)");
        }
    }

    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Evidence File");

        FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter("All Files", "*.*");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        FileChooser.ExtensionFilter videoFilter = new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov");

        fileChooser.getExtensionFilters().addAll(pdfFilter, imageFilter, videoFilter, allFiles);

        selectedFile = fileChooser.showOpenDialog(fileUploadButton.getScene().getWindow());

        if (selectedFile != null) {
            fileNameLabel.setText("Selected: " + selectedFile.getName());
            fileNameLabel.setStyle("-fx-text-fill: #001440;");
        }
    }

    private void handleSave() {
        if (validateForm()) {
            try {
                Evidence newEvidence = createEvidence();

                if (selectedFile != null) {
                    evidenceService.addEvidenceWithFile(newEvidence, selectedFile);
                } else {
                    evidenceService.addEvidence(newEvidence);
                }

                showSuccessAlert();
                resetForm();
                initializeExistingEvidence(); // Refresh list
            } catch (BusinessException e) {
                showAlert("Validation Error", e.getMessage());
            }
        }
    }

    private void handleCancel() {
        resetForm();
    }

    private boolean validateForm() {
        // UI validation only
        if (evidenceTypeCombo.getValue() == null || evidenceTypeCombo.getValue().isEmpty()) {
            showAlert("Validation Error", "Please select an evidence type.");
            return false;
        }

        if (descriptionArea.getText() == null || descriptionArea.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please provide a description.");
            descriptionArea.requestFocus();
            return false;
        }

        if (collectionDatePicker.getValue() == null) {
            showAlert("Validation Error", "Please select a collection date.");
            return false;
        }

        if (collectionTimeField.getText() == null || collectionTimeField.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter a collection time.");
            collectionTimeField.requestFocus();
            return false;
        }

        if (locationField.getText() == null || locationField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter a collection location.");
            locationField.requestFocus();
            return false;
        }

        return true;
    }

    private Evidence createEvidence() {
        Evidence evidence = new Evidence(
                null, // ID will be generated by service
                descriptionArea.getText(),
                evidenceTypeCombo.getValue()
        );

        // Combine date and time for collectionDateTime
        String dateTime = collectionDatePicker.getValue().toString() + " " + collectionTimeField.getText();
        evidence.setCollectionDateTime(dateTime);
        evidence.setLocation(locationField.getText());

        // Set case ID - use selected case if available, otherwise use default
        if (selectedCase != null) {
            evidence.setCaseId(selectedCase.getId());
        } else {
            evidence.setCaseId("CS00001"); // Default case ID
        }

        return evidence;
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Evidence successfully logged!");
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetForm() {
        evidenceTypeCombo.setValue(null);
        descriptionArea.clear();
        collectionDatePicker.setValue(LocalDate.now());
        collectionTimeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        locationField.clear();
        selectedFile = null;
        fileNameLabel.setText("");
    }

    // Custom ListCell for displaying evidence items (UI only)
    private class EvidenceListCell extends ListCell<Evidence> {
        @Override
        protected void updateItem(Evidence evidence, boolean empty) {
            super.updateItem(evidence, empty);

            if (empty || evidence == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox container = new VBox();
                container.setSpacing(4);

                HBox header = new HBox();
                header.setSpacing(8);
                header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                Label idLabel = new Label(evidence.getId());
                idLabel.setStyle("-fx-text-fill: #001440; -fx-font-size: 13px; -fx-font-weight: bold;");

                Label typeLabel = new Label(evidence.getType());
                typeLabel.setStyle("-fx-background-color: #f3f4f6; -fx-padding: 2 6; -fx-font-size: 11px; -fx-text-fill: #6b7280;");

                HBox.setHgrow(header, javafx.scene.layout.Priority.ALWAYS);
                header.getChildren().addAll(idLabel, typeLabel);

                Label descriptionLabel = new Label(evidence.getDescription());
                descriptionLabel.setStyle("-fx-text-fill: #171617; -fx-font-size: 13px;");
                descriptionLabel.setWrapText(true);

                VBox details = new VBox();
                details.setSpacing(2);

                if (evidence.getCollectionDateTime() != null) {
                    Label dateLabel = new Label("Date: " + evidence.getCollectionDateTime());
                    dateLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
                    details.getChildren().add(dateLabel);
                }

                if (evidence.getLocation() != null) {
                    Label locationLabel = new Label("Location: " + evidence.getLocation());
                    locationLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px;");
                    details.getChildren().add(locationLabel);
                }

                if (evidence.getFileName() != null) {
                    Label fileLabel = new Label("📎 " + evidence.getFileName());
                    fileLabel.setStyle("-fx-text-fill: #001440; -fx-font-size: 11px;");
                    details.getChildren().add(fileLabel);
                }

                container.getChildren().addAll(header, descriptionLabel, details);
                container.setStyle("-fx-padding: 12; -fx-background-color: white; -fx-border-color: #e5e5e5; -fx-border-radius: 6;");

                setGraphic(container);
                setText(null);
            }
        }
    }
}