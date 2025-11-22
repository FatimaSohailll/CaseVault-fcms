package com.fcms.controllers.policeOfficer;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SearchCasesController {

    @FXML private TextField searchByIdField, officerField, locationField;
    @FXML private ComboBox<String> caseTypeDropdown;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private TableView<CaseEntry> resultsTable;
    @FXML private TableColumn<CaseEntry, String> idColumn, titleColumn, typeColumn, dateColumn, statusColumn;
    @FXML private TableColumn<CaseEntry, Void> actionColumn;

    @FXML
    public void initialize() {
        caseTypeDropdown.getItems().addAll("Robbery", "Burglary", "Assault", "Fraud", "Cybercrime", "Theft");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        addActionButtons();

        ObservableList<CaseEntry> dummyCases = FXCollections.observableArrayList(
                new CaseEntry("CS-2025-0087", "Armed Robbery at Central Bank", "Robbery", "08/11/2025", "Open"),
                new CaseEntry("CS-2025-0086", "Residential Burglary - Oak Street", "Burglary", "06/11/2025", "Open"),
                new CaseEntry("CS-2025-0085", "Vehicle Theft Investigation", "Theft", "05/11/2025", "Open"),
                new CaseEntry("CS-2025-0084", "Assault Case - Downtown", "Assault", "03/11/2025", "Closed"),
                new CaseEntry("CS-2025-0083", "Fraud Investigation", "Fraud", "03/11/2025", "Pending"),
                new CaseEntry("CS-2025-0082", "Cybercrime - Data Breach", "Cybercrime", "03/11/2025", "Closed"),
                new CaseEntry("CS-2025-0081", "Domestic Violence Report", "Domestic Violence", "02/11/2025", "Pending")
        );

        resultsTable.setItems(dummyCases);
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View Details");

            {
                viewBtn.getStyleClass().add("nav-btn");
                viewBtn.setOnAction(e -> {
                    CaseEntry caseEntry = getTableView().getItems().get(getIndex());
                    System.out.println("Viewing case: " + caseEntry.getId());
                    // TODO: Navigate to case detail screen or open modal
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
                }
            }
        });
    }

    @FXML
    private void handleResetFilters() {
        searchByIdField.clear();
        officerField.clear();
        locationField.clear();
        caseTypeDropdown.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    public static class CaseEntry {
        private final String id, title, type, date, status;

        public CaseEntry(String id, String title, String type, String date, String status) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.date = date;
            this.status = status;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
    }
}
