package com.fcms.controllers.courtOfficial;

import com.fcms.services.RecordVerdictService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RecordVerdictController {

    @FXML
    private ComboBox<String> verdictCombo;

    @FXML
    private TextArea sentenceField;

    @FXML
    private TextArea notesField;

    @FXML
    private DatePicker verdictDate;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private final RecordVerdictService service = new RecordVerdictService();

    @FXML
    public void initialize() {

        // Load dummy verdict options
        verdictCombo.getItems().addAll(service.getVerdictOptions());

        // Button actions
        saveBtn.setOnAction(e -> saveVerdict());
        cancelBtn.setOnAction(e -> clearForm());
    }

    private void saveVerdict() {

        String verdict = verdictCombo.getValue();
        String sentence = sentenceField.getText();
        String notes = notesField.getText();
        var date = verdictDate.getValue();

        if (verdict == null || sentence.isEmpty() || date == null) {
            showAlert("Missing Required Fields", "Please fill all required fields (*)");
            return;
        }

        boolean success = service.saveVerdict(verdict, sentence, notes, date);

        if (success) {
            showAlert("Success", "Verdict recorded successfully!");
            clearForm();
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
