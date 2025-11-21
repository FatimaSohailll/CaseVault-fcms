package com.fcms.controllers.policeOfficer;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CloseCaseController {

    @FXML private ComboBox<String> activeCaseDropdown;
    @FXML private ComboBox<String> closureReasonDropdown;
    @FXML private TextArea finalReportSummary;
    @FXML private VBox checklistContainer;

    @FXML
    public void initialize() {
        // Populate dropdowns
        activeCaseDropdown.getItems().addAll("Case #1021", "Case #1043", "Case #1099");
        closureReasonDropdown.getItems().addAll("Resolved", "Transferred", "Insufficient Evidence", "Other");

        // Inject checklist items
        addChecklistItem("All evidence has been logged and stored");
        addChecklistItem("All witnesses have been interviewed");
        addChecklistItem("Forensic analysis completed (if applicable)");
        addChecklistItem("Case documentation is complete");
        addChecklistItem("Supervisor has reviewed the case");
    }

    private void addChecklistItem(String text) {
        Label label = new Label("✔ " + text);
        label.getStyleClass().add("case-meta");
        checklistContainer.getChildren().add(label);
    }

    @FXML
    private void handleCloseCase() {
        // Logic to finalize case closure
    }

    @FXML
    private void handleCancel() {
        // Logic to return to dashboard or previous screen
    }
}
