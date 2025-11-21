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

    /**
     * Switch only the center content of the main BorderPane.
     *
     * @param fxmlPath Path to the FXML file (e.g. "/fxml/policeOfficer/manageCases.fxml")
     */
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
