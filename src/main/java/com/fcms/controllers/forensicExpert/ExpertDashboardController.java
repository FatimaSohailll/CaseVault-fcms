package com.fcms.controllers.forensicExpert;

import com.fcms.models.ForensicRequest;
import com.fcms.services.ForensicRequestService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ExpertDashboardController implements Initializable {

    @FXML private TableView<ForensicRequest> requestsTable;
    @FXML private TableColumn<ForensicRequest, String> requestIdColumn;
    @FXML private TableColumn<ForensicRequest, String> caseIdColumn;
    @FXML private TableColumn<ForensicRequest, String> evidenceTypeColumn;
    @FXML private TableColumn<ForensicRequest, String> requestedByColumn;
    @FXML private TableColumn<ForensicRequest, String> dateColumn;
    @FXML private TableColumn<ForensicRequest, String> statusColumn;
    @FXML private TableColumn<ForensicRequest, Void> actionsColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    @FXML private Label pendingCountLabel;
    @FXML private Label inProgressCountLabel;
    @FXML private Label completedCountLabel;
    @FXML private Label totalCountLabel;

    private ObservableList<ForensicRequest> allRequests = FXCollections.observableArrayList();
    private FilteredList<ForensicRequest> filteredRequests;
    private ForensicRequestService requestService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.requestService = new ForensicRequestService();
        initializeData();
        initializeTable();
        initializeFilters();
        setupSearchFilter();
        updateStats();
    }

    private void initializeData() {
        try {
            // Load data from service layer
            allRequests.setAll(requestService.getAllRequests());
        } catch (Exception e) {
            showAlert("Error", "Failed to load requests: " + e.getMessage());
            // Fallback to sample data if service fails
            initializeSampleData();
        }
    }

    private void initializeSampleData() {
        // Sample data using the updated ForensicRequest model
        allRequests.addAll(
                new ForensicRequest("1", "FR-001", "CASE-4521", "DNA", "Det. Sarah Johnson",
                        LocalDate.of(2025, 11, 8), "Pending", "Blood sample", "DNA Analysis", "High"),
                new ForensicRequest("2", "FR-002", "CASE-4518", "Fingerprint", "Det. Michael Chen",
                        LocalDate.of(2025, 11, 7), "In Progress", "Latent prints", "Fingerprint Analysis", "Urgent"),
                new ForensicRequest("3", "FR-003", "CASE-4522", "Ballistics", "Det. Emily Rodriguez",
                        LocalDate.of(2025, 11, 9), "Pending", "9mm bullet", "Ballistics Analysis", "Normal"),
                new ForensicRequest("4", "FR-004", "CASE-4515", "Toxicology", "Det. James Wilson",
                        LocalDate.of(2025, 11, 5), "Completed", "Blood samples", "Toxicology Screening", "Normal"),
                new ForensicRequest("5", "FR-005", "CASE-4520", "Digital", "Det. Sarah Johnson",
                        LocalDate.of(2025, 11, 6), "In Progress", "Mobile device", "Digital Forensics", "High")
        );

        // Add sample evidence IDs
        allRequests.get(0).setEvidenceIds(java.util.List.of("EV-001", "EV-002"));
        allRequests.get(1).setEvidenceIds(java.util.List.of("EV-003"));
        allRequests.get(2).setEvidenceIds(java.util.List.of("EV-004", "EV-005"));
        allRequests.get(3).setEvidenceIds(java.util.List.of("EV-006"));
        allRequests.get(4).setEvidenceIds(java.util.List.of("EV-007", "EV-008", "EV-009"));
    }

    private void initializeTable() {
        // Configure table columns
        requestIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRequestId()));

        caseIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCaseId()));

        evidenceTypeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAnalysisType()));

        requestedByColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRequestedBy()));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        // Status column with styling
        statusColumn.setCellFactory(column -> new TableCell<ForensicRequest, String>() {
            private final Label statusLabel = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    statusLabel.setText(item);
                    statusLabel.getStyleClass().clear();
                    statusLabel.getStyleClass().add("status-badge");

                    switch (item) {
                        case "Pending":
                            statusLabel.getStyleClass().add("status-pending");
                            break;
                        case "In Progress":
                            statusLabel.getStyleClass().add("status-in-progress");
                            break;
                        case "Completed":
                            statusLabel.getStyleClass().add("status-completed");
                            break;
                        case "Cancelled":
                            statusLabel.getStyleClass().add("status-cancelled");
                            break;
                    }
                    setGraphic(statusLabel);
                    setText(null);
                }
            }
        });

        // Actions column
        actionsColumn.setCellFactory(column -> new TableCell<ForensicRequest, Void>() {
            private final Button actionButton = new Button();
            private final HBox buttonContainer = new HBox();

            {
                buttonContainer.setAlignment(Pos.CENTER);
                buttonContainer.setSpacing(5);
                buttonContainer.getChildren().add(actionButton);

                actionButton.setOnAction(event -> {
                    ForensicRequest request = getTableView().getItems().get(getIndex());
                    if (request != null) {
                        handleAction(request);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ForensicRequest request = getTableView().getItems().get(getIndex());
                    if (request != null) {
                        if (request.isCompleted()) {
                            actionButton.setText("View Report");
                            actionButton.getStyleClass().setAll("view-button");
                            actionButton.setDisable(false);
                        } else if (request.isInProgress() || request.isPending()) {
                            actionButton.setText("Upload Report");
                            actionButton.getStyleClass().setAll("action-button");
                            actionButton.setDisable(false);
                        } else {
                            actionButton.setText("No Action");
                            actionButton.getStyleClass().setAll("view-button");
                            actionButton.setDisable(true);
                        }
                        setGraphic(buttonContainer);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Setup filtered list
        filteredRequests = new FilteredList<>(allRequests);
        SortedList<ForensicRequest> sortedRequests = new SortedList<>(filteredRequests);
        sortedRequests.comparatorProperty().bind(requestsTable.comparatorProperty());
        requestsTable.setItems(sortedRequests);
    }

    private void initializeFilters() {
        // Status filter
        statusFilter.setItems(FXCollections.observableArrayList(
                "All Status", "Pending", "In Progress", "Completed", "Cancelled"
        ));
        statusFilter.setValue("All Status");

        // Set styles
        statusFilter.getStyleClass().add("filter-combo");
    }

    private void setupSearchFilter() {
        searchField.getStyleClass().add("search-field");

        // Add listeners for real-time filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        filteredRequests.setPredicate(createPredicate());
        updateStats();
    }

    private Predicate<ForensicRequest> createPredicate() {
        return request -> {
            String searchText = searchField.getText();
            String statusFilterValue = statusFilter.getValue();

            // Search filter
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                boolean matchesSearch =
                        request.getRequestId().toLowerCase().contains(lowerCaseFilter) ||
                                request.getCaseId().toLowerCase().contains(lowerCaseFilter) ||
                                request.getAnalysisType().toLowerCase().contains(lowerCaseFilter) ||
                                request.getRequestedBy().toLowerCase().contains(lowerCaseFilter) ||
                                (request.getEvidenceDetails() != null &&
                                        request.getEvidenceDetails().toLowerCase().contains(lowerCaseFilter));

                if (!matchesSearch) {
                    return false;
                }
            }

            // Status filter
            if (statusFilterValue != null && !statusFilterValue.equals("All Status")) {
                if (!request.getStatus().equals(statusFilterValue)) {
                    return false;
                }
            }

            return true;
        };
    }

    private void updateStats() {
        try {
            // Use filtered data for statistics
            long pendingCount = filteredRequests.stream().filter(req -> req.isPending()).count();
            long inProgressCount = filteredRequests.stream().filter(req -> req.isInProgress()).count();
            long completedCount = filteredRequests.stream().filter(req -> req.isCompleted()).count();
            long totalCount = filteredRequests.size();

            pendingCountLabel.setText(String.valueOf(pendingCount));
            inProgressCountLabel.setText(String.valueOf(inProgressCount));
            completedCountLabel.setText(String.valueOf(completedCount));
            totalCountLabel.setText(String.valueOf(totalCount));
        } catch (Exception e) {
            showAlert("Error", "Failed to update statistics: " + e.getMessage());
        }
    }

    private void handleAction(ForensicRequest request) {
        try {
            if (request.isCompleted()) {
                viewReport(request);
            } else if (request.isInProgress() || request.isPending()) {
                uploadReport(request);
            } else {
                showAlert("Information", "No action available for " + request.getStatus() + " requests");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to perform action: " + e.getMessage());
        }
    }

    private void uploadReport(ForensicRequest request) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/expert/uploadReport.fxml"));
            Parent root = loader.load();

            UploadReportController controller = loader.getController();
            controller.setRequestData(request, () -> {
                // Callback when upload is successful
                refreshData();
                updateStats();
                requestsTable.refresh();
                showAlert("Success", "Report uploaded successfully for " + request.getRequestId());
            });

            Stage stage = new Stage();
            stage.setTitle("Upload Forensic Report - " + request.getRequestId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Apply styles
            Scene scene = stage.getScene();
            scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load upload form: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Failed to open upload form: " + e.getMessage());
        }
    }

    private void viewReport(ForensicRequest request) {
        showAlert("View Report",
                "Viewing report for: " + request.getRequestId() +
                        "\nCase: " + request.getCaseId() +
                        "\nAnalysis Type: " + request.getAnalysisType() +
                        "\nStatus: " + request.getStatus());
    }

    private void refreshData() {
        try {
            allRequests.setAll(requestService.getAllRequests());
            applyFilters(); // Re-apply filters after refresh
        } catch (Exception e) {
            showAlert("Error", "Failed to refresh data: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        refreshData();
        showAlert("Refresh", "Data refreshed successfully");
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        statusFilter.setValue("All Status");
        applyFilters();
    }

    @FXML
    private void handleExportData() {
        // Export functionality would go here
        showAlert("Export", "Export functionality would be implemented here");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}