package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.services.CaseService;
import com.fcms.repositories.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

public class SubmitToCourtController {

    @FXML private ComboBox<Case> caseDropdown;

    @FXML private Label selectedCaseLabel;

    @FXML private HBox itemEvidence;
    @FXML private HBox itemForensic;
    @FXML private HBox itemChain;
    @FXML private HBox itemLegal;
    @FXML private HBox itemOfficer;

    @FXML private Label completionStatus;

    @FXML private Label evidenceCount;
    @FXML private Label forensicCount;

    @FXML private ComboBox<String> courtOfficialCombo;
    @FXML private Button submitButton;

    private final CaseService caseService = new CaseService();
    private final UserRepository userRepo = new UserRepository();

    @FXML private TextArea caseDetailsArea;

    @FXML
    private void initialize() {

        loadActiveCases();
        loadCourtOfficials();

        renderCaseDropdown();

        caseDropdown.setOnAction(e -> updateCaseUI());
        courtOfficialCombo.setOnAction(e -> updateSubmitButton());

        updateSubmitButton();
    }

    private void loadActiveCases() {
        List<Case> open = caseService.getAllCases().stream()
                .filter(c -> "open".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());

        caseDropdown.getItems().setAll(open);
    }

    private void loadCourtOfficials() {
        List<String> officials = userRepo.getAllCourtOfficials();
        courtOfficialCombo.getItems().setAll(officials);
    }

    private void renderCaseDropdown() {
        caseDropdown.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Case c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getId() + " — " + c.getTitle());
            }
        });

        caseDropdown.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Case c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getId() + " — " + c.getTitle());
            }
        });
    }

    private void updateCaseUI() {
        Case c = caseDropdown.getValue();
        if (c == null) return;

        selectedCaseLabel.setText(c.getTitle());

        int e = caseService.countEvidenceForCase(c.getId());
        int f = caseService.countForensicReportsForCase(c.getId());

        evidenceCount.setText("" + e);
        forensicCount.setText("" + f);

        // checklist
        mark(itemEvidence, e > 0);
        mark(itemForensic, f > 0);
        mark(itemChain, true);     // static for now
        mark(itemOfficer, true);   // static for now
        mark(itemLegal, false);    // static for now

        updateCompletionStatus();
        updateSubmitButton();
    }

    private void mark(HBox row, boolean completed) {
        Label icon = (Label) row.getChildren().get(0);
        Label text = (Label) row.getChildren().get(1);

        if (completed) {
            row.setStyle("-fx-background-color: #eaffea; -fx-background-radius: 6; -fx-padding: 15;");
            icon.setText("✔");
            icon.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
            text.setStyle("-fx-text-fill: black;");
        } else {
            row.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 6; -fx-padding: 15;");
            icon.setText("○");
            icon.setStyle("-fx-text-fill: #777; -fx-font-size: 16;");
            text.setStyle("-fx-text-fill: #777;");
        }
    }

    private boolean isCompleted(HBox box) {
        return box.getStyle().contains("#eaffea");
    }

    private void updateCompletionStatus() {
        int done = 0;
        if (isCompleted(itemEvidence)) done++;
        if (isCompleted(itemForensic)) done++;
        if (isCompleted(itemChain)) done++;
        if (isCompleted(itemOfficer)) done++;
        if (isCompleted(itemLegal)) done++;

        // now 5 total items
        completionStatus.setText(done + " of 5 completed");
        completionStatus.setTextFill(done == 5 ? Color.GREEN : Color.ORANGE);
    }

    private void updateSubmitButton() {
        boolean ok =
                caseDropdown.getValue() != null &&
                        courtOfficialCombo.getValue() != null;

        submitButton.setDisable(!ok);
    }

    @FXML
    private void handleSubmit() {

        Case c = caseDropdown.getValue();
        String official = courtOfficialCombo.getValue();

        caseService.submitCaseToCourt(c.getId(), official);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Case submitted to court successfully.");
        alert.showAndWait();
    }
}
