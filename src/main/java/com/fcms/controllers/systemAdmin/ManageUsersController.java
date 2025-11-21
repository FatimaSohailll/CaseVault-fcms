package com.fcms.controllers.systemAdmin;

import com.fcms.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class ManageUsersController {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> roleFilter;

    @FXML
    private ComboBox<String> statusFilter;

    private ObservableList<User> allUsers = FXCollections.observableArrayList();
    private ObservableList<User> filteredUsers = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // ---- Setup Columns ----
        TableColumn<User, String> nameCol = (TableColumn<User, String>) usersTable.getColumns().get(0);
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));

        TableColumn<User, String> emailCol = (TableColumn<User, String>) usersTable.getColumns().get(1);
        emailCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<User, String> roleCol = (TableColumn<User, String>) usersTable.getColumns().get(2);
        roleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRole()));

        TableColumn<User, String> deptCol = (TableColumn<User, String>) usersTable.getColumns().get(3);
        deptCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDepartment()));

        TableColumn<User, String> statusCol = (TableColumn<User, String>) usersTable.getColumns().get(4);
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));

        TableColumn<User, Void> actionsCol = (TableColumn<User, Void>) usersTable.getColumns().get(5);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color:#2196f3; -fx-text-fill:white; -fx-background-radius:5;");
                deleteBtn.setStyle("-fx-background-color:#f44336; -fx-text-fill:white; -fx-background-radius:5;");

                editBtn.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : container);
            }
        });

        //--------------------------------------------------
        // Load Dummy Data
        //--------------------------------------------------
        loadDummyUsers();

        //--------------------------------------------------
        // Fill Filters
        //--------------------------------------------------
        roleFilter.setItems(FXCollections.observableArrayList(
                "All Roles", "Police Officer", "Forensic Expert", "Court Official", "Admin"
        ));
        roleFilter.setValue("All Roles");

        statusFilter.setItems(FXCollections.observableArrayList(
                "All Status", "Active", "Pending", "Suspended"
        ));
        statusFilter.setValue("All Status");

        //--------------------------------------------------
        // Add Search + Filters
        //--------------------------------------------------
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        roleFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        //--------------------------------------------------
        // Display initial data
        //--------------------------------------------------
        applyFilters();
    }

    //--------------------------------------------------
    // Dummy Data
    //--------------------------------------------------
    private void loadDummyUsers() {
        allUsers.addAll(
                new User("John Smith", "john@casevault.gov", "Police Officer", "XYZ County", "Active"),
                new User("Jane Doe", "jane@casevault.gov", "Forensic Expert", "ABC County", "Pending"),
                new User("Adam Ray", "adam@casevault.gov", "Court Official", "Central Court", "Active"),
                new User("Sara Malik", "sara@casevault.gov", "Admin", "Head Office", "Active"),
                new User("Tom Hanks", "tom@casevault.gov", "Police Officer", "North Division", "Suspended"),
                new User("Emily Clark", "emily@casevault.gov", "Forensic Expert", "West Labs", "Active")
        );
    }

    //--------------------------------------------------
    // Filtering Logic
    //--------------------------------------------------
    private void applyFilters() {
        filteredUsers.clear();

        String search = searchField.getText().toLowerCase().trim();
        String selectedRole = roleFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        for (User u : allUsers) {

            boolean matchesSearch =
                    u.getName().toLowerCase().contains(search) ||
                            u.getEmail().toLowerCase().contains(search);

            boolean matchesRole =
                    selectedRole.equals("All Roles") || u.getRole().equals(selectedRole);

            boolean matchesStatus =
                    selectedStatus.equals("All Status") || u.getStatus().equals(selectedStatus);

            if (matchesSearch && matchesRole && matchesStatus) {
                filteredUsers.add(u);
            }
        }

        usersTable.setItems(filteredUsers);
    }

    //--------------------------------------------------
    // Actions
    //--------------------------------------------------
    private void handleEdit(User user) {
        System.out.println("Edit: " + user.getName());
        // open edit popup window later
    }

    private void handleDelete(User user) {
        allUsers.remove(user);
        applyFilters();
    }
}
