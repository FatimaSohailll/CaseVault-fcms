package com.fcms.controllers.systemAdmin;

import com.fcms.models.users.UserAccount;
import com.fcms.services.WaitingListService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class WaitingListController {

    @FXML
    private VBox userListContainer;

    @FXML
    private Label countLabel;

    private final WaitingListService service = new WaitingListService();

    @FXML
    public void initialize() {
        refreshUI();
    }

    private void refreshUI() {
        userListContainer.getChildren().clear();

        for (UserAccount user : service.getPendingUsers()) {
            userListContainer.getChildren().add(createUserCard(user));
        }

        countLabel.setText(
                "Showing " + service.getPendingUsers().size() + " pending users"
        );
    }

    private VBox createUserCard(UserAccount user) {

        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 20;");

        // HEADER
        HBox header = new HBox(10);

        Label name = new Label(user.getName());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Label role = new Label(user.getRole());
        role.setStyle("-fx-background-color: #002b7f; -fx-text-fill: white; "
                + "-fx-padding: 3 8; -fx-background-radius: 5; -fx-font-size: 12px;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button approve = new Button("✓");
        approve.setStyle("-fx-background-color: #00c853; -fx-text-fill: white; -fx-background-radius: 6;");

        Button reject = new Button("✕");
        reject.setStyle("-fx-background-color: #607d8b; -fx-text-fill: white; -fx-background-radius: 6;");

        approve.setOnAction(e -> {
            service.approve(user);
            refreshUI();
        });

        reject.setOnAction(e -> {
            service.reject(user);
            refreshUI();
        });

        header.getChildren().addAll(name, role, spacer, approve, reject);

        // DETAILS
        VBox details = new VBox(5);
        details.getChildren().addAll(
                new Label("Email: " + user.getEmail()),
                new Label("Applied By: " + user.getUsername()),
                new Label("Requested Role: " + user.getRole()),
                new Label("Status: " + user.getManagedBy())
        );

        card.getChildren().addAll(header, details);
        return card;
    }
}
