package com.fcms.app;

import com.fcms.database.SQLiteDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class SystemAdmin extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SQLiteDatabase.initializeDatabase();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/systemAdmin/layout.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());

        primaryStage.setTitle("CaseVault - Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
