package com.fcms.controllers.policeOfficer;

import com.fcms.services.ForensicRequestService;
import com.fcms.services.BusinessException;
import com.fcms.models.ForensicRequest;
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
import java.util.ArrayList;
import java.util.List;

public class RequestAnalysisController implements Initializable {

    @FXML private CheckBox ev001Checkbox;
    @FXML private CheckBox ev002Checkbox;
    @FXML private CheckBox ev003Checkbox;
    @FXML private CheckBox ev004Checkbox;
    @FXML private CheckBox ev005Checkbox;
    @FXML private ComboBox<String> analysisTypeCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextArea additionalNotesArea;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;
    @FXML private VBox successAlert;
    @FXML private VBox noCaseSelected;
    @FXML private VBox caseDetails;
    @FXML private Label caseIdLabel;
    @FXML private Label caseTitleLabel;
    @FXML private Label caseTypeLabel;
    @FXML private Label assignedOfficerLabel;
    @FXML private Label locationLabel;
    @FXML private Label selectedEvidenceCountLabel;
    @FXML private Label analysisTypeSummaryLabel;

    private ForensicRequestService requestService;
    private String currentCaseId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.requestService = new ForensicRequestService();
        setupComboBoxes();
        setupEventHandlers();
        updateEvidenceCount();
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
                "Urgent (24-48 hours)",
                "High (3-5 days)",
                "Normal (1-2 weeks)",
                "Low (2-4 weeks)"
        );
        priorityCombo.setItems(priorities);
        priorityCombo.setValue("Normal (1-2 weeks)");
    }

    private void setupEventHandlers() {
        CheckBox[] checkboxes = {ev001Checkbox, ev002Checkbox, ev003Checkbox, ev004Checkbox, ev005Checkbox};
        for (CheckBox checkbox : checkboxes) {
            checkbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateEvidenceCount());
        }

        analysisTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                analysisTypeSummaryLabel.setText(newVal);
            }
        });
    }

    private void updateEvidenceCount() {
        int count = getSelectedEvidenceCount();
        selectedEvidenceCountLabel.setText(count + " item" + (count != 1 ? "s" : ""));
        submitButton.setDisable(count == 0 || analysisTypeCombo.getValue() == null);
    }

    private int getSelectedEvidenceCount() {
        int count = 0;
        if (ev001Checkbox.isSelected()) count++;
        if (ev002Checkbox.isSelected()) count++;
        if (ev003Checkbox.isSelected()) count++;
        if (ev004Checkbox.isSelected()) count++;
        if (ev005Checkbox.isSelected()) count++;
        return count;
    }

    private List<String> getSelectedEvidenceIds() {
        List<String> evidenceIds = new ArrayList<>();
        if (ev001Checkbox.isSelected()) evidenceIds.add("EV-001");
        if (ev002Checkbox.isSelected()) evidenceIds.add("EV-002");
        if (ev003Checkbox.isSelected()) evidenceIds.add("EV-003");
        if (ev004Checkbox.isSelected()) evidenceIds.add("EV-004");
        if (ev005Checkbox.isSelected()) evidenceIds.add("EV-005");
        return evidenceIds;
    }

    @FXML
    private void handleSubmitRequest() {
        try {
            ForensicRequest request = createForensicRequest();
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
        // UI navigation only
    }

    private ForensicRequest createForensicRequest() {
        ForensicRequest request = new ForensicRequest();
        request.setCaseId(currentCaseId);
        request.setAnalysisType(analysisTypeCombo.getValue());
        request.setEvidenceIds(getSelectedEvidenceIds());
        request.setPriority(priorityCombo.getValue());
        //request.setAdditionalNotes(additionalNotesArea.getText());
        return request;
    }

    private void showSuccessAlert() {
        successAlert.setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    successAlert.setVisible(false);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetForm() {
        ev001Checkbox.setSelected(false);
        ev002Checkbox.setSelected(false);
        ev003Checkbox.setSelected(false);
        ev004Checkbox.setSelected(false);
        ev005Checkbox.setSelected(false);
        analysisTypeCombo.setValue(null);
        priorityCombo.setValue("Normal (1-2 weeks)");
        additionalNotesArea.clear();
        updateEvidenceCount();
        analysisTypeSummaryLabel.setText("Not selected");
    }

    public void setCaseData(String caseId, String title, String type, String officer, String location) {
        this.currentCaseId = caseId;
        caseIdLabel.setText(caseId);
        caseTitleLabel.setText(title);
        caseTypeLabel.setText(type);
        assignedOfficerLabel.setText(officer);
        locationLabel.setText(location);
        noCaseSelected.setVisible(false);
        caseDetails.setVisible(true);
    }

    public static class PoliceDashboardController {

        @FXML private VBox recentCasesContainer;

        @FXML
        public void initialize() {
            loadRecentCases();
        }

        private void loadRecentCases() {
            List<CaseRow> cases = List.of(
                    new CaseRow("CS-2025-0387", "Armed Robbery at Central Bank", "Robbery", "Det. Johnson", "08/11/2025"),
                    new CaseRow("CS-2025-0386", "Residential Burglary - Oak Street", "Burglary", "Det. Martinez", "07/11/2025"),
                    new CaseRow("CS-2025-0385", "Vehicle Theft Investigation", "Vehicle Theft", "Det. Johnson", "06/11/2025"),
                    new CaseRow("CS-2025-0384", "Fraud Investigation", "Fraud", "Det. Williams", "04/11/2025"),
                    new CaseRow("CS-2025-0383", "Assault Case - Downtown", "Assault", "Det. Smith", "03/11/2025")
            );

            for (CaseRow c : cases) {
                VBox card = new VBox(6);
                card.getStyleClass().add("case-card");

    // Top row: Case ID + Status
                HBox topRow = new HBox(8);
                topRow.setAlignment(Pos.CENTER_LEFT);

                Label id = new Label(c.getCaseId());
                id.getStyleClass().add("case-id");

                Label status = new Label();
                status.getStyleClass().add("case-status");

                switch (c.getCaseId()) {
                    case "CS-2025-0387", "CS-2025-0385" -> {
                        status.setText("Open");
                        status.getStyleClass().add("status-open");
                    }
                    case "CS-2025-0384" -> {
                        status.setText("Closed");
                        status.getStyleClass().add("status-closed");
                    }
                    default -> {
                        status.setText("Pending");
                        status.getStyleClass().add("status-pending");
                    }
                }

                topRow.getChildren().addAll(id, status);

    // Title and metadata
                Label title = new Label(c.getTitle());
                title.getStyleClass().add("case-title");

                Label category = new Label("Category: " + c.getCategory());
                Label officer = new Label("Officer: " + c.getOfficer());
                Label date = new Label("Date: " + c.getDate());

                category.getStyleClass().add("case-meta");
                officer.getStyleClass().add("case-meta");
                date.getStyleClass().add("case-meta");

    // Assemble card
                card.getChildren().addAll(topRow, title, category, officer, date);
                card.setOnMouseClicked(e -> openCaseDetails(c.getCaseId()));
                recentCasesContainer.getChildren().add(card);

            }
        }


        private void openCaseDetails(String caseId) {
            System.out.println("Opening details for case: " + caseId);
        }

        // Row model
        public static class CaseRow {
            private final String caseId, title, category, officer, date;
            public CaseRow(String caseId, String title, String category, String officer, String date) {
                this.caseId = caseId;
                this.title = title;
                this.category = category;
                this.officer = officer;
                this.date = date;
            }
            public String getCaseId() { return caseId; }
            public String getTitle() { return title; }
            public String getCategory() { return category; }
            public String getOfficer() { return officer; }
            public String getDate() { return date; }
        }
    }
}