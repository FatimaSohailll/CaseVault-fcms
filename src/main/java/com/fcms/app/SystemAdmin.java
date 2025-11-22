package com.fcms.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SystemAdmin extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin/layout.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());

        primaryStage.setTitle("CaseVault - Police Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true); // Launch in full screen
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
