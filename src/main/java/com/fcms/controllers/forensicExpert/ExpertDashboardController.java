package com.fcms.controllers.forensicExpert;

import com.fcms.models.ForensicRequest;
import com.fcms.models.UserSession; // Add this import
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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class ExpertDashboardController implements Initializable {

    @FXML private TableView<ForensicRequest> requestsTable;
    @FXML private TableColumn<ForensicRequest, String> requestIdColumn;
    @FXML private TableColumn<ForensicRequest, String> evidenceIdColumn;
    @FXML private TableColumn<ForensicRequest, String> evidenceTypeColumn;
    @FXML private TableColumn<ForensicRequest, String> requestedByColumn;
    @FXML private TableColumn<ForensicRequest, String> dateColumn;
    @FXML private TableColumn<ForensicRequest, String> statusColumn;
    @FXML private TableColumn<ForensicRequest, String> priorityColumn;
    @FXML private TableColumn<ForensicRequest, Void> actionsColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    @FXML private Label pendingCountLabel;
    @FXML private Label inProgressCountLabel;
    @FXML private Label completedCountLabel;
    @FXML private Label totalCountLabel;

    @FXML private Label welcomeLabel; // Add this to show logged-in user info
    @FXML private Label userRoleLabel; // Add this to show user role

    private ObservableList<ForensicRequest> allRequests = FXCollections.observableArrayList();
    private FilteredList<ForensicRequest> filteredRequests;
    private ForensicRequestService requestService;
    private UserSession userSession; // Add UserSession reference

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get the current user session
        userSession = UserSession.getInstance();

        // Validate that user is logged in and is a forensic expert
        if (!userSession.isLoggedIn() || !userSession.isForensicExpert()) {
            showAlert("Access Denied", "You must be logged in as a Forensic Expert to access this dashboard.");
            return;
        }

        String currentUserId = userSession.getUserID();
        System.out.println("DEBUG: Initializing Expert Dashboard for user: " + currentUserId);

        // Use the actual logged-in user ID instead of hardcoded value
        this.requestService = new ForensicRequestService(currentUserId);

        // Display user information
        //displayUserInfo();

        initializeData();
        initializeTable();
        initializeFilters();
        setupSearchFilter();
        updateStats();
    }

    private void initializeData() {
        try {
            // Load data from service layer - service talks to repo which gets from DB
            allRequests.setAll(requestService.getAllRequests());
        } catch (Exception e) {
            showAlert("Error", "Failed to load requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeTable() {
        // Configure table columns
        requestIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRequestId()));

        evidenceIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEvidenceId()));

        evidenceTypeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEvidenceType()));

        requestedByColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRequestedBy()));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRequestedDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));

        priorityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPriority()));

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

                    switch (item.toLowerCase()) {
                        case "pending":
                            statusLabel.getStyleClass().add("status-pending");
                            break;
                        case "completed":
                            statusLabel.getStyleClass().add("status-completed");
                            break;
                        default:
                            statusLabel.getStyleClass().add("status-pending");
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
                        } else if (request.isPending()) {
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
                "All Status", "pending", "completed"
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
                                request.getEvidenceId().toLowerCase().contains(lowerCaseFilter) ||
                                request.getEvidenceType().toLowerCase().contains(lowerCaseFilter) ||
                                request.getRequestedBy().toLowerCase().contains(lowerCaseFilter) ||
                                request.getAnalysisType().toLowerCase().contains(lowerCaseFilter);

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
            // Get counts from SERVICE, not from filtered list
            int pendingCount = requestService.getPendingCount();
            int inProgressCount = 0;
            int completedCount = requestService.getCompletedCount();
            int totalCount = requestService.getTotalCount();

            pendingCountLabel.setText(String.valueOf(pendingCount));
            inProgressCountLabel.setText(String.valueOf(inProgressCount));
            completedCountLabel.setText(String.valueOf(completedCount));
            totalCountLabel.setText(String.valueOf(totalCount));
        } catch (Exception e) {
            showAlert("Error", "Failed to update statistics: " + e.getMessage());
            e.printStackTrace();
            // Set default values
            pendingCountLabel.setText("0");
            inProgressCountLabel.setText("0");
            completedCountLabel.setText("0");
            totalCountLabel.setText("0");
        }
    }

    private void handleAction(ForensicRequest request) {
        try {
            if (request.isCompleted()) {
                viewReport(request);
            } else if (request.isPending()) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forensicExpert/uploadReport.fxml"));
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
                        "\nEvidence: " + request.getEvidenceId() +
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
        updateStats();
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