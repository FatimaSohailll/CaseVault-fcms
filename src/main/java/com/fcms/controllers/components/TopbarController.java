package com.fcms.controllers.components;
import com.fcms.models.Icons;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class TopbarController {
    @FXML private Pane userIcon;

    @FXML
    public void initialize() {
        userIcon.getChildren().add(createIcon("user"));
    }

    private Icons createIcon(String name) {
        Icons icon = new Icons(name);
        icon.setSize(18);
        return icon;
    }
}
