package com.fcms.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void switchContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            // Get the existing root BorderPane from the current scene
            BorderPane root = (BorderPane) stage.getScene().getRoot();

            root.setCenter(content);   // ✅ replace only center, keep sidebar/topbar

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
