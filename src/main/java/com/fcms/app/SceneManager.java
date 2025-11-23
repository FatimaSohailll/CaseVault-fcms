package com.fcms.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SceneManager {

    private final Stage stage;
    private AnchorPane contentArea;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void setContentArea(AnchorPane area) {
        this.contentArea = area;
    }

    public void switchContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            if (contentArea == null) {
                System.out.println("ERROR: contentArea IS NULL");
                return;
            }

            contentArea.getChildren().setAll(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
