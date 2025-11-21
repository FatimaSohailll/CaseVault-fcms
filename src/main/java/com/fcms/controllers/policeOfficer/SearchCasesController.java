package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SearchCasesController {

    @FXML private TextField searchByIdField, officerField, locationField;
    @FXML private ComboBox<String> caseTypeDropdown;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private TableView<Case> resultsTable;
    @FXML private TableColumn<Case, String> idColumn, titleColumn, typeColumn, statusColumn;
    @FXML private TableColumn<Case, LocalDate> dateColumn;
    @FXML private TableColumn<Case, Void> actionColumn;

    private final ObservableList<Case> dummyCases = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        caseTypeDropdown.getItems().addAll("Robbery", "Burglary", "Assault", "Fraud", "Cybercrime", "Theft", "Domestic Violence");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        addActionButtons();
        loadDummyCases();
        resultsTable.setItems(dummyCases);
    }

    private void loadDummyCases() {
        dummyCases.setAll(
                new Case("CS-2025-0087", "Armed Robbery at Central Bank", "Robbery", "Det. Johnson", "Central Bank", LocalDate.of(2025, 11, 8), "Open"),
                new Case("CS-2025-0086", "Residential Burglary - Oak Street", "Burglary", "Det. Martinez", "Oak Street", LocalDate.of(2025, 11, 6), "Open"),
                new Case("CS-2025-0085", "Vehicle Theft Investigation", "Theft", "Det. Johnson", "Mall Lot", LocalDate.of(2025, 11, 5), "Open"),
                new Case("CS-2025-0084", "Assault Case - Downtown", "Assault", "Det. Smith", "Downtown", LocalDate.of(2025, 11, 3), "Closed"),
                new Case("CS-2025-0083", "Fraud Investigation", "Fraud", "Det. Williams", "Finance District", LocalDate.of(2025, 11, 3), "Pending"),
                new Case("CS-2025-0082", "Cybercrime - Data Breach", "Cybercrime", "Det. Patel", "Tech Park", LocalDate.of(2025, 11, 3), "Closed"),
                new Case("CS-2025-0081", "Domestic Violence Report", "Domestic Violence", "Det. Khan", "Maple Street", LocalDate.of(2025, 11, 2), "Pending")
        );
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("View Details");

            {
                viewBtn.getStyleClass().add("nav-btn");
                viewBtn.setOnAction(e -> {
                    Case selected = getTableView().getItems().get(getIndex());
                    System.out.println("Viewing case: " + selected.getId());
                    // TODO: Navigate to ManageCase or open modal
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
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
        resultsTable.setItems(dummyCases);
    }

    @FXML
    private void handleSearch() {
        String id = searchByIdField.getText().trim();
        String officer = officerField.getText().trim();
        String location = locationField.getText().trim();
        String type = caseTypeDropdown.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        List<Case> filtered = dummyCases.stream()
                .filter(c -> id.isEmpty() || c.getId().contains(id))
                .filter(c -> officer.isEmpty() || c.getOfficer().toLowerCase().contains(officer.toLowerCase()))
                .filter(c -> location.isEmpty() || c.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(c -> type == null || c.getType().equalsIgnoreCase(type))
                .filter(c -> start == null || !c.getDate().isBefore(start))
                .filter(c -> end == null || !c.getDate().isAfter(end))
                .collect(Collectors.toList());

        resultsTable.setItems(FXCollections.observableArrayList(filtered));
    }
}
