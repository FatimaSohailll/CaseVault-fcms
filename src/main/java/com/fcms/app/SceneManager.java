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

            // Set content
            contentArea.getChildren().setAll(content);

            // IMPORTANT: Force loaded screen to fill entire area
            AnchorPane.setTopAnchor(content, 0.0);
            AnchorPane.setBottomAnchor(content, 0.0);
            AnchorPane.setLeftAnchor(content, 0.0);
            AnchorPane.setRightAnchor(content, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}