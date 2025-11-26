package com.fcms.controllers.policeOfficer;

import com.fcms.models.*;
import com.fcms.services.CaseService;
import com.fcms.services.ChainofCustodyService;
import com.fcms.services.ForensicRequestService;
import com.fcms.services.EvidenceService;
import com.fcms.services.ForensicExpertService;
import com.fcms.services.BusinessException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

public class RequestAnalysisController implements Initializable {

    @FXML private ComboBox<Case> caseComboBox; // Changed to ComboBox<Case>
    @FXML private ComboBox<String> analysisTypeCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextArea additionalNotesArea;
    @FXML private Button submitButton;
    @FXML private VBox successAlert;
    @FXML private VBox evidenceCheckboxContainer;
    @FXML private VBox expertsContainer;

    private ForensicRequestService requestService;
    private ChainofCustodyService chainofCustodyService;
    private EvidenceService evidenceService;
    private ForensicExpertService expertService;
    private CaseService caseService;
    private String currentCaseId;
    private String currentOfficerId;
    private List<Evidence> availableEvidence = new ArrayList<>();
    private List<ForensicExpert> availableExperts = new ArrayList<>();
    private List<Case> availableCases = new ArrayList<>();
    private List<CheckBox> evidenceCheckboxes = new ArrayList<>();
    private ToggleGroup expertToggleGroup = new ToggleGroup();
    private UserSession userSession;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userSession = UserSession.getInstance();

        // Validate that user is logged in and is a police officer
        if (!userSession.isLoggedIn() || !userSession.isPoliceOfficer()) {
            showAlert("Access Denied", "You must be logged in as a Police Officer to access this dashboard.");
            return;
        }

        this.currentOfficerId = userSession.getUserID();
        System.out.println("DEBUG: Initializing Request Analysis for officer: " + currentOfficerId);

        // Initialize services
        this.requestService = new ForensicRequestService();
        this.expertService = new ForensicExpertService();
        this.evidenceService = new EvidenceService(); // Initialize without case ID
        this.caseService = new CaseService(); // Initialize case service

        setupComboBoxes();
        setupEventHandlers();
        loadAvailableCases(); // Load cases for the dropdown
        loadAvailableExperts();
    }

    private void loadAvailableCases() {
        try {
            // Fetch cases assigned to the current officer
            availableCases = caseService.getCasesByOfficer(currentOfficerId);

            // Create observable list for combobox
            ObservableList<Case> caseList = FXCollections.observableArrayList(availableCases);
            caseComboBox.setItems(caseList);

            // Set cell factory to display case information properly
            caseComboBox.setCellFactory(param -> new ListCell<Case>() {
                @Override
                protected void updateItem(Case item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getId() + " - " + item.getTitle());
                    }
                }
            });

            // Set button cell to display selected case
            caseComboBox.setButtonCell(new ListCell<Case>() {
                @Override
                protected void updateItem(Case item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select a case to analyze");
                    } else {
                        setText(item.getId() + " - " + item.getTitle());
                    }
                }
            });

            System.out.println("Loaded " + availableCases.size() + " cases for officer: " + currentOfficerId);

        } catch (Exception e) {
            System.err.println("Error loading cases: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Failed to load cases: " + e.getMessage());
        }
    }

    private void setupComboBoxes() {
        // Case combo box setup is now in loadAvailableCases()

        // Analysis types
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

        // Priorities
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
        // Case selection handler
        caseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handleCaseSelection(newVal);
            } else {
                // Clear evidence when no case is selected
                clearEvidenceSelection();
            }
            updateSubmitButtonState();
        });

        // Analysis type handler
        analysisTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSubmitButtonState();
        });

        // Expert selection handler
        expertToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            updateSubmitButtonState();
        });
    }

    private void handleCaseSelection(Case selectedCase) {
        this.currentCaseId = selectedCase.getId();

        // Update evidence service with selected case ID
        this.evidenceService = new EvidenceService(currentCaseId);

        // Load evidence for the selected case
        loadEvidenceFromDatabase();

        System.out.println("Selected case: " + selectedCase.getId() + " - " + selectedCase.getTitle());
    }

    private void loadEvidenceFromDatabase() {
        if (currentCaseId == null || currentCaseId.trim().isEmpty()) {
            System.out.println("No case selected, cannot load evidence");
            clearEvidenceSelection();
            return;
        }

        try {
            // Fetch evidence from database for the selected case
            availableEvidence = evidenceService.getEvidenceByCase(currentCaseId);

            // Create dynamic checkboxes based on fetched evidence
            createEvidenceCheckboxes();

            System.out.println("Loaded " + availableEvidence.size() + " evidence items for case: " + currentCaseId);

        } catch (Exception e) {
            System.err.println("Error loading evidence from database: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Failed to load evidence from database: " + e.getMessage());
            clearEvidenceSelection();
        }
    }

    private void clearEvidenceSelection() {
        evidenceCheckboxContainer.getChildren().clear();
        evidenceCheckboxes.clear();

        Label noCaseLabel = new Label("Please select a case to view available evidence");
        noCaseLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic; -fx-padding: 20; -fx-alignment: center;");
        evidenceCheckboxContainer.getChildren().add(noCaseLabel);
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

            String checkboxText = String.format("%s - %s\nDescription: %s\nCollected: %s",
                    evidence.getId(),
                    evidence.getType(),
                    evidence.getDescription(),
                    evidence.getCollectionDateTime() != null ? evidence.getCollectionDateTime().toString() : "N/A");

            checkbox.setText(checkboxText);
            checkbox.setUserData(evidence.getId());
            checkbox.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px; -fx-padding: 8 0;");
            checkbox.setWrapText(true);

            checkbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                updateSubmitButtonState();
            });

            evidenceCheckboxes.add(checkbox);
            evidenceCheckboxContainer.getChildren().add(checkbox);
        }
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

    private void updateSubmitButtonState() {
        int evidenceCount = getSelectedEvidenceCount();
        boolean caseSelected = caseComboBox.getValue() != null;
        boolean analysisTypeSelected = analysisTypeCombo.getValue() != null;
        boolean expertSelected = getSelectedExpertId() != null;

        submitButton.setDisable(!caseSelected || evidenceCount != 1 || !analysisTypeSelected || !expertSelected);
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
            Case selectedCase = caseComboBox.getValue();
            String selectedEvidenceId = getSelectedEvidenceId();
            String selectedExpertId = getSelectedExpertId();
            this.chainofCustodyService = new ChainofCustodyService();

            // Validate form inputs
            if (selectedCase == null) {
                showErrorAlert("Please select a case");
                return;
            }

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

            ChainOfCustody c = new  ChainOfCustody();
            c.setTimestamp(LocalDateTime.now());
            c.setEvidenceId(selectedEvidenceId);
            c.setDoneBy(selectedCase.getAssignedOfficer());
            c.setAction("Add Evidence");
            chainofCustodyService.addCustodyRecord(c);


            showAlert("Success: ","Request successfully sent!");
            showAlert("Sucess: ", "Chain of Custody successfully logged!");
            resetForm();

        } catch (BusinessException e) {
            showErrorAlert("Failed to submit request: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        resetForm();
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
       // request.set(currentCaseId); // Set the case ID

        // Handle additional notes
        if (additionalNotesArea.getText() != null && !additionalNotesArea.getText().trim().isEmpty()) {
            // Store notes if your model supports it, otherwise log
            System.out.println("Additional notes for request: " + additionalNotesArea.getText());
            // If your ForensicRequest model has a notes field, set it here:
            // request.setNotes(additionalNotesArea.getText());
        }

        return request;
    }

    private void showSuccessAlert(String msg) {
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
        // Clear case selection
        caseComboBox.setValue(null);

        // Clear evidence selections
        evidenceCheckboxes.clear();
        clearEvidenceSelection();

        // Clear expert selection
        expertToggleGroup.selectToggle(null);

        // Reset form fields
        analysisTypeCombo.setValue(null);
        priorityCombo.setValue("Medium");
        additionalNotesArea.clear();

        // Reset current case ID
        currentCaseId = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getters and setters for testing
    public String getCurrentOfficerId() {
        return currentOfficerId;
    }

    public void setCurrentOfficerId(String officerId) {
        this.currentOfficerId = officerId;
    }
}