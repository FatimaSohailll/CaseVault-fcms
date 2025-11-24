package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.services.CaseService;
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

    private final CaseService caseService = new CaseService();
    private ObservableList<Case> allCases = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        caseTypeDropdown.getItems().addAll(
                "Robbery", "Burglary", "Assault", "Fraud", "Cybercrime", "Theft", "Domestic Violence"
        );

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateRegistered"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        addActionButtons();
        loadCasesFromDb();

        searchByIdField.setOnAction(e -> handleSearch());
        System.out.println("Loaded cases: " + allCases.size());
    }

    private void loadCasesFromDb() {
        List<Case> cases = caseService.getAllCases();
        allCases.setAll(cases == null ? List.of() : cases);
        // show all initially; user can clear or search
        resultsTable.setItems(allCases);
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
        // reset to full DB list
        resultsTable.setItems(allCases);
    }

    @FXML
    private void handleSearch() {
        String id = safeTrim(searchByIdField.getText());
        String officer = safeTrim(officerField.getText()).toLowerCase();
        String location = safeTrim(locationField.getText()).toLowerCase();
        String type = caseTypeDropdown.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        // If no filter is set, clear results (prevent showing all)
        if (!isAnyFilterSet(id, officer, location, type, start, end)) {
            resultsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        List<Case> filtered = allCases.stream()
                // ID: partial, case-insensitive
                .filter(c -> id.isEmpty() || (c.getId() != null && c.getId().toLowerCase().contains(id.toLowerCase())))
                // Officer: partial, case-insensitive
                .filter(c -> officer.isEmpty() ||
                        (c.getAssignedOfficer() != null && c.getAssignedOfficer().toLowerCase().contains(officer)))
                // Location: partial, case-insensitive
                .filter(c -> location.isEmpty() ||
                        (c.getLocation() != null && c.getLocation().toLowerCase().contains(location)))
                // Type: exact match (case-insensitive) if selected
                .filter(c -> type == null || type.isBlank() ||
                        (c.getType() != null && c.getType().equalsIgnoreCase(type)))
                // Start date inclusive
                .filter(c -> start == null || (c.getDateRegistered() != null && !c.getDateRegistered().isBefore(start)))
                // End date inclusive
                .filter(c -> end == null || (c.getDateRegistered() != null && !c.getDateRegistered().isAfter(end)))
                .collect(Collectors.toList());

        resultsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private boolean isAnyFilterSet(String id, String officer, String location, String type, LocalDate start, LocalDate end) {
        return (id != null && !id.isBlank())
                || (officer != null && !officer.isBlank())
                || (location != null && !location.isBlank())
                || (type != null && !type.isBlank())
                || start != null
                || end != null;
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
