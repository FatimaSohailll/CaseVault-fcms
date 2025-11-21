package com.fcms.controllers.systemAdmin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SystemAdminController {

    @FXML
    private AnchorPane contentArea;   // the white area on the right

    @FXML
    private VBox sidebar;

    @FXML
    private Label toggleIcon;

    private boolean isCollapsed = false;
    @FXML
    private void initialize() {
        javafx.application.Platform.runLater(() -> {
            loadPage("adminDashboard.fxml");
        });
    }


    // Helper method – loads FXML into contentArea
    private void loadPage(String fxmlFile) {
        try {
            // FIXED — MUST use absolute path starting with /fxml/
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/" + fxmlFile));
            Node node = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(node);

            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================= MENU BUTTON HANDLERS =================

    @FXML
    private void openDashboard(MouseEvent event) {
        loadPage("adminDashboard.fxml");
    }

    @FXML
    private void openManageUsers(MouseEvent event) {
        loadPage("manageUsers.fxml");
    }

    @FXML
    private void openWaitingList(MouseEvent event) {
        loadPage("waitingList.fxml");
    }

    // ================= SIDEBAR COLLAPSE LOGIC =================

    @FXML
    private void toggleSidebar() {
        if (isCollapsed) {
            sidebar.setPrefWidth(150);
            sidebar.setMaxWidth(150);

            for (var node : sidebar.getChildren()) {
                if (node instanceof HBox box && box != sidebar.getChildren().get(0)) {
                    ((Label) box.getChildren().get(0)).setVisible(true);
                }
            }

            toggleIcon.setText("<");
            isCollapsed = false;

        } else {
            sidebar.setPrefWidth(50);
            sidebar.setMaxWidth(50);

            for (var node : sidebar.getChildren()) {
                if (node instanceof HBox box && box != sidebar.getChildren().get(0)) {
                    ((Label) box.getChildren().get(0)).setVisible(false);
                }
            }

            toggleIcon.setText(">");
            isCollapsed = true;
        }
    }
}
