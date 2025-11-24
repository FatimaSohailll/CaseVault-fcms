package com.fcms.controllers.policeOfficer;

import com.fcms.models.Case;
import com.fcms.models.UserSession;
import com.fcms.services.CaseService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PoliceDashboardController {

    @FXML private BorderPane mainBorderPane;
    @FXML private VBox recentCasesContainer;

    @FXML private Label totalCasesLabel;
    @FXML private Label openCasesLabel;
    @FXML private Label pendingAnalysisLabel;
    @FXML private Label closedThisMonthLabel;

    private final CaseService caseService = new CaseService();

    @FXML
    public void initialize() {
        // Run load operations off the FX thread
        new Thread(() -> {
            loadRecentCases();
            loadStats();
        }).start();
    }

    /**
     * Load 5 most recent cases assigned to the signed-in officer by filtering getAllCases().
     * This avoids requiring a repository method for recent cases.
     */
    private void loadRecentCases() {
        Platform.runLater(() -> {
            if (recentCasesContainer != null) recentCasesContainer.getChildren().clear();
        });

        UserSession session = UserSession.getInstance();
        if (session == null || !session.isLoggedIn()) return;

        String assigned = session.getUserID() != null ? session.getUserID() : session.getUsername();

        try {
            List<Case> all = caseService.getAllCases();
            if (all == null || all.isEmpty()) return;

            List<Case> recent = all.stream()
                    .filter(c -> {
                        String a = c.getAssignedOfficer();
                        return a != null && a.equals(assigned);
                    })
                    .sorted(Comparator.comparing(Case::getDateRegistered, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            for (Case c : recent) {
                createCaseCardOnUiThread(c);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Load stats for the signed-in officer:
     * - total assigned cases
     * - open assigned cases
     * - pending analysis (from ForensicRequest counts via CaseService)
     * - closed this month (assigned cases)
     */
    private void loadStats() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isLoggedIn()) {
            updateStatsOnUiThread(0, 0, 0, 0);
            return;
        }

        String assigned = session.getUserID() != null ? session.getUserID() : session.getUsername();

        try {
            // Prefer DB-side counts if CaseService implements them; otherwise fallback to in-memory
            Integer total = null, open = null, pending = null, closedThisMonth = null;
            try {
                total = caseService.countByAssignedOfficer(assigned);
                open = caseService.countByAssignedOfficerAndStatus(assigned, "open");
                pending = caseService.countPendingAnalysisForOfficer(assigned); // uses ForensicRequestRepository join
                YearMonth now = YearMonth.now();
                closedThisMonth = caseService.countClosedThisMonthForOfficer(assigned, now.getYear(), now.getMonthValue());
            } catch (NoSuchMethodError | AbstractMethodError | Exception ignored) {
                // fall back to in-memory below
            }

            if (total != null && open != null && pending != null && closedThisMonth != null) {
                updateStatsOnUiThread(total, open, pending, closedThisMonth);
                return;
            }

            // Fallback: compute from getAllCases() and forensic request count for pending
            List<Case> all = caseService.getAllCases();
            if (all == null) {
                updateStatsOnUiThread(0, 0, 0, 0);
                return;
            }

            List<Case> assignedCases = all.stream()
                    .filter(c -> {
                        String a = c.getAssignedOfficer();
                        return a != null && a.equals(assigned);
                    })
                    .collect(Collectors.toList());

            long totalCount = assignedCases.size();
            long openCount = assignedCases.stream()
                    .filter(c -> c.getStatus() != null && c.getStatus().equalsIgnoreCase("open"))
                    .count();

            int pendingCount;
            try {
                pendingCount = caseService.countPendingAnalysisForOfficer(assigned);
            } catch (Exception e) {
                // Last-resort fallback: count cases whose status equals 'pending' (only if you use case.status)
                pendingCount = (int) assignedCases.stream()
                        .filter(c -> c.getStatus() != null && c.getStatus().equalsIgnoreCase("pending"))
                        .count();
            }

            YearMonth nowYm = YearMonth.now();
            long closedThisMonthCount = assignedCases.stream()
                    .filter(c -> c.getStatus() != null && c.getStatus().equalsIgnoreCase("closed"))
                    .filter(c -> c.getDateRegistered() != null)
                    .filter(c -> YearMonth.from(c.getDateRegistered()).equals(nowYm))
                    .count();

            updateStatsOnUiThread(totalCount, openCount, pendingCount, closedThisMonthCount);

        } catch (Exception ex) {
            ex.printStackTrace();
            updateStatsOnUiThread(0, 0, 0, 0);
        }
    }

    private void updateStatsOnUiThread(long total, long open, long pendingAnalysis, long closedThisMonth) {
        Platform.runLater(() -> {
            if (totalCasesLabel != null) totalCasesLabel.setText(String.valueOf(total));
            if (openCasesLabel != null) openCasesLabel.setText(String.valueOf(open));
            if (pendingAnalysisLabel != null) pendingAnalysisLabel.setText(String.valueOf(pendingAnalysis));
            if (closedThisMonthLabel != null) closedThisMonthLabel.setText(String.valueOf(closedThisMonth));
        });
    }

    private void createCaseCardOnUiThread(Case c) {
        Platform.runLater(() -> createCaseCard(c));
    }

    private void createCaseCard(Case c) {
        VBox card = new VBox(6);
        card.getStyleClass().add("case-card");

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label id = new Label(c.getId());
        id.getStyleClass().add("case-id");

        Label status = new Label(c.getStatus());
        status.getStyleClass().add("case-status");

        String normalizedStatus = c.getStatus() == null ? "" : c.getStatus().toLowerCase();
        switch (normalizedStatus) {
            case "open" -> status.getStyleClass().add("status-open");
            case "closed" -> status.getStyleClass().add("status-closed");
            default -> status.getStyleClass().add("status-pending");
        }

        topRow.getChildren().addAll(id, status);

        Label title = new Label(c.getTitle());
        title.getStyleClass().add("case-title");

        Label category = new Label("Category: " + c.getType());
        Label officer = new Label("Officer: " + c.getAssignedOfficer());
        Label date = new Label("Date: " + (c.getDateRegistered() != null ? c.getDateRegistered().toString() : ""));

        category.getStyleClass().add("case-meta");
        officer.getStyleClass().add("case-meta");
        date.getStyleClass().add("case-meta");

        card.getChildren().addAll(topRow, title, category, officer, date);
        card.setOnMouseClicked(e -> openCaseDetails(c.getId()));

        if (recentCasesContainer != null) recentCasesContainer.getChildren().add(card);
    }

    private void openCaseDetails(String caseId) {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isLoggedIn()) {
            showAlert("Not signed in", "Please sign in to view case details.");
            return;
        }
        System.out.println("Opening details for case: " + caseId);
        // TODO: navigate to case detail screen
    }

    public void loadRequestAnalysis() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isLoggedIn()) {
            showAlert("Not signed in", "Please sign in to access Request Analysis.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fcms/views/policeOfficer/requestAnalysis.fxml"));
            Node requestAnalysisView = loader.load();
            if (mainBorderPane != null) mainBorderPane.setCenter(requestAnalysisView);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load request analysis view: " + e.getMessage());
        }
    }

    public void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fcms/views/policeOfficer/policeDashboard.fxml"));
            Node dashboardContent = loader.load();
            if (mainBorderPane != null) mainBorderPane.setCenter(dashboardContent);
            new Thread(this::loadStats).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load dashboard view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle(title);
            a.setHeaderText(null);
            a.setContentText(content);
            a.showAndWait();
        });
    }
}
