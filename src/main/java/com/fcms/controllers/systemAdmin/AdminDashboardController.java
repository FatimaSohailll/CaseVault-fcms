package com.fcms.controllers.systemAdmin;

import com.fcms.models.users.UserAccount;
import com.fcms.repositories.UserRepository;
import com.fcms.services.WaitingListService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

public class AdminDashboardController {

    @FXML private Label totalUsersLabel;
    @FXML private VBox historyContainer;

    // NEW for waiting list sync
    @FXML private Label waitingListCountLabel;
    @FXML private VBox waitingListContainer;

    private final WaitingListService waitingListService = new WaitingListService();

    // ============================================================
    // INITIALIZE
    // ============================================================
    @FXML
    public void initialize() {
        loadUserStats();
        loadWaitingListPreview();
        loadRecentHistory();
    }

    // ============================================================
    // USER COUNT (Active only)
    // ============================================================
    private void loadUserStats() {
        int total = UserRepository.getUserCount();
        totalUsersLabel.setText(String.valueOf(total));

        int pending = waitingListService.getPendingUsers().size();
        waitingListCountLabel.setText(String.valueOf(pending));
    }

    // ============================================================
    // WAITING LIST PREVIEW (Top 3)
    // ============================================================
    private void loadWaitingListPreview() {
        waitingListContainer.getChildren().clear();

        List<UserAccount> pending = waitingListService.getPendingUsers();

        int limit = Math.min(3, pending.size());
        for (int i = 0; i < limit; i++) {
            waitingListContainer.getChildren().add(createWaitingCard(pending.get(i)));
        }
    }

    private VBox createWaitingCard(UserAccount user) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;"
                + "-fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-padding: 15;");

        HBox head = new HBox(10);

        Label name = new Label(user.getName());
        name.setStyle("-fx-font-weight: bold;");

        Label role = new Label(user.getRole());
        role.setStyle("-fx-background-color: #08276B; -fx-text-fill: white;"
                + "-fx-padding: 2 8; -fx-background-radius: 5;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button approve = new Button("✓");
        approve.setStyle("-fx-background-color: #81d05f; -fx-text-fill: white;"
                + "-fx-padding: 6 14; -fx-background-radius: 5;");

        Button reject = new Button("✗");
        reject.setStyle("-fx-background-color: #909090; -fx-text-fill: white;"
                + "-fx-padding: 6 14; -fx-background-radius: 5;");

        approve.setOnAction(e -> {
            waitingListService.approve(user);
            reloadDashboard();
        });

        reject.setOnAction(e -> {
            waitingListService.reject(user);
            reloadDashboard();
        });

        head.getChildren().addAll(name, role, spacer, approve, reject);

        VBox details = new VBox(3);
        details.getChildren().addAll(
                new Label("Email: " + user.getEmail()),
                new Label("Applied By: " + user.getUsername()),
                new Label("Requested Role: " + user.getRole()),
                new Label("Status: " + user.getManagedBy())
        );

        card.getChildren().addAll(head, details);
        return card;
    }

    private void reloadDashboard() {
        loadUserStats();
        loadWaitingListPreview();
        loadRecentHistory();
    }

    // ============================================================
    // HISTORY SECTION
    // ============================================================
    private void loadRecentHistory() {
        List<String[]> historyList = UserRepository.getRecentHistory(10);

        historyContainer.getChildren().clear();

        for (String[] h : historyList) {
            String actor = h[0];     // always System Admin
            String action = h[1];    // will contain meaningful text
            String time = h[2];

            // Extract readable action type
            String actionType;
            String targetName = "";

            if (action.startsWith("Approved pending user:")) {
                actionType = "Approved User";
                targetName = action.replace("Approved pending user:", "").trim();
            }
            else if (action.startsWith("Rejected pending user:")) {
                actionType = "Rejected User";
                targetName = action.replace("Rejected pending user:", "").trim();
            }
            else if (action.startsWith("Added new user:")) {
                actionType = "Added User";
                targetName = action.replace("Added new user:", "").trim();
            }
            else if (action.startsWith("Deleted user:")) {
                actionType = "Removed User";
                targetName = action.replace("Deleted user:", "").trim();
            }
            else {
                actionType = action; // fallback
                targetName = "";
            }

            VBox card = new VBox(5);
            card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-border-color: #e0e0e0;
            -fx-border-radius: 10;
            -fx-padding: 15;
        """);

            // USER NAME (bold)
            Label nameLabel = new Label(
                    targetName.isEmpty() ? "User" : targetName
            );
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            // ACTION TYPE
            Label actionLabel = new Label(actionType);
            actionLabel.setStyle("-fx-text-fill: #333;");

            // TIMESTAMP
            Label timeLabel = new Label(time);
            timeLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");

            card.getChildren().addAll(nameLabel, actionLabel, timeLabel);
            historyContainer.getChildren().add(card);
        }
    }
}
