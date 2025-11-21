package com.fcms.controllers.policeOfficer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class SubmitToCourtController {

    // ---------- CASE SUMMARY ----------
    @FXML private Label selectedCaseLabel;

    // ---------- CHECKLIST STATUS ----------
    @FXML private HBox itemEvidence;          // ✔ All Evidence Logged
    @FXML private HBox itemForensic;          // ✔ Forensic Report Uploaded
    @FXML private HBox itemStatements;        // ✔ Witness Statements Recorded
    @FXML private HBox itemChain;             // ✔ Chain of Custody Documented
    @FXML private HBox itemLegal;             // ○ Legal Review Completed
    @FXML private HBox itemOfficer;           // ✔ Officer Report Finalized

    @FXML private Label completionStatus;

    // ---------- EVIDENCE SUMMARY ----------
    @FXML private Label evidenceCount;
    @FXML private Label forensicCount;
    @FXML private Label witnessCount;

    // ---------- BUTTONS ----------
    @FXML private Button submitButton;
    @FXML private Button cancelButton;


    @FXML
    private void initialize() {

        // Dummy values for now – you can connect real data later
        selectedCaseLabel.setText("No case selected");

        evidenceCount.setText("5");
        forensicCount.setText("3");
        witnessCount.setText("4");

        updateCompletionStatus();

        // Disable submit if checklist incomplete
        submitButton.setDisable(!isChecklistComplete());
    }


    // --------------------------------------------------------
    // CHECKLIST LOGIC
    // --------------------------------------------------------

    private boolean isChecklistComplete() {
        // itemLegal is incomplete → so checklist incomplete
        return !itemLegal.getStyle().contains("#f5f5f5");
    }

    private int countCompleted() {
        int completed = 0;

        if (isCompleted(itemEvidence)) completed++;
        if (isCompleted(itemForensic)) completed++;
        if (isCompleted(itemStatements)) completed++;
        if (isCompleted(itemChain)) completed++;
        if (isCompleted(itemOfficer)) completed++;

        // itemLegal NOT completed
        return completed;
    }

    private boolean isCompleted(HBox box) {
        return box.getStyle().contains("#eaffea");
    }

    private void updateCompletionStatus() {
        int c = countCompleted();
        completionStatus.setText(c + " of 6 completed");
        completionStatus.setTextFill(Color.web(c == 6 ? "green" : "#d77a00"));
    }


    // --------------------------------------------------------
    // BUTTON ACTIONS
    // --------------------------------------------------------

    @FXML
    private void handleSubmit() {
        if (!isChecklistComplete()) {
            System.out.println("Cannot submit: Checklist incomplete.");
            return;
        }

        System.out.println("Case submitted to court successfully.");
    }

    @FXML
    private void handleCancel() {
        System.out.println("Submission cancelled.");
    }

    // --------------------------------------------------------
    // OPTIONAL: call this from other controllers when selecting a case
    // --------------------------------------------------------

    public void setSelectedCase(String caseName) {
        selectedCaseLabel.setText(caseName);
    }
}
