package com.fcms.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.fcms.controllers.components.SidebarController;
import com.fcms.controllers.policeOfficer.PoliceDashboardController;
import com.fcms.database.SQLiteDatabase;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize DB
        SQLiteDatabase.initializeDatabase();

        // Create SceneManager
        SceneManager sceneManager = new SceneManager(primaryStage);

        // Prepare loader for dashboard
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/policeOfficer/policeDashboard.fxml"));

        // Controller factory: inject SceneManager into SidebarController when it's constructed
        loader.setControllerFactory(type -> {
            try {
                if (type == SidebarController.class) {
                    SidebarController sc = new SidebarController();
                    sc.setSceneManager(sceneManager);
                    return sc;
                }
                // default construction for other controllers (e.g., PoliceDashboardController)
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = loader.load();

        // Optional: get dashboard controller if you need it for other tasks
        PoliceDashboardController dashboardController = loader.getController();

        // Create scene and attach CSS
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());

        // Configure stage
        primaryStage.setTitle("CaseVault - Police Officer Dashboard");
        //primaryStage.setResizable(true);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
