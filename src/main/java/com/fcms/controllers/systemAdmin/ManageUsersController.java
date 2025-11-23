package com.fcms.controllers.systemAdmin;

import com.fcms.models.users.UserAccount;
import com.fcms.services.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ManageUsersController {

    @FXML private TableView<UserAccount> usersTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Label totalUsersLabel;

    private final UserService userService = new UserService();

    private final ObservableList<UserAccount> allUserAccounts =
            FXCollections.observableArrayList();
    private final ObservableList<UserAccount> filteredUserAccounts =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // USER ID
        TableColumn<UserAccount, String> idCol =
                (TableColumn<UserAccount, String>) usersTable.getColumns().get(0);
        idCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getUserID()));

        // NAME
        TableColumn<UserAccount, String> nameCol =
                (TableColumn<UserAccount, String>) usersTable.getColumns().get(1);
        nameCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName()));

        // EMAIL
        TableColumn<UserAccount, String> emailCol =
                (TableColumn<UserAccount, String>) usersTable.getColumns().get(2);
        emailCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmail()));

        // ROLE
        TableColumn<UserAccount, String> roleCol =
                (TableColumn<UserAccount, String>) usersTable.getColumns().get(3);
        roleCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getRole()));

        // CREATED AT
        TableColumn<UserAccount, String> createdCol =
                (TableColumn<UserAccount, String>) usersTable.getColumns().get(4);
        createdCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCreatedAt()));

        // ACTION BUTTONS
        TableColumn<UserAccount, Void> actionsCol =
                (TableColumn<UserAccount, Void>) usersTable.getColumns().get(5);

        actionsCol.setCellFactory(col -> new TableCell<>() {

            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color:#2196f3; -fx-text-fill:white;");
                deleteBtn.setStyle("-fx-background-color:#f44336; -fx-text-fill:white;");
                container.setStyle("-fx-alignment: CENTER_LEFT;");

                editBtn.setOnAction(e ->
                        handleEdit(getTableView().getItems().get(getIndex())));

                deleteBtn.setOnAction(e ->
                        handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : container);
            }
        });

        // Load users from DB (only approved)
        loadUsers();

        // Role filter only
        roleFilter.setItems(FXCollections.observableArrayList(
                "All Roles",
                "Police Officer",
                "Forensic Expert",
                "Court Official"
        ));
        roleFilter.setValue("All Roles");

        // Listeners
        searchField.textProperty().addListener((o, oldV, newV) -> applyFilters());
        roleFilter.valueProperty().addListener((o, oldV, newV) -> applyFilters());

        // Initial filter + table bind
        applyFilters();
    }

    // LOAD ONLY APPROVED USERS (approved = true)
    private void loadUsers() {
        allUserAccounts.setAll(userService.getAllUsers());
        allUserAccounts.removeIf(u -> !u.isApproved());
    }

    private void updateUserCount() {
        totalUsersLabel.setText("All Users (" + filteredUserAccounts.size() + ")");
    }

    private void applyFilters() {
        filteredUserAccounts.clear();

        String search = searchField.getText() == null
                ? ""
                : searchField.getText().toLowerCase().trim();

        String selectedRole = roleFilter.getValue();
        if (selectedRole == null) selectedRole = "All Roles";

        for (UserAccount u : allUserAccounts) {

            // --- search: name OR email (you can add username here if you want) ---
            boolean matchesSearch =
                    u.getName().toLowerCase().contains(search) ||
                            u.getEmail().toLowerCase().contains(search);

            // --- role filter ---
            String role = u.getRole() == null ? "" : u.getRole().trim();

            boolean matchesRole =
                    selectedRole.equals("All Roles") ||
                            role.equalsIgnoreCase(selectedRole);

            if (matchesSearch && matchesRole) {
                filteredUserAccounts.add(u);
            }
        }

        usersTable.setItems(filteredUserAccounts);
        updateUserCount();
    }

    public void reloadTable() {
        loadUsers();
        applyFilters();
    }

    private void openUserDialog(boolean isEdit, UserAccount user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/systemAdmin/userDialog.fxml"));
            Parent root = loader.load();

            UserController ctrl = loader.getController();
            ctrl.setParent(this);

            if (isEdit) ctrl.loadUser(user);

            Stage stage = new Stage();
            stage.setTitle(isEdit ? "Edit User" : "Add User");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddUser() {
        openUserDialog(false, null);
    }

    private void handleEdit(UserAccount user) {
        openUserDialog(true, user);
    }

    private void handleDelete(UserAccount userAccount) {
        userService.deleteUser(userAccount.getUserID());
        reloadTable();
    }
}
