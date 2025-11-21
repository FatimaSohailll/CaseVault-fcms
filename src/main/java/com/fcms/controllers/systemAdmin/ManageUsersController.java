package com.fcms.controllers.systemAdmin;

import com.fcms.models.users.UserAccount;
import com.fcms.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;

public class ManageUsersController {

    @FXML private TableView<UserAccount> usersTable;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label totalUsersLabel;

    private final UserService userService = new UserService();

    private ObservableList<UserAccount> allUserAccounts = FXCollections.observableArrayList();
    private ObservableList<UserAccount> filteredUserAccounts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // USER ID
        TableColumn<UserAccount, String> idCol =
                (TableColumn<UserAccount, String>) usersTable.getColumns().get(0);
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUserID()));

        // NAME
        TableColumn<UserAccount, String> nameCol =
                (TableColumn<UserAccount, String>) usersTable.getColumns().get(1);
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        // EMAIL
        TableColumn<UserAccount, String> emailCol =
                (TableColumn<UserAccount, String>) usersTable.getColumns().get(2);
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        // ROLE
        TableColumn<UserAccount, String> roleCol =
                (TableColumn<UserAccount, String>) usersTable.getColumns().get(3);
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));

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

        loadUsers();
        updateUserCount();

        // Filters
        roleFilter.setItems(FXCollections.observableArrayList(
                "All Roles", "Police", "Forensic Expert", "Court Official"
        ));
        roleFilter.setValue("All Roles");

        statusFilter.setItems(FXCollections.observableArrayList(
                "All Status", "Active", "Pending"
        ));
        statusFilter.setValue("All Status");

        searchField.textProperty().addListener((o, oldV, newV) -> applyFilters());
        roleFilter.valueProperty().addListener((o, oldV, newV) -> applyFilters());
        statusFilter.valueProperty().addListener((o, oldV, newV) -> applyFilters());

        applyFilters();
    }

    private void loadUsers() {
        allUserAccounts.setAll(userService.getAllUsers());

        // Exclude pending accounts (they belong in Waiting List)
        allUserAccounts.removeIf(u ->
                u.getManagedBy() != null &&
                        u.getManagedBy().equalsIgnoreCase("Pending")
        );
    }
    private void updateUserCount() {
        totalUsersLabel.setText("All Users (" + userService.getUserCount() + ")");
    }

    private void applyFilters() {
        filteredUserAccounts.clear();

        String search = searchField.getText().toLowerCase().trim();
        String selectedRole = roleFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        for (UserAccount u : allUserAccounts) {

            boolean matchesSearch =
                    u.getName().toLowerCase().contains(search) ||
                            u.getEmail().toLowerCase().contains(search);

            boolean matchesRole =
                    selectedRole.equals("All Roles") || u.getRole().equals(selectedRole);

            boolean isPending = u.getManagedBy().equalsIgnoreCase("pending");
            String currentStatus = isPending ? "Pending" : "Active";

            boolean matchesStatus =
                    selectedStatus.equals("All Status") || currentStatus.equals(selectedStatus);

            if (matchesSearch && matchesRole && matchesStatus)
                filteredUserAccounts.add(u);
        }

        usersTable.setItems(filteredUserAccounts);
    }

    public void reloadTable() {
        loadUsers();
        applyFilters();
        updateUserCount();
    }

    private void openUserDialog(boolean isEdit, UserAccount user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/systemAdmin/userDialog.fxml"));
            Parent root = loader.load();

            UserController ctrl = loader.getController();
            ctrl.setParent(this);

            if (isEdit)
                ctrl.loadUser(user);

            Stage stage = new Stage();
            stage.setTitle(isEdit ? "Edit User" : "Add User");
            stage.setScene(new Scene(root));
            stage.setWidth(450);    // bigger width
            stage.setHeight(420);   // bigger height
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
        loadUsers();
        applyFilters();
        updateUserCount();
    }
}
