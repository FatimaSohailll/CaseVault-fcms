package com.fcms.controllers.policeOfficer;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class MainLayoutController {

    @FXML private BorderPane rootLayout;

    public void setCenterContent(Node content) {
        rootLayout.setCenter(content);
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void setSidebar(Parent sidebar) {
        rootLayout.setLeft(sidebar);
    }
}
