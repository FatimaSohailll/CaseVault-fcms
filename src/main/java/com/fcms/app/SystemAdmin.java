package com.fcms.app;

import com.fcms.database.SQLiteDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Statement;

public class SystemAdmin extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SQLiteDatabase.initializeDatabase();
//
//        // ================================
//        // ---- DUMMY PENDING USERS ----
//        // ================================
//        try (Connection conn = com.fcms.database.SQLiteDatabase.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Pending Police
//            stmt.executeUpdate("""
//                INSERT INTO UserAccount (userID, username, email, name, password, role, managedBY)
//                VALUES ('U200', 'pending_police', 'police@test.com',
//                        'Pending Police', '123', 'Police', 'Pending')
//            """);
//            stmt.executeUpdate("""
//                INSERT INTO PoliceOfficer (officerID, rank, department)
//                VALUES ('U200', 'Inspector', 'CID')
//            """);
//
//            // Pending Court Official
//            stmt.executeUpdate("""
//                INSERT INTO UserAccount (userID, username, email, name, password, role, managedBY)
//                VALUES ('U201', 'pending_court', 'court@test.com',
//                        'Pending Court', '123', 'Court Official', 'Pending')
//            """);
//            stmt.executeUpdate("""
//                INSERT INTO CourtOfficial (officialID, courtName, designation)
//                VALUES ('U201', 'Central Court', 'Clerk')
//            """);
//
//            // Pending Forensic Expert
//            stmt.executeUpdate("""
//                INSERT INTO UserAccount (userID, username, email, name, password, role, managedBY)
//                VALUES ('U202', 'pending_expert', 'expert@test.com',
//                        'Pending Expert', '123', 'Forensic Expert', 'Pending')
//            """);
//            stmt.executeUpdate("""
//                INSERT INTO ForensicExpert (expertID, labName)
//                VALUES ('U202', 'Metro Lab')
//            """);
//
//            System.out.println("Dummy pending users added.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // ================================
//        // ---- END DUMMY INSERTS ----
//        // ================================


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
