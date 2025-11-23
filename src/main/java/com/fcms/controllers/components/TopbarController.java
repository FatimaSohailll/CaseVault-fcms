package com.fcms.controllers.components;

import com.fcms.app.SceneManager;
import com.fcms.models.Icons;
import com.fcms.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class TopbarController {

    @FXML private Pane userIcon;
    @FXML private Label userNameLabel; // username (top)
    @FXML private Label userIdLabel;   // role (bottom)
    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sm) { this.sceneManager = sm; }

    @FXML private HBox topbarContainer;

    @FXML
    public void initialize() {

        // Add user icon
        userIcon.getChildren().add(createIcon("user"));

        // Get session
        UserSession session = UserSession.getInstance();
        String username = session.getUsername();
        String role = session.getRole();

        if (username == null || username.isBlank()) username = "Unknown User";
        if (role == null || role.isBlank()) role = "Unknown Role";

        userNameLabel.setText(username);
        userIdLabel.setText(role);

        // 🎨 Apply top bar theme
        applyTopbarTheme(role);
    }

    private Icons createIcon(String name) {
        Icons icon = new Icons(name);
        icon.setSize(18);
        return icon;
    }

    private void applyTopbarTheme(String role) {
        topbarContainer.getStyleClass().removeAll(
                "topbar-police",
                "topbar-court",
                "topbar-forensic",
                "topbar-admin"
        );

        switch (role) {
            case "Police Officer" ->
                    topbarContainer.getStyleClass().add("topbar-police");

            case "Court Official" ->
                    topbarContainer.getStyleClass().add("topbar-court");

            case "Forensic Expert" ->
                    topbarContainer.getStyleClass().add("topbar-forensic");

            case "System Admin" ->
                    topbarContainer.getStyleClass().add("topbar-admin");
        }
    }

}
