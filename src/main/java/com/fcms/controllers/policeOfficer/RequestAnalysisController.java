package com.fcms.controllers.policeOfficer;

import com.fcms.services.CaseService;
import com.fcms.models.Case;
import com.fcms.models.UserSession;
import com.fcms.services.ForensicRequestService;
import com.fcms.services.EvidenceService;
import com.fcms.services.ForensicExpertService;
import com.fcms.services.BusinessException;
import com.fcms.models.ForensicRequest;
import com.fcms.models.Evidence;
import com.fcms.models.ForensicExpert;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

public class RequestAnalysisController implements Initializable {

    @FXML private ComboBox<String> analysisTypeCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextArea additionalNotesArea;
    @FXML private Button submitButton;
    @FXML private VBox successAlert;
    @FXML private VBox noCaseSelected;
    @FXML private VBox caseDetails;
    @FXML private Label caseIdLabel;
    @FXML private Label caseTitleLabel;
    @FXML private Label caseTypeLabel;
    @FXML private Label assignedOfficerLabel;
    @FXML private Label locationLabel;
    @FXML private VBox evidenceCheckboxContainer;
    @FXML private VBox expertsContainer;

    private ForensicRequestService requestService;
    private EvidenceService evidenceService;
    private ForensicExpertService expertService;
    private String currentCaseId;
    private String currentOfficerId;
    private List<Evidence> availableEvidence = new ArrayList<>();
    private List<ForensicExpert> availableExperts = new ArrayList<>();
    private List<CheckBox> evidenceCheckboxes = new ArrayList<>();
    private ToggleGroup expertToggleGroup = new ToggleGroup();
    private UserSession userSession;
    private CaseService caseService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userSession = UserSession.getInstance();

        // Validate that user is logged in and is a forensic expert
        if (!userSession.isLoggedIn() || !userSession.isPoliceOfficer()) {
            showAlert("Access Denied", "You must be logged in as a Police Officer to access this dashboard.");
            return;
        }

        String currentUserId = userSession.getUserID();
        System.out.println("DEBUG: Initializing Police Dashboard for user: " + currentUserId);

        this.currentCaseId = "CS00001"; // Dummy case ID for testing
        this.currentOfficerId= currentUserId;
        this.requestService = new ForensicRequestService();
        this.expertService = new ForensicExpertService();
        this.caseService = new CaseService(currentCaseId);

        this.evidenceService = new EvidenceService(currentCaseId);

        setupComboBoxes();
        setupEventHandlers();
        loadAvailableExperts();
        loadCaseFromDatabase();
        loadEvidenceFromDatabase(); // Load evidence using dummy case ID
    }

    private void loadAvailableExperts() {
        try {
            availableExperts = expertService.getAllExperts();
            createExpertSelectionCards();

            System.out.println("Loaded " + availableExperts.size() + " forensic experts");

        } catch (Exception e) {
            System.err.println("Error loading forensic experts: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Failed to load forensic experts: " + e.getMessage());
        }
    }

    private void loadCaseFromDatabase() {
        if (currentCaseId == null || currentCaseId.trim().isEmpty()) {
            System.out.println("No case selected, cannot load case details");
            return;
        }

        try {
            // Fetch case details from database
            Case caseDetails = caseService.getCaseById(currentCaseId);

            if (caseDetails != null) {
                // Update UI with case details from database
                setCaseData(
                        caseDetails.getId(),
                        caseDetails.getTitle(),
                        caseDetails.getType(),
                        caseDetails.getAssignedOfficer(),
                        caseDetails.getLocation()
                );
            } else {
                System.err.println("Case not found in database: " + currentCaseId);
                showErrorAlert("Case not found: " + currentCaseId);
            }

        } catch (Exception e) {
            System.err.println("Error loading case from database: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Failed to load case details from database: " + e.getMessage());
        }
    }

    private void createExpertSelectionCards() {
        expertsContainer.getChildren().clear();

        if (availableExperts.isEmpty()) {
            Label noExpertsLabel = new Label("No forensic experts available");
            noExpertsLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic; -fx-padding: 20; -fx-alignment: center;");
            expertsContainer.getChildren().add(noExpertsLabel);
            return;
        }

        for (ForensicExpert expert : availableExperts) {
            VBox expertCard = createExpertCard(expert);
            expertsContainer.getChildren().add(expertCard);
        }
    }

    private VBox createExpertCard(ForensicExpert expert) {
        VBox card = new VBox();
        card.getStyleClass().add("expert-card");
        card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16;");
        card.setSpacing(8);

        HBox headerBox = new HBox();
        headerBox.setSpacing(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Radio button for selection
        RadioButton selectButton = new RadioButton();
        selectButton.setToggleGroup(expertToggleGroup);
        selectButton.setUserData(expert.getExpertId());
        selectButton.setStyle("-fx-text-fill: #001440;");

        VBox expertInfo = new VBox();
        expertInfo.setSpacing(4);

        // Expert name
        Label nameLabel = new Label(expert.getName());
        nameLabel.setStyle("-fx-text-fill: #001440; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Specialization and lab info
        Label detailsLabel = new Label(expert.getSpecialization() + " • " + expert.getLabName());
        detailsLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        // Availability status
        String availability = expert.isAvailable() ? "Available" : "Unavailable";
        Label availabilityLabel = new Label(availability);
        availabilityLabel.setStyle("-fx-text-fill: " + (expert.isAvailable() ? "#059669" : "#dc2626") +
                "; -fx-font-size: 11px; -fx-font-weight: bold;");

        expertInfo.getChildren().addAll(nameLabel, detailsLabel, availabilityLabel);
        headerBox.getChildren().addAll(selectButton, expertInfo);

        card.getChildren().addAll(headerBox);

        return card;
    }

    private void loadEvidenceFromDatabase() {
        if (currentCaseId == null || currentCaseId.trim().isEmpty()) {
            System.out.println("No case selected, cannot load evidence");
            return;
        }

        try {
            // Fetch evidence from database for the current case
            availableEvidence = evidenceService.getEvidenceByCase(currentCaseId);

            // Create dynamic checkboxes based on fetched evidence
            createEvidenceCheckboxes();

            System.out.println("Loaded " + availableEvidence.size() + " evidence items for case: " + currentCaseId);

        } catch (Exception e) {
            System.err.println("Error loading evidence from database: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Failed to load evidence from database: " + e.getMessage());
        }
    }

    private void createEvidenceCheckboxes() {
        evidenceCheckboxContainer.getChildren().clear();
        evidenceCheckboxes.clear();

        if (availableEvidence.isEmpty()) {
            Label noEvidenceLabel = new Label("No evidence available for this case");
            noEvidenceLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic; -fx-padding: 20; -fx-alignment: center;");
            evidenceCheckboxContainer.getChildren().add(noEvidenceLabel);
            return;
        }

        for (Evidence evidence : availableEvidence) {
            CheckBox checkbox = new CheckBox();

            String checkboxText = String.format("%s %s\n%s",
                    evidence.getId(),
                    evidence.getType(),
                    evidence.getDescription());

            checkbox.setText(checkboxText);
            checkbox.setUserData(evidence.getId());
            checkbox.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px; -fx-padding: 8 0;");
            checkbox.setWrapText(true);

            checkbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                //updateEvidenceCount();
            });

            evidenceCheckboxes.add(checkbox);
            evidenceCheckboxContainer.getChildren().add(checkbox);
        }
    }

    private void setupComboBoxes() {
        ObservableList<String> analysisTypes = FXCollections.observableArrayList(
                "DNA Analysis",
                "Fingerprint Analysis",
                "Digital Forensics",
                "Ballistics Analysis",
                "Toxicology Screening",
                "Trace Evidence Analysis",
                "Document Examination"
        );
        analysisTypeCombo.setItems(analysisTypes);

        ObservableList<String> priorities = FXCollections.observableArrayList(
                "Urgent",
                "High",
                "Medium",
                "Low"
        );
        priorityCombo.setItems(priorities);
        priorityCombo.setValue("Medium");
    }

    private void setupEventHandlers() {
        analysisTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateSubmitButtonState();
            }
        });

        // Add listener for expert selection
        expertToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            updateSubmitButtonState();
        });
    }

    private void updateSubmitButtonState() {
        int evidenceCount = getSelectedEvidenceCount();
        boolean analysisTypeSelected = analysisTypeCombo.getValue() != null;
        boolean expertSelected = getSelectedExpertId() != null;

        submitButton.setDisable(evidenceCount != 1 || !analysisTypeSelected || !expertSelected);
    }

    private int getSelectedEvidenceCount() {
        int count = 0;
        for (CheckBox checkbox : evidenceCheckboxes) {
            if (checkbox.isSelected()) {
                count++;
            }
        }
        return count;
    }

    private String getSelectedEvidenceId() {
        for (CheckBox checkbox : evidenceCheckboxes) {
            if (checkbox.isSelected()) {
                return (String) checkbox.getUserData();
            }
        }
        return null;
    }

    private String getSelectedExpertId() {
        RadioButton selected = (RadioButton) expertToggleGroup.getSelectedToggle();
        return selected != null ? (String) selected.getUserData() : null;
    }

    @FXML
    private void handleSubmitRequest() {
        try {
            String selectedEvidenceId = getSelectedEvidenceId();
            String selectedExpertId = getSelectedExpertId();

            // Validate form inputs
            if (selectedEvidenceId == null) {
                showErrorAlert("Please select exactly one evidence item");
                return;
            }

            if (analysisTypeCombo.getValue() == null) {
                showErrorAlert("Please select an analysis type");
                return;
            }

            if (selectedExpertId == null) {
                showErrorAlert("Please select a forensic expert");
                return;
            }

            // Validate expert using service
            expertService.validateExpertSelection(selectedExpertId);

            // Set the expert ID for the request service
            requestService.setExpertID(selectedExpertId);

            // Create and submit forensic request
            ForensicRequest request = createForensicRequest(selectedEvidenceId, selectedExpertId);
            requestService.createRequest(request);

            showSuccessAlert();
            resetForm();

        } catch (BusinessException e) {
            showErrorAlert("Failed to submit request: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        resetForm();
    }

    @FXML
    private void handleBackToDashboard() {
        // UI navigation logic here
        // Example: NavigationUtil.navigateToDashboard();
    }

    private ForensicRequest createForensicRequest(String evidenceId, String expertId) {
        // Find the selected evidence to get its type
        Evidence selectedEvidence = null;
        for (Evidence evidence : availableEvidence) {
            if (evidence.getId().equals(evidenceId)) {
                selectedEvidence = evidence;
                break;
            }
        }

        // Create forensic request that matches database schema
        ForensicRequest request = new ForensicRequest();
        request.setAnalysisType(analysisTypeCombo.getValue());
        request.setEvidenceId(evidenceId);
        request.setRequestedBy(currentOfficerId);
        request.setExpertId(expertId);
        request.setPriority(priorityCombo.getValue());
        request.setEvidenceType(selectedEvidence != null ? selectedEvidence.getType() : analysisTypeCombo.getValue());
        request.setStatus("pending");

        // Handle additional notes (you may need to extend your model)
        if (additionalNotesArea.getText() != null && !additionalNotesArea.getText().trim().isEmpty()) {
            // Store notes if your model supports it, otherwise log
            System.out.println("Additional notes for request: " + additionalNotesArea.getText());
        }

        return request;
    }

    private void showSuccessAlert() {
        successAlert.setVisible(true);

        // Auto-hide success alert after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    successAlert.setVisible(false);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Submission Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetForm() {
        // Clear evidence selections
        for (CheckBox checkbox : evidenceCheckboxes) {
            checkbox.setSelected(false);
        }

        // Clear expert selection
        expertToggleGroup.selectToggle(null);

        // Reset form fields
        analysisTypeCombo.setValue(null);
        priorityCombo.setValue("Medium");
        additionalNotesArea.clear();

    }

    public void setCaseData(String caseId, String title, String type, String officer, String location) {
        this.currentCaseId = caseId;

        // Update case details UI
        caseIdLabel.setText(caseId);
        caseTitleLabel.setText(title);
        caseTypeLabel.setText(type);
        assignedOfficerLabel.setText(officer);
        locationLabel.setText(location);

        // Show case details and hide "no case" message
        noCaseSelected.setVisible(false);
        caseDetails.setVisible(true);

        // Load evidence for the selected case
        loadEvidenceFromDatabase();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter for current officer ID (useful for testing)
    public String getCurrentOfficerId() {
        return currentOfficerId;
    }

    // Setter for current officer ID (useful for testing)
    public void setCurrentOfficerId(String officerId) {
        this.currentOfficerId = officerId;
    }
}