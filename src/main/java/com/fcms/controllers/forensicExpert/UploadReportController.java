package com.fcms.controllers.forensicExpert;

import com.fcms.services.ReportService;
import com.fcms.services.ForensicRequestService;
import com.fcms.services.BusinessException;
import com.fcms.models.ForensicRequest;
import com.fcms.models.ForensicReport;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UploadReportController implements Initializable {

    @FXML private Label requestIdLabel;
    @FXML private Label caseIdLabel;
    @FXML private Label evidenceTypeLabel;
    @FXML private Label requestedByLabel;
    @FXML private Label requestDateLabel;
    @FXML private TextField titleField;
    @FXML private DatePicker completionDatePicker;
    @FXML private Button chooseFileButton;
    @FXML private Label fileNameLabel;
    @FXML private TextArea notesArea;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;

    private ForensicRequest currentRequest;
    private File selectedFile;
    private Runnable onSuccessCallback;
    private ReportService reportService;
    private ForensicRequestService requestService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.reportService = new ReportService();
        this.requestService = new ForensicRequestService();
        setupStyles();
        setupEventHandlers();
    }

    public void setRequestData(ForensicRequest request, Runnable onSuccessCallback) {
        this.currentRequest = request;
        this.onSuccessCallback = onSuccessCallback;
        populateRequestData();
    }

    private void setupStyles() {
        chooseFileButton.getStyleClass().add("outline-button");
        submitButton.getStyleClass().add("action-button");
        cancelButton.getStyleClass().add("view-button");
        titleField.getStyleClass().add("form-field");
        completionDatePicker.getStyleClass().add("form-field");
        notesArea.getStyleClass().add("form-field");
    }

    private void setupEventHandlers() {
        chooseFileButton.setOnAction(e -> handleFileSelection());
        submitButton.setOnAction(e -> handleSubmit());
        cancelButton.setOnAction(e -> handleCancel());
        completionDatePicker.setValue(LocalDate.now());
    }

    private void populateRequestData() {
        if (currentRequest != null) {
            requestIdLabel.setText(currentRequest.getRequestId());
            caseIdLabel.setText(currentRequest.getCaseId());
            evidenceTypeLabel.setText(currentRequest.getEvidenceType());
            requestedByLabel.setText(currentRequest.getRequestedBy());
            requestDateLabel.setText(currentRequest.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
            titleField.setText(currentRequest.getEvidenceType() + " Report - " + currentRequest.getCaseId());
        }
    }

    private void handleFileSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Forensic Report");

        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf");
        FileChooser.ExtensionFilter docFilter = new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files", "*.*");

        fileChooser.getExtensionFilters().addAll(pdfFilter, docFilter, allFilter);

        Stage stage = (Stage) chooseFileButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName());
            fileNameLabel.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
        } else {
            fileNameLabel.setText("No file selected");
            fileNameLabel.setStyle("-fx-text-fill: #6b7280;");
        }
    }

    private void handleSubmit() {
        if (!validateForm()) {
            return;
        }

        try {
            ForensicReport report = createReport();
            reportService.uploadReport(report, selectedFile);
            reportService.linkReportToRequest(report.getReportId(), currentRequest.getRequestId());

            showSuccessAlert("Report submitted successfully for " + currentRequest.getRequestId());

            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }

            closeWindow();
        } catch (BusinessException e) {
            showErrorAlert("Failed to submit report: " + e.getMessage());
        }
    }

    private void handleCancel() {
        closeWindow();
    }

    private boolean validateForm() {
        // UI validation only
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            showErrorAlert("Report title is required");
            titleField.requestFocus();
            return false;
        }

        if (completionDatePicker.getValue() == null) {
            showErrorAlert("Completion date is required");
            completionDatePicker.requestFocus();
            return false;
        }

        if (completionDatePicker.getValue().isAfter(LocalDate.now())) {
            showErrorAlert("Completion date cannot be in the future");
            completionDatePicker.requestFocus();
            return false;
        }

        if (selectedFile == null) {
            showErrorAlert("Please select a report file to upload");
            chooseFileButton.requestFocus();
            return false;
        }

        return true;
    }

    private ForensicReport createReport() {
        ForensicReport report = new ForensicReport();
        report.setRequestId(currentRequest.getRequestId());
        report.setTitle(titleField.getText());
        report.setCompletionDate(completionDatePicker.getValue());
        report.setNotes(notesArea.getText());
        return report;
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}