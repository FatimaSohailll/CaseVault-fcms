package com.fcms.controllers.systemAdmin;

import com.fcms.models.users.*;
import com.fcms.repositories.UserRepository;
import com.fcms.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserController {

    @FXML private TextField usernameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleField;

    @FXML private VBox roleSpecificBox;

    @FXML private GridPane policeBox;
    @FXML private TextField policeRankField;
    @FXML private TextField policeDeptField;

    @FXML private GridPane courtBox;
    @FXML private TextField courtNameField;
    @FXML private TextField courtDesigField;

    @FXML private GridPane expertBox;
    @FXML private TextField labNameField;

    private ManageUsersController parentController;

    private final UserService userService = new UserService();
    private UserAccount editingUser;
    private boolean editMode = false;

    public void setParent(ManageUsersController ctrl) { this.parentController = ctrl; }

    @FXML
    public void initialize() {
        roleField.getItems().addAll("Police Officer", "Court Official", "Forensic Expert");
        roleField.valueProperty().addListener((obs, oldVal, newVal) -> showRoleFields(newVal));
    }

    private void showRoleFields(String role) {

        roleSpecificBox.setVisible(false);
        roleSpecificBox.setManaged(false);
        policeBox.setVisible(false);
        policeBox.setManaged(false);
        courtBox.setVisible(false);
        courtBox.setManaged(false);
        expertBox.setVisible(false);
        expertBox.setManaged(false);

        if (role == null) return;

        roleSpecificBox.setVisible(true);
        roleSpecificBox.setManaged(true);

        switch (role) {
            case "Police Officer" -> {
                policeBox.setVisible(true);
                policeBox.setManaged(true);
            }
            case "Court Official" -> {
                courtBox.setVisible(true);
                courtBox.setManaged(true);
            }
            case "Forensic Expert" -> {
                expertBox.setVisible(true);
                expertBox.setManaged(true);
            }
        }
    }

    // =====================================================
    // EDIT EXISTING USER
    // =====================================================
    public void loadUser(UserAccount user) {
        this.editingUser = user;
        this.editMode = true;

        usernameField.setText(user.getUsername());
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getPassword());

        roleField.setValue(user.getRole());
        showRoleFields(user.getRole());

        if (user instanceof PoliceOfficer p) {
            policeRankField.setText(p.getRank());
            policeDeptField.setText(p.getDepartment());
        }

        if (user instanceof CourtOfficial c) {
            courtNameField.setText(c.getCourtName());
            courtDesigField.setText(c.getDesignation());
        }

        if (user instanceof ForensicExpert f) {
            labNameField.setText(f.getLabName());
        }
    }

    // =====================================================
    // SAVE USER
    // =====================================================
    @FXML
    private void saveUser() {

        String role = roleField.getValue();   // <-- MUST be defined FIRST

        String id;

        if (editMode) {
            id = editingUser.getUserID();
        } else {
            id = UserRepository.generateUserID(role);  // now works
        }

        String username = usernameField.getText();
        String name     = nameField.getText();
        String email    = emailField.getText();
        String password = passwordField.getText();

        String managedBy = "System Admin";

        UserAccount u;

        switch (role) {

            case "Police Officer" -> {
                u = new PoliceOfficer(
                        id,
                        username,
                        name,
                        email,
                        password,
                        role,
                        managedBy,
                        true,
                        null,
                        policeRankField.getText(),
                        policeDeptField.getText()
                );
            }

            case "Court Official" -> {
                u = new CourtOfficial(
                        id,
                        username,
                        name,
                        email,
                        password,
                        role,
                        managedBy,
                        true,
                        null,
                        courtNameField.getText(),
                        courtDesigField.getText()
                );
            }

            default -> {
                u = new ForensicExpert(
                        id,
                        username,
                        name,
                        email,
                        password,
                        role,
                        managedBy,
                        true,
                        null,
                        labNameField.getText()
                );
            }
        }

        if (!editMode)
            userService.addUser(u);
        else {
            u.setUserID(editingUser.getUserID());
            userService.updateUser(u);
        }

        if (parentController != null)
            parentController.reloadTable();

        closeDialog();
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
