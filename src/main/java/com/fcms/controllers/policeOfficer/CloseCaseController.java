package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.services.CaseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CloseCaseController {

    @FXML private ComboBox<Case> activeCaseDropdown;
    @FXML private ComboBox<String> closureReasonDropdown;
    @FXML private TextArea finalReportSummary;
    @FXML private VBox checklistContainer;

    private final CaseService caseService = new CaseService(); // unified service

    @FXML
    public void initialize() {
        // Populate dropdowns with active cases from service
        activeCaseDropdown.getItems().addAll(caseService.getAllCases());

        closureReasonDropdown.getItems().addAll(
                "Resolved", "Transferred", "Insufficient Evidence", "Other"
        );

        // Inject checklist items
        addChecklistItem("All evidence has been logged and stored");
        addChecklistItem("All witnesses have been interviewed");
        addChecklistItem("Forensic analysis completed (if applicable)");
        addChecklistItem("Case documentation is complete");
        addChecklistItem("Supervisor has reviewed the case");

        // Show case titles in dropdown
        activeCaseDropdown.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Case c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getId() + " — " + c.getTitle());
            }
        });
        activeCaseDropdown.setButtonCell(activeCaseDropdown.getCellFactory().call(null));
    }

    private void addChecklistItem(String text) {
        Label label = new Label("✔ " + text);
        label.getStyleClass().add("case-meta");
        checklistContainer.getChildren().add(label);
    }

    @FXML
    private void handleCloseCase() {
        Case selectedCase = activeCaseDropdown.getValue();
        String reason = closureReasonDropdown.getValue();
        String report = finalReportSummary.getText();

        if (selectedCase == null) {
            showAlert("Please select a case to close.");
            return;
        }
        if (reason == null || reason.isEmpty()) {
            showAlert("Please select a closure reason.");
            return;
        }
        if (report == null || report.isBlank()) {
            showAlert("Please provide a final report summary.");
            return;
        }

        caseService.closeCase(selectedCase.getId(), reason, report);
        showAlert("Case " + selectedCase.getId() + " closed successfully with reason: " + reason);
    }

    @FXML
    private void handleCancel() {
        // Logic to return to dashboard or previous screen
        showAlert("Case closure cancelled. Returning to dashboard...");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
