package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.models.UserSession;
import com.fcms.services.CaseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class CloseCaseController {

    @FXML private ComboBox<Case> activeCaseDropdown;
    @FXML private ComboBox<String> closureReasonDropdown;
    @FXML private TextArea finalReportSummary;
    @FXML private VBox checklistContainer;

    private final CaseService caseService = new CaseService();

    @FXML
    public void initialize() {
        // Configure dropdown cell rendering first
        activeCaseDropdown.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Case c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getId() + " — " + c.getTitle());
            }
        });
        activeCaseDropdown.setButtonCell(activeCaseDropdown.getCellFactory().call(null));

        // Populate UI
        loadActiveCases();

        closureReasonDropdown.getItems().addAll(
                "Resolved", "Transferred", "Insufficient Evidence", "Other"
        );

        // Inject checklist items
        addChecklistItem("All evidence has been logged and stored");
        addChecklistItem("All witnesses have been interviewed");
        addChecklistItem("Forensic analysis completed (if applicable)");
        addChecklistItem("Case documentation is complete");
        addChecklistItem("Supervisor has reviewed the case");
    }

    /**
     * Load only open cases assigned to the signed-in officer.
     * If no session is present, clear the dropdown and show an informational alert.
     */
    private void loadActiveCases() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isLoggedIn()) {
            // No signed-in user: clear dropdown and inform user
            activeCaseDropdown.getItems().clear();
            // Optional: show a subtle message rather than a modal alert
            System.out.println("No user session: active cases list cleared.");
            return;
        }

        String assigned = session.getUserID() != null ? session.getUserID() : session.getUsername();

        List<Case> openCases = caseService.getAllCases().stream()
                .filter(c -> c.getStatus() != null && c.getStatus().equalsIgnoreCase("open"))
                .filter(c -> {
                    String a = c.getAssignedOfficer();
                    return a != null && a.equals(assigned);
                })
                .collect(Collectors.toList());

        activeCaseDropdown.getItems().setAll(openCases);
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

        // Persist closure (service should enforce authorization server-side as well)
        caseService.closeCase(selectedCase.getId(), reason, report);

        showAlert("Case " + selectedCase.getId() + " closed successfully with reason: " + reason);

        // Refresh dropdown to remove closed case
        loadActiveCases();
        activeCaseDropdown.getSelectionModel().clearSelection();
        closureReasonDropdown.getSelectionModel().clearSelection();
        finalReportSummary.clear();
    }

    @FXML
    private void handleCancel() {
        showAlert("Case closure cancelled.");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
