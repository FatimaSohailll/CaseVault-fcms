package com.fcms.controllers.components;

import com.fcms.app.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class MasterLayoutController {

    @FXML private AnchorPane contentArea;

    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        System.out.println("Master Layout loaded. contentArea = " + contentArea);

        // now both sceneManager and contentArea should be ready
        if (sceneManager != null) {
            sceneManager.setContentArea(contentArea);
        } else {
            System.out.println("WARNING: sceneManager is null in MasterLayoutController.initialize");
        }
    }
}
