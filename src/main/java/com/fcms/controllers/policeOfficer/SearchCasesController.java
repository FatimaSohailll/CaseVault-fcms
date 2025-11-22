package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.services.CaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    }

    private void loadCasesFromDb() {
        List<Case> cases = caseService.getAllCases();
        allCases.setAll(cases);
        resultsTable.setItems(allCases);
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
        resultsTable.setItems(allCases); // reset to full DB list
    }

    @FXML
    private void handleSearch() {
        String id = searchByIdField.getText().trim();
        String officer = officerField.getText().trim();
        String location = locationField.getText().trim();
        String type = caseTypeDropdown.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        List<Case> filtered = allCases.stream()
                .filter(c -> id.isEmpty() || c.getId().contains(id))
                .filter(c -> officer.isEmpty() ||
                        (c.getAssignedOfficer() != null && c.getAssignedOfficer().toLowerCase().contains(officer.toLowerCase())))
                .filter(c -> location.isEmpty() ||
                        (c.getLocation() != null && c.getLocation().toLowerCase().contains(location.toLowerCase())))
                .filter(c -> type == null || (c.getType() != null && c.getType().equalsIgnoreCase(type)))
                .filter(c -> start == null || (c.getDateRegistered() != null && !c.getDateRegistered().isBefore(start)))
                .filter(c -> end == null || (c.getDateRegistered() != null && !c.getDateRegistered().isAfter(end)))
                .collect(Collectors.toList());

        resultsTable.setItems(FXCollections.observableArrayList(filtered));
    }
}
