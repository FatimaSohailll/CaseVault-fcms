package com.fcms.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.fcms.controllers.forensicExpert.*;
import com.fcms.database.SQLiteDatabase;
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SQLiteDatabase.initializeDatabase();

        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/policeDashboard.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/registerCase.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/closeCase.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/searchCases.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/manageParticipants.fxml"));
        //Scene scene = new Scene(root);
        //Scene scene = new Scene(root);
        // Load Expert Dashboard FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forensicExpert/expertDashboard.fxml"));
        Parent root = loader.load();

        // Get controller if you need to pass references
        ExpertDashboardController controller = loader.getController();
        //RequestAnalysisController controller = loader.getController();
        //ManageParticipantsController controller = loader.getController();
        //UploadReportController controller = loader.getController();
        //PoliceDashboardController controller = loader.getController();
        //AddEvidenceController controller = loader.getController();
        // Optionally: controller.setMainApp(this); if you want callbacks

        // Create scene and apply stylesheet
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());

        // Configure stage
        primaryStage.setTitle("CaseVault - Police Officer Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true); // Launch maximized
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
