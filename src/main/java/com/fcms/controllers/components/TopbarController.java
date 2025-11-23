package com.fcms.controllers.components;

import com.fcms.app.SceneManager;
import com.fcms.models.Icons;
import com.fcms.models.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class TopbarController {

    @FXML private Pane userIcon;
    @FXML private Label userNameLabel; // username (top)
    @FXML private Label userIdLabel;   // role (bottom)
    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sm) { this.sceneManager = sm; }

    @FXML
    public void initialize() {

        // Add icon
        userIcon.getChildren().add(createIcon("user"));

        // Session
        UserSession session = UserSession.getInstance();

        String username = session.getUsername();  // login username
        String role     = session.getRole();      // Police Officer / Forensic Expert / Court Official

        if (username == null || username.isBlank()) username = "Unknown User";
        if (role == null || role.isBlank()) role = "Unknown Role";

        // TOP = username
        userNameLabel.setText(username);

        // BOTTOM = role
        userIdLabel.setText(role);
    }

    private Icons createIcon(String name) {
        Icons icon = new Icons(name);
        icon.setSize(18);
        return icon;
    }
}
