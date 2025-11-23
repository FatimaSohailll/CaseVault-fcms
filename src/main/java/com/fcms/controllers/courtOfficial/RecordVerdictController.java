package com.fcms.controllers.courtOfficial;

import com.fcms.models.Case;
import com.fcms.services.RecordVerdictService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RecordVerdictController {

    @FXML private Label caseIdLabel;
    @FXML private Label titleLabel;
    @FXML private Label typeLabel;
    @FXML private Label officerLabel;
    @FXML private Label dateLabel;

    @FXML private ComboBox<String> verdictCombo;
    @FXML private TextArea sentenceField;
    @FXML private TextArea notesField;
    @FXML private DatePicker verdictDate;

    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private final RecordVerdictService service = new RecordVerdictService();
    private Case currentCase;

    // =====================================================
    // LOAD CASE DATA (from search page)
    // =====================================================
    public void setCaseData(Case c) {
        this.currentCase = c;

        caseIdLabel.setText(c.getId());
        titleLabel.setText(c.getTitle());
        typeLabel.setText(c.getType());
        officerLabel.setText(c.getAssignedOfficer());     // FIXED
        dateLabel.setText(c.getDateRegistered() != null
                ? c.getDateRegistered().toString()
                : "N/A");                                // FIXED
    }

    @FXML
    public void initialize() {
        verdictCombo.getItems().addAll(service.getVerdictOptions());

        saveBtn.setOnAction(e -> saveVerdict());
        cancelBtn.setOnAction(e -> clearForm());
    }

    // =====================================================
    // SAVE VERDICT
    // =====================================================
    private void saveVerdict() {

        String verdict = verdictCombo.getValue();
        String sentence = sentenceField.getText();
        String notes = notesField.getText();
        var date = verdictDate.getValue();

        if (currentCase == null) {
            showAlert("Error", "No case selected.");
            return;
        }

        if (verdict == null || sentence.isEmpty() || date == null) {
            showAlert("Missing Required Fields", "Please fill all required fields (*)");
            return;
        }

        // TODO: Replace with actual logged-in court official ID
        String courtOfficialId = "COURT-UNKNOWN";     // TEMP FIX

        boolean success = service.saveVerdict(
                verdict,
                sentence,
                notes,
                date,
                currentCase.getId(),
                courtOfficialId
        );

        if (success) {
            showAlert("Success", "Verdict recorded successfully!");
            clearForm();
        } else {
            showAlert("Error", "Failed to save verdict.");
        }
    }

    private void clearForm() {
        verdictCombo.setValue(null);
        sentenceField.clear();
        notesField.clear();
        verdictDate.setValue(null);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}
