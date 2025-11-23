package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.services.CaseService;
import com.fcms.repositories.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class SubmitToCourtController {

    @FXML private ComboBox<Case> caseDropdown;
    @FXML private Label selectedCaseLabel;

    @FXML private Label evidenceCount;
    @FXML private Label forensicCount;

    @FXML private ComboBox<String> courtOfficialCombo;
    @FXML private Button submitButton;

    private final CaseService caseService = new CaseService();
    private final UserRepository userRepo = new UserRepository();

    @FXML private TextArea caseDetailsArea;

    @FXML
    private void initialize() {

        loadCases();
        loadCourtOfficials();
        renderCaseDropdown();

        caseDropdown.setOnAction(e -> updateCaseUI());
        courtOfficialCombo.setOnAction(e -> updateSubmitButton());

        updateSubmitButton();
    }

    private void loadCases() {
        List<Case> all = caseService.getAllCases();
        caseDropdown.getItems().setAll(all);
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
        Case selected = caseDropdown.getValue();
        if (selected == null) return;

        // fetch latest state
        Case c = caseService.getCaseById(selected.getId());

        selectedCaseLabel.setText(c.getTitle());

        // counts
        int e = caseService.countEvidenceForCase(c.getId());
        int f = caseService.countForensicReportsForCase(c.getId());

        evidenceCount.setText(String.valueOf(e));
        forensicCount.setText(String.valueOf(f));

        // disable button if submitted
        if ("submitted".equalsIgnoreCase(c.getStatus())) {
            disableSubmitButton();
        } else {
            updateSubmitButton();
        }
    }

    private void disableSubmitButton() {
        submitButton.setDisable(true);
        if (!submitButton.getStyleClass().contains("submit-disabled")) {
            submitButton.getStyleClass().add("submit-disabled");
        }
    }

    private void updateSubmitButton() {

        Case selected = caseDropdown.getValue();
        if (selected == null) {
            disableSubmitButton();
            return;
        }

        // fetch real DB state
        Case fresh = caseService.getCaseById(selected.getId());

        // if already submitted
        if ("submitted".equalsIgnoreCase(fresh.getStatus())) {
            disableSubmitButton();
            return;
        }

        boolean ok = courtOfficialCombo.getValue() != null;

        submitButton.setDisable(!ok);

        if (ok) {
            submitButton.getStyleClass().remove("submit-disabled");
        }
    }

    @FXML
    private void handleSubmit() {

        Case c = caseDropdown.getValue();
        String official = courtOfficialCombo.getValue();

        if (c == null || official == null) return;

        // update in DB
        caseService.submitCaseToCourt(c.getId(), official);

        disableSubmitButton();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Case submitted to court successfully.");
        alert.showAndWait();
    }
}
